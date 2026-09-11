package com.kian.mahmoudi.vegang.data.repository

import com.kian.mahmoudi.vegang.dto.ProfileItem
import com.kian.mahmoudi.vegang.data.config.ConfigProvider
import com.kian.mahmoudi.vegang.data.config.ConfigTester
import com.kian.mahmoudi.vegang.data.config.FetchStage
import com.kian.mahmoudi.vegang.data.config.FetchStatus
import com.kian.mahmoudi.vegang.data.config.GeoLocator
import com.kian.mahmoudi.vegang.data.database.dao.ConfigDao
import com.kian.mahmoudi.vegang.data.database.mapper.ConfigMapper
import com.kian.mahmoudi.vegang.enums.ConfigSort
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import javax.inject.Inject

class ConfigRepositoryImpl @Inject constructor(
    private val configProvider: ConfigProvider,
    private val configDao: ConfigDao,
    private val configMapper: ConfigMapper,
) :
    ConfigRepository {

    override val fetchStatus: StateFlow<FetchStatus> = configProvider.fetchStatus

    private val pingSemaphore = Semaphore(15)

    override suspend fun getConfigs(count: Int) {
        val remoteConfigs = configProvider.getConfigs(count, isDuplicate = { config ->
            configDao.configExists(
                config.server ?: "",
                config.serverPort ?: "",
                config.path
            )
        })

        configProvider.fetchStatus.update { it.copy(stage = FetchStage.GeoLocating) }

        val addresses = remoteConfigs.map { it.server ?: "" }
        val locations = GeoLocator.locateServers(addresses)
        val finalConfigs = GeoLocator.buildLocationRemarks(remoteConfigs, locations)

        val entities = configMapper.toEntityList(finalConfigs)
        configDao.upsertConfigs(entities)

        configProvider.fetchStatus.update {
            it.copy(stage = FetchStage.Done, healthy = finalConfigs.size)
        }
    }

    override fun resetFetchStatus() {
        configProvider.fetchStatus.value = FetchStatus()
    }

    override fun observeConfigs(configSort: ConfigSort): Flow<List<ProfileItem>> {
        return configDao.getAllConfigs().map { it ->
            val profiles = configMapper.toProfileList(it)
            when (configSort) {
                ConfigSort.DEFAULT -> {
                    profiles
                }

                ConfigSort.LATENCY_ASC -> {
                    profiles.sortedBy { if (it.latency == -1L) Long.MAX_VALUE else it.latency }
                }
            }
        }
    }

    override suspend fun deleteConfig(profileItem: ProfileItem) {
        val entity = configMapper.toEntity(profileItem)
        configDao.deleteConfig(entity)
    }

    override suspend fun deleteNonWorkingConfigs() {
        configDao.deleteNonWorkingConfigs()
    }

    override suspend fun testConfig(profileItem: ProfileItem) {
        val ping = pingSemaphore.withPermit {
            try {
                ConfigTester.realPing(profileItem)
            } catch (e: Exception) {
                -1L
            }
        }
        val entity = configDao.getConfigById(profileItem.id)
        if (entity != null) {
            configDao.updateConfig(entity.copy(latency = ping))
        }
    }

    override suspend fun testAllConfigs() {
        val configs = configDao.getAllConfigsSync()
        coroutineScope {
            configs.forEach { config ->
                launch {
                    pingSemaphore.withPermit {
                        val profile = configMapper.toProfileItem(config)
                        val ping = try {
                            ConfigTester.realPing(profile)
                        } catch (e: Exception) {
                            -1L
                        }
                        configDao.updateConfig(config.copy(latency = ping))
                    }
                }
            }
        }
    }

}

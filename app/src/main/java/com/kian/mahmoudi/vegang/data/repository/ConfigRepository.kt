package com.kian.mahmoudi.vegang.data.repository

import com.kian.mahmoudi.vegang.data.config.FetchStatus
import com.kian.mahmoudi.vegang.dto.ProfileItem
import com.kian.mahmoudi.vegang.enums.ConfigSort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow


interface ConfigRepository {
    val fetchStatus: StateFlow<FetchStatus>

    suspend fun getConfigs(count: Int)

    fun observeConfigs(configSort: ConfigSort): Flow<List<ProfileItem>>

    suspend fun deleteConfig(profileItem: ProfileItem)

    suspend fun deleteNonWorkingConfigs()

    suspend fun testConfig(profileItem: ProfileItem)

    suspend fun testAllConfigs()

    fun resetFetchStatus()

}
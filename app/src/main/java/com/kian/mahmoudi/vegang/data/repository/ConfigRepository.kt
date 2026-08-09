package com.kian.mahmoudi.vegang.data.repository

import com.kian.mahmoudi.vegang.dto.ProfileItem
import com.kian.mahmoudi.vegang.enums.ConfigSort
import kotlinx.coroutines.flow.Flow


interface ConfigRepository {
    suspend fun getConfigs(count: Int)

    fun observeConfigs(configSort: ConfigSort): Flow<List<ProfileItem>>

    suspend fun deleteConfig(profileItem: ProfileItem)

    suspend fun deleteNonWorkingConfigs()

    suspend fun testConfig(profileItem: ProfileItem)

    suspend fun testAllConfigs()

}
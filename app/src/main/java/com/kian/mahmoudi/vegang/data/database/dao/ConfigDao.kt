package com.kian.mahmoudi.vegang.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.kian.mahmoudi.vegang.data.database.model.ConfigModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigDao {

    @Query("SELECT * FROM configs ORDER BY addedTime DESC")
    fun getAllConfigs(): Flow<List<ConfigModel>>

    @Query("SELECT * FROM configs ORDER BY addedTime DESC")
    suspend fun getAllConfigsSync(): List<ConfigModel>

    @Query("SELECT * FROM configs WHERE id = :id")
    suspend fun getConfigById(id: Long): ConfigModel?

    @Query("SELECT * FROM configs WHERE isFavorite = 1")
    fun getFavorites(): Flow<List<ConfigModel>>

    @Query("SELECT * FROM configs WHERE configType = :type")
    fun getConfigsByType(type: String): Flow<List<ConfigModel>>

    @Upsert
    suspend fun upsertConfigs(configs: List<ConfigModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: ConfigModel): Long

    @Delete
    suspend fun deleteConfig(config: ConfigModel)

    @Query("DELETE FROM configs")
    suspend fun deleteAll()

    @Query("DELETE FROM configs WHERE latency = -1")
    suspend fun deleteNonWorkingConfigs()

    @Query("SELECT EXISTS(SELECT 1 FROM configs WHERE server = :server AND serverPort = :port AND IFNULL(path, '') = IFNULL(:path, ''))")
    suspend fun configExists(server: String, port: String, path: String?): Boolean

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateConfig(config: ConfigModel)


}
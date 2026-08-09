package com.kian.mahmoudi.vegang.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kian.mahmoudi.vegang.data.database.dao.ConfigDao
import com.kian.mahmoudi.vegang.data.database.model.ConfigModel

@Database(entities = [ConfigModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun configDao(): ConfigDao
}
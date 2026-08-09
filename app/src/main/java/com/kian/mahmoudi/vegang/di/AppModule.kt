package com.kian.mahmoudi.vegang.di

import android.content.Context
import androidx.room.Room
import com.kian.mahmoudi.vegang.data.repository.ConfigRepository
import com.kian.mahmoudi.vegang.data.repository.ConfigRepositoryImpl
import com.kian.mahmoudi.vegang.data.config.ConfigProvider
import com.kian.mahmoudi.vegang.data.database.AppDatabase
import com.kian.mahmoudi.vegang.data.database.dao.ConfigDao
import com.kian.mahmoudi.vegang.data.database.mapper.ConfigMapper
import com.kian.mahmoudi.vegang.data.repository.VpnRepository
import com.kian.mahmoudi.vegang.data.repository.VpnRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideConfigProvider() = ConfigProvider()


    @Provides
    fun provideConfigRepository(
        configProvider: ConfigProvider,
        configDao: ConfigDao,
        configMapper: ConfigMapper
    ): ConfigRepository =
        ConfigRepositoryImpl(configProvider, configDao, configMapper)


    @Provides
    fun provideVpnRepository(
        @ApplicationContext context: Context
    ): VpnRepository = VpnRepositoryImpl(context)


    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "vPNG_database"
    ).build()

    @Singleton
    @Provides
    fun provideConfigDao(db: AppDatabase) = db.configDao()

    @Singleton
    @Provides
    fun provideConfigMapper() = ConfigMapper()

}
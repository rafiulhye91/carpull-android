package com.example.carpull.di

import android.app.Application
import androidx.room.Room
import com.example.carpull.data.local.AppDao
import com.example.carpull.data.local.AppDatabase
import com.example.carpull.data.local.AppDatabase.Companion.DATABASE_NAME
import com.example.carpull.data.remote.ApiServices
import com.example.carpull.data.remote.ApiServices.Companion.BASE_URL
import com.example.carpull.data.remote.ApiServices.Companion.TIMEOUT
import com.example.carpull.repositories.CarPullRepository
import com.example.carpull.repositories.ICarPullRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiServices(
        app: Application,
    ): ApiServices {
        val client = OkHttpClient.Builder()
            .callTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiServices::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME).build()
    }

    @Provides
    @Singleton
    fun provideDaoServices(database: AppDatabase): AppDao {
        return database.getDao()
    }

    @Provides
    @Singleton
    fun provideCarPullRepository(api: ApiServices, dao: AppDao): ICarPullRepository {
        return CarPullRepository(api, dao)
    }

}
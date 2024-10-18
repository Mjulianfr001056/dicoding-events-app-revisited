package org.bangkit.dicodingevent.injection

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.hilt.work.HiltWorkerFactory
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.bangkit.dicodingevent.BuildConfig
import org.bangkit.dicodingevent.data.repository.DicodingEventDao
import org.bangkit.dicodingevent.data.repository.DicodingEventDatabase
import org.bangkit.dicodingevent.data.retrofit.DicodingEventApi
import org.bangkit.dicodingevent.settings.SettingPreferences
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.BASIC
            }
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideEventApi(retrofit: Retrofit): DicodingEventApi {
        return retrofit.create(DicodingEventApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DicodingEventDatabase {
        Log.i(TAG, "Database created!")
        return Room.databaseBuilder(
            context,
            DicodingEventDatabase::class.java,
            "dicodingevent_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideDao(database: DicodingEventDatabase) : DicodingEventDao {
        return database.dicodingEventDao
    }

    @Provides
    @Singleton
    fun provideSettingsPreferences(@ApplicationContext context: Context) : SettingPreferences {
        return SettingPreferences(context.dataStore)
    }

    companion object {
        private const val TAG = "AppModule"
    }
}
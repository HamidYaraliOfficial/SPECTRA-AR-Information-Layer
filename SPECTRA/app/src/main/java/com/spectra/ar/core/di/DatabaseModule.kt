package com.spectra.ar.core.di

import android.content.Context
import androidx.room.Room
import com.spectra.ar.core.util.SpectraConstants
import com.spectra.ar.data.database.SpectraDatabase
import com.spectra.ar.data.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        passphraseProvider: com.spectra.ar.security.DatabasePassphraseProvider
    ): SpectraDatabase {
        // The local database is encrypted at rest using SQLCipher; the passphrase itself
        // is generated once and sealed in the Android Keystore (see security/KeystoreManager).
        val factory = SupportFactory(passphraseProvider.getOrCreatePassphrase())
        return Room.databaseBuilder(context, SpectraDatabase::class.java, SpectraConstants.DATABASE_NAME)
            .openHelperFactory(factory)
            .fallbackToDestructiveMigrationOnDowngrade()
            .build()
    }

    @Provides fun provideArMarkerDao(db: SpectraDatabase): ArMarkerDao = db.arMarkerDao()
    @Provides fun provideNoteDao(db: SpectraDatabase): NoteDao = db.noteDao()
    @Provides fun provideTaskDao(db: SpectraDatabase): TaskDao = db.taskDao()
    @Provides fun providePlaceDao(db: SpectraDatabase): PlaceDao = db.placeDao()
    @Provides fun provideOpeningHoursDao(db: SpectraDatabase): OpeningHoursDao = db.openingHoursDao()
    @Provides fun provideSessionDao(db: SpectraDatabase): SessionDao = db.sessionDao()
    @Provides fun provideHistoryDao(db: SpectraDatabase): HistoryDao = db.historyDao()
    @Provides fun provideSearchDao(db: SpectraDatabase): SearchDao = db.searchDao()
}

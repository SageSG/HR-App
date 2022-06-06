package com.example.nodrama.model.Repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.nodrama.model.Entities.Credential

@Database(entities = arrayOf(Credential::class), version = 1, exportSchema = false)
abstract class CredentialDatabase : RoomDatabase() {

    abstract fun getDaoDao(): DaoCredentials

    companion object {
        /**
         * Singleton to prevent multiple instances of database opening at the same time
         */
        @Volatile
        private var INSTANCE: CredentialDatabase? = null

        fun getDatabase(context: Context): CredentialDatabase {
            /**
             * Return instance if it is NULL
             * Otherwise, create the database instance
             */
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CredentialDatabase::class.java,
                    "credsDB"
                ).build()
                INSTANCE = instance
                /**
                 * Return instance
                 */
                instance
            }
        }
    }
}
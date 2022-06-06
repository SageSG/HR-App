package com.example.nodrama.model.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.nodrama.model.Entities.Credential

class BiometricRepository(private val credFuncFromDao: DaoCredentials) {

    val secretInfo: LiveData<Credential> = credFuncFromDao.getInfo()

    /**
     * Create an Insert method to add the note into the database
     */
    suspend fun insert(credential: Credential){
        Log.d("Commit", "intodata")
        credFuncFromDao.insert(credential)
    }

    /**
     * Create a Delete method to delete note from the database
     */
    suspend fun delete(credential: Credential){
        credFuncFromDao.delete(credential)
    }

    /**
     * Create an Update method to update the note in the database
     */
    suspend fun update(credential: Credential) {
        credFuncFromDao.update(credential)
    }

    /**
     * Create an Update method to update the fingerprint data in the database
     */
    fun updateFingerprint(fingerprint: Boolean){
        credFuncFromDao.updateFingerprint(fingerprint)
    }
}
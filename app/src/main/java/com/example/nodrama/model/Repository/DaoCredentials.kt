package com.example.nodrama.model.Repository

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.nodrama.model.Entities.Credential

@Dao
interface DaoCredentials {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(credential: Credential)

    @Delete
    suspend fun delete(credential: Credential)

    @Query("Select * from credTable order by id ASC")
    fun getInfo(): LiveData<Credential>

    @Update
    suspend fun update(credential: Credential)

    @Query("Update credTable set allow_biometric = :biometric")
    fun updateFingerprint(biometric:Boolean)
}
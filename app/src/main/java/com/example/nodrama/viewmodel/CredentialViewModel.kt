package com.example.nodrama.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.nodrama.model.Entities.Credential
import com.example.nodrama.model.Repository.BiometricRepository
import com.example.nodrama.model.Repository.CredentialDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CredentialViewModel (application: Application) :AndroidViewModel(application) {

    val repository : BiometricRepository
    val allData : LiveData<Credential>
    val payslipAuthLiveData: MutableLiveData<Boolean>

    /**
     * Initialise DAO, repository and all notes
     */
    init {
        val dao = CredentialDatabase.getDatabase(application).getDaoDao()
        repository = BiometricRepository(dao)
        allData = repository.secretInfo
        payslipAuthLiveData = MutableLiveData()
    }

    /**
     * To delete a note
     * 1. Call delete method from the repository
     */
    fun deleteData (credential: Credential) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(credential)
    }

    /**
     * To update a note
     * 1. Call update method from the repository
     */
    fun updateData(credential: Credential) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(credential)
    }

    /**
     * To update a note
     * 1. Call update fingerprint method from the repository
     */
    fun updateFingerprint(fingerprint:Boolean) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateFingerprint(fingerprint)
    }


    /**
     * To add a note
     * 1. Call add method from the repository
     */
    fun addData(credential: Credential) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(credential)
    }

    /***
     * Retrieve a message after biometric authentication
     */
    fun authenticatePayslip(pass: Boolean){
        payslipAuthLiveData.postValue(pass)
    }

    /**
     * Return the message back to activity
     */
    fun getAuthenticatePayslip(): MutableLiveData<Boolean>{
        return payslipAuthLiveData
    }
}
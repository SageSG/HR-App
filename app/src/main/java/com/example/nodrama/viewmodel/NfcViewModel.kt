package com.example.nodrama.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.nodrama.model.Repository.RoomAuthenticationRepository

class NfcViewModel(application: Application): AndroidViewModel(application) {
    private val isRoomAuthorizedLiveData: MutableLiveData<Boolean?>?
    private val roomAuthRepository: RoomAuthenticationRepository?

    /**
     * Retrieve room authentication data
     */
    fun getRoomAuth(userId: String, doorId: String): MutableLiveData<Boolean?>?{
        return roomAuthRepository?.getRoomAuth(userId,doorId)
    }

    /**
     * check if authorised
     */
    fun setIsAuthenticated(authenticated: Boolean){
        isRoomAuthorizedLiveData?.postValue(authenticated)
    }

    /**
     * Initialise room repository
     */
    init {
        isRoomAuthorizedLiveData = MutableLiveData()
        roomAuthRepository = RoomAuthenticationRepository(application)
    }
}
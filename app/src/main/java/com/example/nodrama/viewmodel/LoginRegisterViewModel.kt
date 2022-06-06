package com.example.nodrama.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.nodrama.model.Repository.AuthAppRepository
import com.example.nodrama.model.Entities.User
import com.google.firebase.auth.FirebaseUser

class LoginRegisterViewModel(application: Application): AndroidViewModel(application){

    private val authAppRepository : AuthAppRepository?
    private val userLiveData: MutableLiveData<FirebaseUser?>?
    private val loggedOutLiveData: MutableLiveData<Boolean?>?
    private val userDetailsLiveData: MutableLiveData<User?>?
    private val resetSuccessfully: LiveData<Boolean>

    /***
     * login function from auth repo
     */
    fun login(email: String, password: String){
        authAppRepository?.login(email, password)
    }

    /***
     *  Register function from auth repo
     */
    fun register(email: String, password: String, user: User){
        authAppRepository?.register(email, password, user)
    }

    /***
     * update biometric login
     */
    fun updateBio (fingerPrint: Boolean, userId: String){
        // update local db
        authAppRepository?.updateBio(userId, fingerPrint)
    }

    /***
     * check login state from the repo
     */
    fun getUserLiveData(): MutableLiveData<FirebaseUser?>? {
        return userLiveData
    }

    /***
     * return whether logout is true to any views from the repo
     */
    fun getLoggedOutLiveData(): MutableLiveData<Boolean?>? {
        return loggedOutLiveData
    }

    /***
     * return user data to any views from the repo
     */
    fun getUserDetailsLiveData(): MutableLiveData<User?>? {
        return userDetailsLiveData
    }

    /***
     *  calling logout function to the repo, to sign out from firebase auth
     */
    fun logout() {
        authAppRepository?.logOut()
    }

    /***
     * reset password
     */
    fun resetPassword(email: String){
        authAppRepository?.resetPassword(email)
    }

    /***
     * return reset message
     */
    fun getResetMessage(): LiveData<Boolean>{
        return resetSuccessfully
    }

    init{
        authAppRepository = AuthAppRepository(application)
        userLiveData = authAppRepository.getUserLiveData()
        loggedOutLiveData = authAppRepository.getLoggedOutLiveData()
        userDetailsLiveData = authAppRepository.getUserDetailsLiveData()
        resetSuccessfully = authAppRepository.getResetMessage()
    }
}

/***
 * Ensure that only 1 instance of view model is created
 */
class LoginRegisterViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginRegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginRegisterViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
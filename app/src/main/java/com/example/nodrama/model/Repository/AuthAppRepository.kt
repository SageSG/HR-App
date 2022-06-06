package com.example.nodrama.model.Repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.nodrama.model.Entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class AuthAppRepository(private val application: Application) {
    private var auth: FirebaseAuth
    private var db: FirebaseDatabase
    private val userLiveData: MutableLiveData<FirebaseUser?>?
    private val loggedOutLiveData: MutableLiveData<Boolean?>?
    private val userDetailsLiveData: MutableLiveData<User?>?
    private var user: User?
    private val resetSuccessfully: MutableLiveData<Boolean>

    /***
     * To verify and login users by checking email and password in database
     */
    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(application.mainExecutor) { task ->
                if (task.isSuccessful) {
                    userLiveData?.postValue(auth.currentUser)
                    Log.d("Login:", "Verified")
                    Log.d("UserId:", auth.currentUser?.uid.toString())
                }
            }.addOnFailureListener { exception ->
            userLiveData?.postValue(null)
            Log.d("Login:", exception.toString())
        }
        Log.d("Login", "not executing function>?!")
    }

    /***
     * To register user into database
     */
    fun register(email: String, password: String, user: User) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.userId = auth.uid.toString()
                    FirebaseDatabase.getInstance("https://auth-1fced-default-rtdb.asia-southeast1.firebasedatabase.app/")
                        .getReference("Users")
                        .child(user.userId!!)
                        .setValue(user).addOnCompleteListener() { task ->
                            if (task.isSuccessful) {
                                userLiveData?.postValue(auth.currentUser)
                                Log.d("Register Activity", "User Created")
                            } else {
                                Log.d("Register Activity", "User Not Created")
                            }
                        }
                } else {
                    Toast.makeText(
                        application.getApplicationContext(),
                        "Registration Failure: " + task.exception?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(application, exception.message, Toast.LENGTH_LONG).show()
            }
    }

    /***
     * To log user out from database
     */
    fun logOut() {
        auth.signOut()
        loggedOutLiveData?.postValue(true)
        Log.d("Repo Debug", "Log Out successful")
    }

    /***
     * To check whether user is logged In, and to retrieve user details
     */
    fun getUserLiveData(): MutableLiveData<FirebaseUser?>? {
        return userLiveData
    }

    /***
     * To retrieve user details
     */
    fun getUserDetailsLiveData(): MutableLiveData<User?>? {
        if (userLiveData != null) {
            db.getReference("Users").child(auth.currentUser?.uid.toString()).get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        user?.userId = auth.currentUser?.uid.toString()
                        user?.age = it.child("age").value.toString()
                        user?.email = it.child("email").value.toString()
                        user?.phoneNum = it.child("phoneNum").value.toString()
                        user?.fullname = it.child("fullname").value.toString()
                        user?.fingerprint = it.child("fingerprint").value.toString().toBoolean()
                        userDetailsLiveData?.postValue(user)
                    }
                }
            return userDetailsLiveData
        }
        return null
    }

    /***
     * Return whether loggedout variable is true or false
     */
    fun getLoggedOutLiveData(): MutableLiveData<Boolean?>? {
        return loggedOutLiveData
    }

    /***
     * To verify and login users by checking email and password in database
     */
    fun updateBio(userId: String, fingerPrint: Boolean) {
        FirebaseDatabase.getInstance("https://auth-1fced-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Users")
            .child(userId)
            .child("fingerprint")
            .setValue(fingerPrint).addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    if(fingerPrint) Toast.makeText(application, "Biometric login has been enabled.", Toast.LENGTH_LONG).show()
                    else Toast.makeText(application, "Biometric login has been disabled.", Toast.LENGTH_LONG).show()
                    Log.d("Biometric Activity", "Fingerprint is : " + fingerPrint.toString())

                } else {
                    Log.d("Biometric Activity", "Fingerprint failed")
                    Toast.makeText(application, "Biometric Failed", Toast.LENGTH_SHORT).show()
                }
            }
        user?.fingerprint = fingerPrint
    }

    /***
     * To reset password
     */
    fun resetPassword(email: String){
        auth.sendPasswordResetEmail(email).addOnCompleteListener{
            if (it.isSuccessful){
                resetSuccessfully.postValue(true)
            } else {
                resetSuccessfully.postValue(false)
            }
        }
    }

    /***
     * To retrieve successful reset message
     */
    fun getResetMessage(): MutableLiveData<Boolean>{
        return resetSuccessfully
    }

    /**
     * Initialisation
     */
    init {
        db = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        userLiveData = MutableLiveData()
        loggedOutLiveData = MutableLiveData()
        userDetailsLiveData = MutableLiveData()
        resetSuccessfully = MutableLiveData()
        user = User()
        /***
         * Keep users logged in throughout the application
         */
        if (auth.getCurrentUser() != null) {
            userDetailsLiveData.postValue(user)
            userLiveData.postValue(auth.getCurrentUser())
            loggedOutLiveData.postValue(false)
        }
    }
}
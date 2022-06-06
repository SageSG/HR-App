package com.example.nodrama.model.Repository

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.nodrama.model.Entities.Test
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ARTRepository(private val application: Application) {
    private var db: FirebaseDatabase
    private lateinit var database: DatabaseReference
    private val artListLiveData: MutableLiveData<List<Test>?>?

    /**
     * Initialise database
     */
    init {
        db =
            FirebaseDatabase.getInstance("https://auth-1fced-default-rtdb.asia-southeast1.firebasedatabase.app/")
        artListLiveData = MutableLiveData()
    }

    /**
     * To retrieve list of ART results
     * 1. Create an empty list
     * 2. Populate the whole list
     */
    fun retrieveArtData(Id: String): MutableLiveData<List<Test>?>? {
        db.getReference("Tests").child(Id).get().addOnSuccessListener {
            if (it.exists()) {
                // Empty list
                var testList = mutableListOf<Test>()
                // populate the whole list
                for (i in it.children) {
                    val Test = i.getValue(Test::class.java)
                    if (Test != null) {
                        testList.add(Test)
                    }
                }
                artListLiveData?.postValue(testList)
            }
        }
        return artListLiveData
    }

    /**
     * To save the ART result form submission details
     * 1. Create a test object to store into the database
     * 2. Save into the database
     */
    fun saveArtTest(
        userId: String,
        fullName: String,
        phoneNumber: String,
        HRWOption: String,
        ResultOption: String,
        saveTime: String,
        imageUri: Uri
    ) {
        var testId = saveTime + "-Test1"
        database =
            FirebaseDatabase.getInstance("https://auth-1fced-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Tests")
        database.child(userId).get().addOnSuccessListener {
            if (it.exists()) {
                testId = saveTime + "-Test" + (it.childrenCount + 1).toString()
                val test =
                    Test(testId, userId, fullName, phoneNumber, HRWOption, ResultOption, saveTime)
                FirebaseDatabase.getInstance("https://auth-1fced-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("Tests")
                    .child(userId).child(testId)
                    .setValue(test).addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            Log.d("Register Activity", "Test Created")
                            uploadImageToFirebase(testId, userId, imageUri)
                        } else {
                            Log.d("Register Activity", "Test Not Created")
                        }
                    }
            } else {
                val test =
                    Test(testId, userId, fullName, phoneNumber, HRWOption, ResultOption, saveTime)
                FirebaseDatabase.getInstance("https://auth-1fced-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("Tests")
                    .child(userId).child(testId)
                    .setValue(test).addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            Log.d("Register Activity", "Test Created")
                            uploadImageToFirebase(testId, userId, imageUri)
                        } else {
                            Log.d("Register Activity", "Test Not Created")
                        }
                    }
            }
        }
    }

    /**
     * To upload the ART result image and save into the database
     */
    fun uploadImageToFirebase(filename: String, userid: String, selectedPhotoUri: Uri) {
        if (selectedPhotoUri.toString() == "") return
        val ref = FirebaseStorage.getInstance("gs://auth-1fced.appspot.com")
            .getReference("/ArtTests/$userid/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("ART Activity", "Successfully uploaded image: ${it.metadata?.path}")
            }
    }
}
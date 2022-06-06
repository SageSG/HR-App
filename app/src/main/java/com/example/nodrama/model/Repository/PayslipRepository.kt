package com.example.nodrama.model.Repository

import android.app.Application
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.nodrama.model.Entities.Payslip
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


class PayslipRepository(private val application: Application) {
    private var db: FirebaseDatabase
    private val payslipListLiveData: MutableLiveData<List<Payslip>?>?
    private val firebaseStorage: FirebaseStorage
    private val uriLiveData: MutableLiveData<Uri>

    /**
     * To retrieve all the logged in user's payslips from the database
     * 1. Create an empty list
     * 2. Populate the list
     */
    fun getAllPayslip(userId: String) : MutableLiveData<List<Payslip>?>?{
        db.getReference("Payslips").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        /**
                         * Empty list
                         */
                        var payslipList = mutableListOf<Payslip>()
                        /**
                         * Populate the list
                         */
                        for (i in snapshot.children){
                            val payslip = i.getValue(Payslip::class.java)
                            if (payslip != null) {
                                payslipList.add(payslip)
                            }
                        }
                        payslipListLiveData?.postValue(payslipList)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        return payslipListLiveData
    }

    /**
     * Retrieve URI live data
     */
    fun getUriLiveData(): MutableLiveData<Uri>{
        return uriLiveData
    }

    /**
     * Locate and return logged in user's payslip
     */
    fun returnPayslip(userId: String, payslip: Payslip){
        firebaseStorage.getReference().child("/pdf/$userId/${payslip.fileName}")
            .downloadUrl.addOnSuccessListener {
                uriLiveData.postValue(it)
            }
    }

    /**
     * Initialisation
     */
    init {
        db = FirebaseDatabase.getInstance()
        firebaseStorage = FirebaseStorage.getInstance("gs://auth-1fced.appspot.com")
        payslipListLiveData = MutableLiveData()
        uriLiveData = MutableLiveData()
    }
}
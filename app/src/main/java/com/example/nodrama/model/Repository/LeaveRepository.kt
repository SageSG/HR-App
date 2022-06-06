package com.example.nodrama.model.Repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.nodrama.model.Entities.Leave
import com.example.nodrama.model.Entities.LeaveBalances
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate

class LeaveRepository(private val application: Application) {

    private val db: FirebaseDatabase
    private val leaveBalancesLiveData: MutableLiveData<LeaveBalances>
    private val pendingLeaveLiveData: MutableLiveData<List<Leave>>
    private val pastLeaveLiveData: MutableLiveData<List<Leave>>

    /***
     * To add new leave
     * @param userId user's id
     * @param leave new leave applied
     */
    fun addNewLeave(userId: String, leave: Leave) {
        db.getReference("Leaves").child(userId).get().addOnSuccessListener {
            if (it.exists()){
                leave.leaveId= (it.childrenCount + 1).toString()
                db.getReference("Leaves").child(userId)
                    .child((it.childrenCount + 1).toString()).setValue(leave)
                Log.d("LeaveRepository", (it.childrenCount + 1).toString())
            }
            /***
             * Update new leave balance after leave has been added
             */
            db.getReference("LeaveBalances").child(userId).get()
                .addOnSuccessListener { balanceList ->
                    for (i in balanceList.children) {
                        if (i.key.equals(leave.leaveType.toString().lowercase())) {
                            val newBalance = i.value.toString().toInt() - leave.daysTaken
                            Log.d("LeaveRepository", newBalance.toString())
                            db.getReference("LeaveBalances").child(userId)
                                .child(leave.leaveType.toString().lowercase()).setValue(newBalance)
                        }
                    }
                }
        }

    }

    /***
     * Retrieve leave balances from the firebase
     * @param userId user's id
     */
    fun getUserLeaveBalances(userId: String): MutableLiveData<LeaveBalances> {
        db.getReference("LeaveBalances").child(userId).get().addOnSuccessListener {
            if (it.exists()) {
                leaveBalancesLiveData.postValue(it.getValue(LeaveBalances::class.java))
            }
        }
        return leaveBalancesLiveData
    }

    /***
     * Return pending leaves (start date after today)
     * @param userId user's id
     */
    fun getPendingLeaves(userId: String): MutableLiveData<List<Leave>> {
        db.getReference("Leaves").child(userId).get().addOnSuccessListener {
            if (it.exists()) {
                var leaveList = mutableListOf<Leave>()
                var today = LocalDate.now()
                for (i in it.children) {
                    var leave = i.getValue(Leave::class.java)
                    if (leave != null && leave.status?.equals("Pending") == true) {
                        leaveList.add(leave)
                    }
                }
                pendingLeaveLiveData.postValue(leaveList)
            }
        }
        return pendingLeaveLiveData
    }

    /***
     * Return past leaves (start date before today)
     * @param userId user's id
     */
    fun getPastLeaves(userId: String): MutableLiveData<List<Leave>> {
        db.getReference("Leaves").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var leaveList = mutableListOf<Leave>()
                    var today = LocalDate.now()
                    for (i in snapshot.children) {
                        var leave = i.getValue(Leave::class.java)
                        if (leave != null && leave.status?.equals("Pending") != true) {
                            leaveList.add(leave)
                        }
                    }
                    pastLeaveLiveData.postValue(leaveList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        return pastLeaveLiveData
    }

    /***
     * Delete leaveRequest from firebase
     * @param userId user's id
     * @param leave leave selected to be deleted
     */
    fun deleteLeave(userId: String, leave: Leave) {
        db.getReference("Leaves").child(userId).get().addOnSuccessListener { leaveList ->
            if (leaveList.exists()) {
                for (i in leaveList.children) {
                    val leaveFromDB = i.getValue(Leave::class.java)
                    if (leaveFromDB?.leaveId?.equals(leave.leaveId) == true) {
                        db.getReference("Leaves").child(userId)
                            .child(leave.leaveId.toString())
                            .child("status")
                            .setValue("Cancelled")
                        /***
                         * Update new leave balance after leave has been removed
                         */
                        db.getReference("LeaveBalances").child(userId).get()
                            .addOnSuccessListener { balanceList ->
                                for (i in balanceList.children) {
                                    if (i.key.equals(leave.leaveType.toString().lowercase())) {
                                        val newBalance = i.value.toString().toInt() + leave.daysTaken
                                        Log.d("NEWBALANCE", newBalance.toString())
                                        db.getReference("LeaveBalances").child(userId)
                                            .child(leave.leaveType.toString().lowercase()).setValue(newBalance)
                                    }
                                }
                            }
                    }
                }
            }
        }
    }

    /**
     * Initialisation
     */
    init {
        db = FirebaseDatabase.getInstance()
        leaveBalancesLiveData = MutableLiveData()
        pendingLeaveLiveData = MutableLiveData()
        pastLeaveLiveData = MutableLiveData()
    }
}
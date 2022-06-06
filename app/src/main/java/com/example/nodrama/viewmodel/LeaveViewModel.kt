package com.example.nodrama.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.nodrama.model.Entities.Leave
import com.example.nodrama.model.Entities.LeaveBalances
import com.example.nodrama.model.Repository.LeaveRepository

class LeaveViewModel(application: Application): AndroidViewModel(application){

    private val leaveRepository : LeaveRepository?

    /***
     * Retrieve leave balance from users
     */
    fun getUserLeaveBalances(userId: String) : MutableLiveData<LeaveBalances>? {
        return leaveRepository?.getUserLeaveBalances(userId)
    }

    /***
     * Retrieve pending leaves from users
     */
    fun getPendingLeaves(userId: String) : MutableLiveData<List<Leave>>? {
        return leaveRepository?.getPendingLeaves(userId)
    }

    /***
     * Retrieve past leaves from users
     */
    fun getPastLeaves(userId: String) : MutableLiveData<List<Leave>>? {
        return leaveRepository?.getPastLeaves(userId)
    }

    /***
     * Delete leave from DB
     */
    fun deleteLeave(userId: String, leave: Leave) {
        leaveRepository?.deleteLeave(userId, leave);
    }

    /***
     * add new leave to DB
     */
    fun addNewLeave(userId: String, leave: Leave){
        leaveRepository?.addNewLeave(userId, leave)
    }


    /**
     * Initialise leave repository
     */
    init{
        leaveRepository = LeaveRepository(application)
    }
}
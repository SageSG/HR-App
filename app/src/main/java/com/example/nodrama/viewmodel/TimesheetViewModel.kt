package com.example.nodrama.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.nodrama.model.Entities.Timesheet
import com.example.nodrama.model.Repository.TimesheetRepository
import com.example.nodrama.model.Entities.User

class TimesheetViewModel(application: Application): AndroidViewModel(application){

    private val timesheetRepository : TimesheetRepository?
    private val timesheetLiveData: MutableLiveData<Timesheet?>?
    private val timesheetListLiveData: MutableLiveData<List<Timesheet?>?>?


    /***
     * Add timesheet for checkin
     */
    fun addNewTimesheetForCheckIn(userId: String){
        timesheetRepository?.addNewTimesheetForCheckIn(userId)
    }

    /***
     * update Timesheet for checkout
     */
    fun addNewTimesheetForCheckOut(userId: String){
        timesheetRepository?.addNewTimesheetForCheckOut(userId)
    }

    /***
     * get timesheet live data
     */
    fun getTodayTimesheetLiveData(user: User): MutableLiveData<Timesheet?>?{
        return timesheetRepository?.getTodayTimesheetLiveData(user)
    }

    /***
     * get timesheet live data
     */
    fun getTimesheetList(user: User): MutableLiveData<List<Timesheet?>?>?{
        return timesheetRepository?.getTimesheetList(user)
    }

    init{
        timesheetRepository = TimesheetRepository(application)
        timesheetListLiveData = MutableLiveData()
        timesheetLiveData = MutableLiveData()
    }
}

/***
 * Ensure that only 1 instance of view model is created
 */
class TimesheetViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginRegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginRegisterViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
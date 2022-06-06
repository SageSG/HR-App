package com.example.nodrama.model.Repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.nodrama.model.Entities.Timesheet
import com.example.nodrama.model.Entities.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class TimesheetRepository(private val application: Application) {
    private val db: FirebaseDatabase
    private val timesheetLiveData: MutableLiveData<Timesheet?>?
    private val timesheetListLiveData: MutableLiveData<List<Timesheet?>?>?

    /***
     * Add new timesheet for checkin
     * @param userId user's Id
     *
     */
    fun addNewTimesheetForCheckIn(userId: String) {

        db.getReference("Timesheet").child(userId).get().addOnSuccessListener {
            var todayDateTime = LocalDateTime.now()
            val timesheetToDB = Timesheet()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            var clockIn = todayDateTime.format(formatter)
            if (it.exists()) {
                var todayTimesheetExist = false
                var today = LocalDate.now()
                var todayString = today.format(DateTimeFormatter.ISO_LOCAL_DATE)
                for (i in it.children) {
                    val timesheet = i.getValue(Timesheet::class.java)
                    if (timesheet?.clockIn?.contains(todayString) == true) {
                        todayTimesheetExist = true
                    }
                }
                if (!todayTimesheetExist) {
                    timesheetToDB.sheetId = (it.childrenCount + 1).toString()
                    timesheetToDB.clockIn = clockIn
                    timesheetToDB.clockOut = "You have not clocked out for today."
                    timesheetToDB.UserId = userId
                    db.getReference("Timesheet").child(userId)
                        .child(timesheetToDB.sheetId.toString())
                        .setValue(timesheetToDB)
                }
            }
            else {
                timesheetToDB.sheetId = (it.childrenCount + 1).toString()
                timesheetToDB.clockIn = clockIn
                timesheetToDB.clockOut = "You have not clocked out for today."
                timesheetToDB.UserId = userId
                db.getReference("Timesheet").child(userId)
                    .child(timesheetToDB.sheetId.toString())
                    .setValue(timesheetToDB)
            }
        }
    }

    /***
     * Add new timesheet for checkout
     * @param userId user's Id
     *
     */
    fun addNewTimesheetForCheckOut(userId: String) {
        db.getReference("Timesheet").child(userId).get().addOnSuccessListener {
            if (it.exists()) {
                var timesheet: Timesheet
                val today = LocalDateTime.now()
                val todayString = today.format(DateTimeFormatter.ISO_LOCAL_DATE)
                for (i in it.children) {
                    timesheet = i.getValue(Timesheet::class.java)!!
                    if (timesheet.clockIn?.contains(todayString) == true) {
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        var clockOut = today.format(formatter)
                        timesheet.clockOut = clockOut
                        db.getReference("Timesheet").child(userId).child(timesheet.sheetId.toString())
                            .child("clockOut").setValue(timesheet.clockOut.toString())
                    }
                }
            }
        }
    }

    /***
     * Return today's timesheet live data
     * @param userId user's Id
     */
    fun getTodayTimesheetLiveData(user: User): MutableLiveData<Timesheet?>? {
        timesheetLiveData?.postValue(null)
        db.getReference("Timesheet").child(user.userId.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("Snapshot", snapshot.toString())
                    if (snapshot.exists()) {
                        var today = LocalDate.now()
                        var todayString = today.format(DateTimeFormatter.ISO_LOCAL_DATE)
                        var count: Long = 0
                        for (i in snapshot.children) {
                            val timesheet = i.getValue(Timesheet::class.java)
                            if (timesheet?.clockIn?.contains(todayString) == true) {
                                timesheetLiveData?.postValue(timesheet)
                            } else {
                                count++
                            }
                            if (count == snapshot.childrenCount) {
                                timesheetLiveData?.postValue(null)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        return timesheetLiveData
    }

    /***
     * Retrieve all timesheet information from user
     * @param user user's information
     */
    fun getTimesheetList(user: User): MutableLiveData<List<Timesheet?>?>? {
        db.getReference("Timesheet").child(user.userId.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Empty list
                        var timesheetList = mutableListOf<Timesheet>()
                        // populate the whole list
                        for (i in snapshot.children){
                            val timesheet = i.getValue(Timesheet::class.java)
                            var timesheetDate = timesheet?.clockIn?.replaceAfter(" ", "")
                            val today = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                            if (timesheet != null && timesheetDate?.contains(today) != true) {
                                timesheetList.add(timesheet)
                            }
                        }
                        timesheetListLiveData?.postValue(timesheetList)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        return timesheetListLiveData
    }

    /**
     * Initialisation
     */
    init {
        db = FirebaseDatabase.getInstance()
        timesheetListLiveData = MutableLiveData()
        timesheetLiveData = MutableLiveData()
    }
}
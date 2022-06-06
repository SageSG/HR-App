package com.example.nodrama.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.nodrama.R
import com.example.nodrama.databinding.ActivityApplyLeaveBinding
import com.example.nodrama.model.Entities.Leave
import com.example.nodrama.viewmodel.LeaveViewModel
import com.example.nodrama.viewmodel.LoginRegisterViewModel
import java.text.SimpleDateFormat
import java.util.*


class ApplyLeaveActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApplyLeaveBinding
    var leaveTypeList: Array<String>? = null
    var selectedLeaveType: String? = null
    private var loginRegisterViewModel: LoginRegisterViewModel? = null
    private var leaveViewModel: LeaveViewModel? = null
    private val TAG = ApplyLeaveActivity::class.java.simpleName
    private var startDatePickerDialog: DatePickerDialog? = null
    private var endDatePickerDialog: DatePickerDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply_leave)

        /**
         * Initialise viewBinding for this activity
         */
        binding = ActivityApplyLeaveBinding.inflate(layoutInflater)
        setContentView(binding.root)


        leaveTypeList = resources.getStringArray(R.array.LeaveTypeList)

        /***
         * Initialize instance of a view model
         */
        loginRegisterViewModel = ViewModelProvider(this).get(
            LoginRegisterViewModel::class.java
        )

        /***
         * Initialize instance of a view model
         */
        leaveViewModel = ViewModelProvider(this).get(
            LeaveViewModel::class.java
        )

        initialiseStartDatePicker()
        initialiseEndDatePicker()
        binding.startDatePickerButton.text = getTodaysDate()
        binding.endDatePickerButton.text = getTodaysDate()

        /***
         * Population of leave type spinner
         */
        val spinnerHRW = findViewById<Spinner>(R.id.spinnerLeaveType)
        if (spinnerHRW != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, leaveTypeList!!
            )
            spinnerHRW.adapter = adapter
            spinnerHRW.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    selectedLeaveType = leaveTypeList!![position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }

        /***
         * On click listener to submit leave application and open dialog popup
         */
        binding.buttonSubmitLeave.setOnClickListener{
            submitLeave()
        }
    }

    /***
     * Save leave data to the database
     */
    private fun submitLeave(){
        val leave = Leave()
        leave.leaveId = "1"
        leave.daysTaken = getDaysTaken(
            binding.startDatePickerButton.text.toString(),
            binding.endDatePickerButton.text.toString()
        ).toString().toInt()
        leave.status = "Pending"
        leave.startDate = binding.startDatePickerButton.text.toString()
        leave.endDate = binding.endDatePickerButton.text.toString()
        leave.leaveType = selectedLeaveType.toString()
        createNewDialog(leave)
    }

    /***
     * Create a dialog
     * @param isSuccess checks whether the room is authenticated or not
     * @param roomId returns the room name to be displayed on the dialog
     */
    fun createNewDialog(leave: Leave) {
        var view =
            View.inflate(this@ApplyLeaveActivity, R.layout.popup_confirmation_leave_apply, null)
        val builder = AlertDialog.Builder(this@ApplyLeaveActivity)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        var btnConfirm = dialog.findViewById<Button>(R.id.btnConfirm)
        dialog.setCancelable(false)
        loginRegisterViewModel?.getUserDetailsLiveData()?.observe(this) { user ->
            leaveViewModel?.addNewLeave(user?.userId.toString(), leave)
            btnConfirm.setOnClickListener {
                val intent = Intent(this, MainLeaveActivity::class.java)
                startActivity(intent)
                dialog.dismiss()
            }
        }
    }

    /***
     * Calculate days between the start leave date and end leave date
     * @param start start date
     * @param end end date
     */
    @SuppressLint("SimpleDateFormat")
    private fun getDaysTaken(start: String, end: String): Long {
        val df = SimpleDateFormat("yyy-MM-dd")
        val startDate = df.parse(start)
        val endDate = df.parse(end)
        val diff: Long = endDate.time - startDate.time
        return ((((diff / 1000) / 60) / 60) / 24) + 1
    }

    /***
     * Retrieve current (today's) date
     */
    private fun getTodaysDate(): String? {
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        var month: Int = cal.get(Calendar.MONTH)
        month = month + 1
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
        return makeDateString(day, month, year)
    }

    /***
     * Create start date picker
     */
    private fun initialiseStartDatePicker() {
        val dateSetListener =
            OnDateSetListener { datePicker, year, month, day ->
                var month = month
                month = month + 1
                val date = makeDateString(day, month, year)
                binding.startDatePickerButton.text = date
                binding.endDatePickerButton.text = date
            }
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH)
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
        val style: Int = AlertDialog.THEME_HOLO_LIGHT
        startDatePickerDialog = DatePickerDialog(this, style, dateSetListener, year, month, day)
    }
    /***
     * Create end date picker
     */
    private fun initialiseEndDatePicker() {
        val dateSetListener =
            OnDateSetListener { datePicker, year, month, day ->
                var month = month
                month = month + 1
                val date = makeDateString(day, month, year)
                binding.endDatePickerButton.text = date
                val days = getDaysTaken(
                    binding.startDatePickerButton.text.toString(),
                    date.toString()
                ).toString()
                binding.textViewDaysTaken.text = "Number of days taken: ${days} days(s)"
            }
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH)
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
        val style: Int = AlertDialog.THEME_HOLO_LIGHT
        endDatePickerDialog = DatePickerDialog(this, style, dateSetListener, year, month, day)
    }
    /***
     * Format date to string
     * @param day day of the date
     * @param month month of the date
     * @param year year of the date
     */
    private fun makeDateString(day: Int, month: Int, year: Int): String? {
        var formatDay = day.toString()
        if (day < 10)
            formatDay = "0$day"
        return year.toString() + "-" + getMonthFormat(month) + "-" + formatDay
    }

    /***
     * Format month
     * @param month month of the date
     */
    private fun getMonthFormat(month: Int): String {
        if (month == 1) return "01"
        if (month == 2) return "02"
        if (month == 3) return "03"
        if (month == 4) return "04"
        if (month == 5) return "05"
        if (month == 6) return "06"
        if (month == 7) return "07"
        if (month == 8) return "08"
        if (month == 9) return "09"
        if (month == 10) return "10"
        if (month == 11) return "11"
        return if (month == 12) "12" else "01"
    }

    /***
     * Open the start date picker
     */
    fun openStartDatePicker(view: View?) {
        startDatePickerDialog!!.show()
    }

    /***
     * Open the end date picker
     */
    fun openEndDatePicker(view: View?){
        endDatePickerDialog!!.show()
    }
}
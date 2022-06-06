package com.example.nodrama.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nodrama.R
import com.example.nodrama.databinding.ActivityTimesheetBinding
import com.example.nodrama.viewmodel.LoginRegisterViewModel
import com.example.nodrama.viewmodel.TimesheetViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class TimesheetActivity : AppCompatActivity() {

    private var timesheetViewModel: TimesheetViewModel? = null
    private var loginRegisterViewModel: LoginRegisterViewModel? = null
    private val adapter = TimesheetListAdapter()
    private lateinit var binding: ActivityTimesheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * Initialise viewBinding for this activity
         */
        binding = ActivityTimesheetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /***
         * Initialize instance of a view model
         */
        timesheetViewModel = ViewModelProvider(this).get(
            TimesheetViewModel::class.java
        )

        /***
         * Initialize instance of a view model
         */
        loginRegisterViewModel = ViewModelProvider(this).get(
            LoginRegisterViewModel::class.java
        )


        var textViewClockInTime = findViewById<TextView>(R.id.textViewClockInDate)
        var textViewClockOutTime = findViewById<TextView>(R.id.textViewClockOutDate)
        var listViewTimesheet = findViewById<RecyclerView>(R.id.recyclerViewTimesheet)
        setUpRecyclerView(listViewTimesheet)

        /***
         * Pass user information into the timesheet repo to query for user's timesheet
         */
        loginRegisterViewModel?.getUserDetailsLiveData()?.observe(this) {
            if (it != null) {
                Log.d("Retrieved User", it.toString())
                timesheetViewModel?.getTodayTimesheetLiveData(it)?.observe(this) {
                    if (it != null) {
                        if (it.clockIn?.contains("you") != true)
                            textViewClockInTime.text = it.clockIn.toString().replaceBefore(" ", "")
                        else
                            textViewClockInTime.text = getString(R.string.null_clockin_message)
                        if (it.clockOut?.contains("you") != true)
                            textViewClockOutTime.text = it.clockOut.toString().replaceBefore(" ", "")
                        else
                            textViewClockOutTime.text = getString(R.string.null_clockout_message)
                    } else {
                        textViewClockInTime.text = getString(R.string.null_clockin_message)
                        textViewClockOutTime.text = getString(R.string.null_clockout_message)
                    }
                }
            }
        }

        /***
         * Bottom navigation logic
         */

        var fab = findViewById<FloatingActionButton>(R.id.navFab)
        fab.setOnClickListener {
            Log.d("Inside onclick", "message")
            val navIntent = Intent(this, NFCActivity::class.java)
            startActivity(navIntent)
        }

        /**
         * Implicitly declare type of bottomNavigationView as NavigationBarView
         */
        var bottomNavigationView = findViewById<NavigationBarView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.miHome -> {
                    val navIntent = Intent(this, MainActivity::class.java)
                    startActivity(navIntent)
                    true
                }
                R.id.miAccount -> {
                    val navIntent = Intent(this, AccountActivity::class.java)
                    startActivity(navIntent)
                    true
                }
                else -> false
            }
        }
        val currentDate: String = getTodayTimeStamp();
        binding.textViewCurrentDate.setText(currentDate)
    }

    /**
     * Set up recycler view to display the timesheet
     */
    private fun setUpRecyclerView(view: RecyclerView) {
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        view.addItemDecoration(decoration)
        view.adapter = adapter
        view.layoutManager = LinearLayoutManager(this)

        loginRegisterViewModel?.getUserDetailsLiveData()?.observe(this) {
            if (it != null) {
                timesheetViewModel?.getTimesheetList(it)?.observe(this) {
                    Log.d("TIMESHEETLIST", it.toString())
                    it.let { adapter.submitList(it) }
                }
            }
        }
    }

    /***
     * Retrieve current timestamp
     */
    private fun getTodayTimeStamp(): String {
        val saveTime = DateTimeFormatter
            .ofPattern("dd MMMM yyyy")
            .withZone(ZoneOffset.UTC)
            .format(Instant.now())
        return saveTime
    }
}
package com.example.nodrama.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.nodrama.service.AcceleratorService
import com.example.nodrama.R
import com.example.nodrama.databinding.ActivityMainBinding
import com.example.nodrama.viewmodel.LoginRegisterViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView
import java.text.DateFormat
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {
    private var loginRegisterViewModel: LoginRegisterViewModel? = null
    private lateinit var binding: ActivityMainBinding

    private var userId: String? = null

    private var acceleratorService: AcceleratorService? = null
    private var mBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
            val binder = service as AcceleratorService.LocalBinder
            acceleratorService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(className: ComponentName?) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * Initialise viewBinding for this activity
         */
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val textViewDate = findViewById<TextView>(R.id.textViewDate)
        val textViewTime = findViewById<TextView>(R.id.textViewTime)
        val calendar = Calendar.getInstance()
        val currentDate: String = getTodayTimeStamp();
        val currentTime: String =
            DateFormat.getTimeInstance(DateFormat.MEDIUM).format(calendar.time)
        textViewDate.setText(currentDate)
        textViewTime.setText(currentTime)

        /***
         * Initialize instance of a view model
         */
        loginRegisterViewModel = ViewModelProvider(this).get(
            LoginRegisterViewModel::class.java
        )

        /***
         * Link to cards
         */
        binding.cardViewTimesheet.setOnClickListener {
            val intent = Intent(this, TimesheetActivity::class.java)
            startActivity(intent)
        }

        binding.cardViewPayslips.setOnClickListener {
            val intent = Intent(this, PayslipActivity::class.java)
            startActivity(intent)
        }

        binding.cardViewART.setOnClickListener {
            val intentART = Intent(this, ArtHomeActivity::class.java)
            startActivity(intentART)
        }

        binding.cardViewLeaves.setOnClickListener {
            val intentLeave = Intent(this, MainLeaveActivity::class.java)
            startActivity(intentLeave)
        }

        /***
         * Bottom navigation logic
         */
        var fab = findViewById<FloatingActionButton>(R.id.navFab)
        fab.setOnClickListener {
            val navIntent = Intent(this, NFCActivity::class.java)
            startActivity(navIntent)
            // mini rotating animation -- fun stuff :>
            val rotate_left = AnimationUtils.loadAnimation(
                this,
                R.anim.nfc_rotateleft
            )
            fab.startAnimation(rotate_left)
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
        retrieveData()
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

    /***
     * Retrieve logged in user's information
     */
    fun retrieveData() {
        loginRegisterViewModel?.getUserDetailsLiveData()?.observe(this) {
            Log.d("Retrieved User", it.toString())
            if (it != null) {
                val textViewWelcome = findViewById<TextView>(R.id.textViewName)
                textViewWelcome.setText(it.fullname.toString())
                userId = it.userId

                val sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.apply {
                    putString("userid", userId)
                    putString("examplekey", "someRandomValue")
                    putString("pdftitle", "payslip_example.pdf")
                }.apply()
            } else {
                Toast.makeText(this, "User doesn't exist", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Callback when activity is starting
     */
    override fun onStart() {
        super.onStart()
        Intent(this, AcceleratorService::class.java).also {
            bindService(it, connection, Context.BIND_AUTO_CREATE)
        }
    }

    /**
     * Callback when activity is stopping
     */
    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false
    }

    /**
     * Callback when activity is resuming
     */
    override fun onResume() {
        super.onResume()
    }

    /**
     * Callback when activity is on pause
     */
    override fun onPause() {
        super.onPause()
    }

    /**
     * Callback when activity is finishing
     */
    override fun onDestroy() {
        super.onDestroy()
    }
}
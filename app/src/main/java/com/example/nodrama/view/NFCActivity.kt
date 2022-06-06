package com.example.nodrama.view

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.nfc.FormatException
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.nodrama.R
import com.example.nodrama.databinding.ActivityNfcBinding
import com.example.nodrama.viewmodel.LoginRegisterViewModel
import com.example.nodrama.viewmodel.NfcViewModel
import com.example.nodrama.viewmodel.TimesheetViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView
import com.skyfishjy.library.RippleBackground
import java.io.IOException
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class NFCActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNfcBinding
    val TAG = MainActivity::class.java.simpleName
    private val mEtMessage: EditText? = null
    private var mBtWrite: Button? = null
    private var mBtRead: Button? = null
    private var isWrite = false
    private var mNfcAdapter: NfcAdapter? = null
    private var timesheetViewModel: TimesheetViewModel? = null
    private var nfcViewModel: NfcViewModel? = null
    private var loginRegisterViewModel: LoginRegisterViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /***
         * Initialize viewbinding
         */
        binding = ActivityNfcBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(R.layout.activity_nfc)

        /***
         * Initialize instance of a view model
         */
        loginRegisterViewModel = ViewModelProvider(this).get(
            LoginRegisterViewModel::class.java
        )

        /***
         * Initialize instance of a view model
         */
        nfcViewModel = ViewModelProvider(this).get(
            NfcViewModel::class.java
        )

        /***
         * Initialize instance of a view model
         */
        timesheetViewModel = ViewModelProvider(this).get(
            TimesheetViewModel::class.java
        )

        /***
         * Bottom navigation logic
         */
        var fab = findViewById<FloatingActionButton>(R.id.navFab)
        fab.backgroundTintList = ColorStateList.valueOf(Color.rgb(11, 190, 174))
        fab.setImageResource(R.drawable.ic_nfc_rotate)

        fab.setOnClickListener {
            val navIntent = Intent(this, NFCActivity::class.java)
            startActivity(navIntent)
        }

        /**
         * Implicitly declare type of bottomNavigationView as NavigationBarView
         */
        var bottomNavigationView = findViewById<NavigationBarView>(R.id.bottomNavigationView)
        bottomNavigationView.setSelectedItemId(R.id.miPlaceholder)
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

        /**
         * Initialise and run NFC
         */
        initNFC()

        /**
         * NFC Animation
         */
        val rippleBackground = findViewById<View>(R.id.animation) as RippleBackground
        rippleBackground.visibility = View.VISIBLE
        rippleBackground.startRippleAnimation()
    }

    /***
     * create dialog
     * @param isSuccess checks whether the room is authenticated or not
     * @param roomId returns the room name to be displayed on the dialog
     */
    fun createNewDialog(isSuccess: Boolean, roomId: String){
        if (isSuccess)
        {
            var view = View.inflate(this@NFCActivity, R.layout.popup_success_nfc, null)
            val builder = AlertDialog.Builder(this@NFCActivity)
            builder.setView(view)
            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            var roomText = dialog.findViewById<TextView>(R.id.textViewError)
            var timestampText = dialog.findViewById<TextView>(R.id.textViewTimestamp)
            var btnPopup = dialog.findViewById<Button>(R.id.btnConfirm)
            roomText.text = roomId
            timestampText.text = getTodayTimeStamp()
            dialog.setCancelable(true)
            btnPopup.setOnClickListener {
                dialog.dismiss()
                val intent = Intent(this, TimesheetActivity::class.java)
                startActivity(intent)
                true
            }
        } else {
            var view = View.inflate(this@NFCActivity, R.layout.popup_error_nfc, null)
            val builder = AlertDialog.Builder(this@NFCActivity)
            builder.setView(view)
            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            var btnPopup = dialog.findViewById<Button>(R.id.btnConfirm)
            var roomText = dialog.findViewById<TextView>(R.id.textViewRoom)
            roomText.text = roomId
            dialog.setCancelable(false)
            btnPopup.setOnClickListener {
                dialog.dismiss()
                val intent = intent
                finish()
                startActivity(intent)
            }
        }
    }

    /**
     * Retrieve the current timestamp
     */
    private fun getTodayTimeStamp(): String {
        val saveTime = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneOffset.UTC)
            .format(Instant.now())
        return saveTime
    }

    /***
     * NFC Functionalities
     */
    private fun initNFC() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
    }

    override fun onResume() {
        super.onResume()
        val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        val ndefDetected = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        val techDetected = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        val nfcIntentFilter = arrayOf(techDetected, tagDetected, ndefDetected)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        )
        if (mNfcAdapter != null) mNfcAdapter!!.enableForegroundDispatch(
            this,
            pendingIntent,
            nfcIntentFilter,
            null
        )
    }

    override fun onPause() {
        super.onPause()
        if (mNfcAdapter != null) mNfcAdapter!!.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val tag = intent!!.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        Log.d(TAG, "onNewIntent: " + intent.action)
        if (tag != null) {
/*            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT)
                .show()*/
            val ndef = Ndef.get(tag)
            this.onNfcDetected(ndef)
        }
    }

    /**
     * Check if NFC is detected
     */
    fun onNfcDetected(ndef: Ndef?){
        if (ndef != null) {
            readFromNFC(ndef)
        }
    }

    /**
     * Read data from NFC
     */
    private fun readFromNFC(ndef: Ndef) {
        try {
            ndef.connect()
            val ndefMessage = ndef.ndefMessage
            loginRegisterViewModel?.getUserDetailsLiveData()?.observe(this) {
                if (it!=null) {
                    if (ndefMessage != null) {
                        val message = String(ndefMessage.records[0].payload)
                        if (message.trim { it <= ' ' }.length != 0) {
                            if (message.contains("%")) {
                                val itemData =
                                    message.split("\\%".toRegex()).toTypedArray()
                                Log.d(TAG, "readFromNFC: $message")
                                //check whether tag is entry or exit
                                val doorData = itemData[1].split("|")
                                if (doorData.size > 1){
                                    if (doorData[1] == "ENTRY"){
                                        nfcViewModel?.getRoomAuth(it.userId.toString(), doorData[0])?.observe(this){ isAuthenticated ->
                                            if (isAuthenticated == true){
                                                /**
                                                 * Add check-in timesheet
                                                 */
                                                timesheetViewModel?.addNewTimesheetForCheckIn(it.userId.toString())
                                                createNewDialog(true,doorData[0])
                                            } else {
                                                createNewDialog(false,doorData[0])
                                            }
                                        }
                                    } else if (doorData[1] == "EXIT"){
                                        /**
                                         * Add checkout timesheet
                                         */
                                        timesheetViewModel?.addNewTimesheetForCheckOut(it.userId.toString())
                                        createNewDialog(true, doorData[0])
                                    }
                                } else {
                                    createNewDialog(false,doorData[0])
                                }
                            } else {
                                Log.d(TAG, "readFromNFC: $message")
                            }
                        }
                    }
                }
            }
            ndef.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: FormatException) {
            e.printStackTrace()
        }
    }
}
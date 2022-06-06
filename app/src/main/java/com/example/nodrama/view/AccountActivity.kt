package com.example.nodrama.view

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.nodrama.R
import com.example.nodrama.databinding.ActivityAccountBinding
import com.example.nodrama.viewmodel.CredentialViewModel
import com.example.nodrama.viewmodel.LoginRegisterViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.textfield.TextInputLayout

class AccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountBinding
    private var loginRegisterViewModel: LoginRegisterViewModel? = null
    private var credentialViewModel: CredentialViewModel? = null
    private var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * Initialise viewBinding for this activity
         */
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setContentView(R.layout.activity_account)

        var buttonLogout = findViewById<EditText>(R.id.buttonLogout2)

        loginRegisterViewModel = ViewModelProvider(this).get(
            LoginRegisterViewModel::class.java
        )
        credentialViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(
            CredentialViewModel::class.java
        )

        /***
         *  1. Observe if user in view model is true
         *  2. If true, enable the logout button
         */
        loginRegisterViewModel?.getUserLiveData()?.observe(this) { firebaseUser ->
            if (firebaseUser != null) {
                buttonLogout.isEnabled = true
            } else {
                buttonLogout.isEnabled = false
            }
        }

        /***
         *  1. Observe the view model
         *  2. Navigate back to login activity if logout livedata is true
         */
        loginRegisterViewModel?.getLoggedOutLiveData()?.observe(this) { isLogOut ->
            if (isLogOut == true) {
                val navIntent = Intent(this, LoginActivity::class.java)
                startActivity(navIntent)
            }
        }

        /***
         * Bottom navigation logic
         */
        var fab = findViewById<FloatingActionButton>(R.id.navFab)
        fab.setOnClickListener {
            val navIntent = Intent(this, NFCActivity::class.java)
            startActivity(navIntent)
        }

        /**
         * Implicitly declare type of bottomNavigationView as NavigationBarView
         */
        var bottomNavigationView = findViewById<NavigationBarView>(R.id.bottomNavigationView)
        bottomNavigationView.setSelectedItemId(R.id.miAccount)
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
         * call retrieveAccountData function to retrieve user information
         */
        retrieveAccountData()

        /**
         * On click listener to provide access to the user via biometric fingerprint scanning
         */
        val buttonBiometric = findViewById<EditText>(R.id.buttonBiometric)
        buttonBiometric.setOnClickListener {

            @Override
            fun onClick(view: View?) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 5000) {
                    Toast.makeText(
                        applicationContext,
                        "Please wait 5s to change option",
                        Toast.LENGTH_SHORT
                    ).show()
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                //TODO: Make the userid persistent
                var fingerPrint: Boolean
                var userId: String
                loginRegisterViewModel?.getUserDetailsLiveData()?.observe(this) {
                    userId = it!!.userId.toString()
                    fingerPrint = !it.fingerprint!!
                    loginRegisterViewModel?.updateBio(fingerPrint, userId)
                    credentialViewModel?.updateFingerprint(fingerPrint)
                    credentialViewModel?.allData?.observe(this) {
                        Log.d("HENRY", it.access.toString())
                    }
                }
                /**
                 * Refresh fingerprint status
                 */
                retrieveAccountData()
            }
            onClick(it);
        }

        /***
         * Logout user with firebase sign out function
         */
        buttonLogout.setOnClickListener {
            loginRegisterViewModel?.logout()
            Toast.makeText(applicationContext, "Logged out successfully", Toast.LENGTH_SHORT)
            Log.d("LOGOUT", "button clicked")
            finish()
        }
    }

    /***
     * Retrieve user information
     */
    fun retrieveAccountData() {
        loginRegisterViewModel?.getUserDetailsLiveData()?.observe(this) {
            Log.d("Account Retrieved User", it.toString())
            if (it != null) {
                val textViewAccountName = findViewById<TextView>(R.id.textViewAccountName)
                val textViewAccountEmail = findViewById<TextView>(R.id.textViewAccountEmail)
                val textContactNum = findViewById<TextInputLayout>(R.id.textContactNum)
                val buttonBiometricLayout =
                    findViewById<TextInputLayout>(R.id.buttonBiometricLayout)

                var fingerprintData: Boolean

                fingerprintData = it.fingerprint
                if (fingerprintData) {
                    buttonBiometricLayout.setHint("Disable Biometric")
                } else {
                    buttonBiometricLayout.setHint("Enable Biometric")
                }

                textViewAccountName.setText(it.fullname.toString())

                textViewAccountEmail.setText(it.email.toString())

                textContactNum.getEditText()?.setText(it.phoneNum.toString());
            } else {
                Toast.makeText(this, "User doesn't exist", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
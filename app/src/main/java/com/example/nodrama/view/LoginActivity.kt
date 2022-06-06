package com.example.nodrama.view

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.example.nodrama.R
import com.example.nodrama.model.Entities.Credential
import com.example.nodrama.viewmodel.CredentialViewModel
import com.example.nodrama.viewmodel.LoginRegisterViewModel
import com.google.android.material.textfield.TextInputLayout
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {

    private var loginRegisterViewModel: LoginRegisterViewModel? = null
    private var credentialViewModel: CredentialViewModel? = null
    private var email: String? = null
    private var password: String? = null
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    lateinit var viewModal: CredentialViewModel
    private var mLastClickTime: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {

        /**
         * Handle the splash screen transition
         */
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /***
         * Initialize instance of a view model
         */
        loginRegisterViewModel = ViewModelProvider(this).get(
            LoginRegisterViewModel::class.java
        )

        /***
         * Initialize instance of a view model
         */
        credentialViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(
            CredentialViewModel::class.java
        )

        /***
         * 1. Observes if user from viewmodel is not null
         * 2. If not null, log in to the main page
         */
        loginRegisterViewModel?.getUserLiveData()?.observe(this) { firebaseUser ->
            if (firebaseUser != null) {
                val intentMain = Intent(this, MainActivity::class.java)
                intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intentMain)
                finish()
            } else {
                Toast.makeText(applicationContext, "Login Failed!", Toast.LENGTH_LONG).show()
            }
        }

        /***
         * Call new intent to reset password activity
         */
        val forgetPasswordText = findViewById<TextView>(R.id.textViewForgetPassword)
        forgetPasswordText.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }

        /**
         * Call enableBiometric function to enable biometric scanning
         */
        enableBiometric()

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login")
            .setSubtitle("Confirm your fingerprint to continue.")
            .setNegativeButtonText("Use password instead")
            .build()


        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val buttonRegister = findViewById<TextView>(R.id.buttonRegister)
        val button2FA = findViewById<ImageView>(R.id.button2FA)

        /***
         * Login button onclick listener
         */
        buttonLogin.setOnClickListener {
            loginAuthentication(it)
        }

        /***
         * Register button onclick listener
         */
        buttonRegister.setOnClickListener {
            val intentRegister = Intent(this, RegisterActivity::class.java)
            startActivity(intentRegister)
        }

        /***
         * 2fa button onclick listener
         */
        button2FA.setOnClickListener {

            @Override fun onClick(view: View?) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 5000) {
                    Toast.makeText(
                        applicationContext,
                        "Please wait 5s",
                        Toast.LENGTH_SHORT
                    ).show()
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                //TODO: Add 2FA Code here
                // on below line we are initialing our view modal.
                viewModal = ViewModelProvider(
                    this,
                    ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                ).get(CredentialViewModel::class.java)

                viewModal.allData?.observe(this) {
                    if (it != null) {
                        if (it.access) {
                            email = it.email
                            password = it.password
                            biometricPrompt.authenticate(promptInfo)

                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Enable biometric in user settings",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Enable biometric in user settings",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
            onClick(it)
        }
    }

    /**
     * 1. Check if user input is empty
     * 2. Retrieve and validate if the login credentials are correct
     * 3. Ensure that email is complete (@gmail.com, @hotmail.com)
     */
    private fun loginAuthentication(view: View) {
        val editTextLoginEmail = findViewById<TextInputLayout>(R.id.editTextLoginEmail)
        val editTextLoginPassword = findViewById<TextInputLayout>(R.id.editTextLoginPassword)
        val email = editTextLoginEmail.editText!!.text.toString().trim()
        val password = editTextLoginPassword.editText!!.text.toString().trim()

        if (email.isEmpty()) {
            editTextLoginEmail.setError("Email is required!")
            editTextLoginEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextLoginEmail.setError("Please provide a valid email!")
            editTextLoginEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            editTextLoginPassword.setError("Password is required!")
            editTextLoginPassword.requestFocus()
            return
        }

        if (password.length < 8) {
            editTextLoginPassword.setError("Password is more than 8 characters!")
            editTextLoginPassword.requestFocus()
            return
        }
        updateToLocalDB(false, email, password)
        loginRegisterViewModel?.login(email, password)!!

    }

    /**
     * Verify if email address is valid
     */
    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this)
            .matches()
    }

    /**
     * Update data to local database
     */
    fun updateToLocalDB(access: Boolean, email: String, password: String) {
        val credential = Credential(access, email, password)
        credentialViewModel?.addData(credential)
    }

    /**
     * Enable biometric scanning
     */
    fun enableBiometric(){
        /***
         * Biometric Authentication
         */
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT
                    )
                        .show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    loginRegisterViewModel?.login(email!!, password!!)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        applicationContext, "Authentication failed, try again",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
    }

    /**
     * Disallow users from going 'back' from login page
     */
    override fun onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
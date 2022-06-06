package com.example.nodrama.view

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nodrama.R
import com.example.nodrama.databinding.ActivityPayslipBinding
import com.example.nodrama.model.Entities.Payslip
import com.example.nodrama.viewmodel.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.textfield.TextInputLayout
import java.util.concurrent.Executor

class PayslipActivity : AppCompatActivity(), PayslipAdapter.OnClickListener {
    private lateinit var binding: ActivityPayslipBinding
    private lateinit var userId: String
    private var email: String? = null
    private var password: String? = null
    private lateinit var executor: Executor
    private var adapter: PayslipAdapter? = null
    private var payslipViewModel: PayslipViewModel? = null
    private val payslipAdapter = PayslipAdapter(this)
    lateinit var viewModal: CredentialViewModel
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var success2fa: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * Initialise viewBinding for this activity
         */
        binding = ActivityPayslipBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(R.layout.activity_payslip)

        payslipViewModel = ViewModelProvider(this).get(
            PayslipViewModel::class.java
        )

        viewModal = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(CredentialViewModel::class.java)

        val sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("userid", null)!!

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
                    viewModal.authenticatePayslip(true)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    success2fa = false
                    Toast.makeText(
                        applicationContext, "Authentication failed, try again",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login")
            .setSubtitle("Confirm your fingerprint to continue.")
            .setNegativeButtonText("Use password instead")
            .build()

        /**
         * Recycler view
         */
        generateItems(userId)

        /**
         * Bottom Navigation Logic
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
    }


    /***
     * Populates the recycler view of payslips
     */
    fun generateItems(userid: String) {
        val recyclerViewPayslip = findViewById<RecyclerView>(R.id.recyclerViewPayslip)
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerViewPayslip.addItemDecoration(decoration)
        recyclerViewPayslip.adapter = payslipAdapter
        recyclerViewPayslip.layoutManager = LinearLayoutManager(this)
        payslipViewModel?.callPayslipFromView(userid)?.observe(this) {
            it.let {
                payslipAdapter.submitList(it)
            }
        }
    }

    /***
     * Call adapter function when one of the item view is clicked
     */
    override fun onItemClick(payslip: Payslip?) {
        Log.d("PayslipActivity", payslip.toString())
        viewDialog(payslip)
    }

    /***
     * Call adapter function when an image view is clicked
     */
    override fun onDownloadClick(payslip: Payslip?) {
        Log.d("PayslipActivity", "Download Button Clicked!!!")
        downloadDialog(payslip)
    }

    /***
     * Create authentication dialog for view button
     * @param payslip returns the selected payslip item
     */
    fun viewDialog(payslip: Payslip?) {
        var view = View.inflate(this@PayslipActivity, R.layout.popup_verification_payslip, null)
        val builder = AlertDialog.Builder(this@PayslipActivity)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        var btn2FA = dialog.findViewById<ImageView>(R.id.button2FA)
        var btnManualVerify = dialog.findViewById<Button>(R.id.buttonPayslipVerify)
        var editTextPassword = dialog.findViewById<TextInputLayout>(R.id.editTextLoginPassword)
        dialog.setCancelable(true)

        btnManualVerify.setOnClickListener {
            viewModal.allData?.observe(this) {
                if (it != null) {
                    email = it.email
                    password = it.password
                    if (editTextPassword.editText?.text.toString().trim() == password) {
                        val intent = Intent(this, ViewPayslipActivity::class.java)
                        intent.putExtra("Payslip", payslip)
                        startActivity(intent)
                        dialog.dismiss()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Password Entered is Invalid!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Enable biometric in user settings", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        btn2FA.setOnClickListener {
            viewModal.allData?.observe(this) {
                if (it != null) {
                    if (it.access) {
                        biometricPrompt.authenticate(promptInfo)
                        viewModal.getAuthenticatePayslip().observe(this) { success ->
                            if (success) {
                                val intent = Intent(this, ViewPayslipActivity::class.java)
                                intent.putExtra("Payslip", payslip)
                                startActivity(intent)
                                dialog.dismiss()
                                viewModal.authenticatePayslip(false)
                            }
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Enable biometric in user settings",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Enable biometric in user settings", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    /***
     * Create authentication dialog for download button
     * @param payslip returns the selected payslip item
     */
    fun downloadDialog(payslip: Payslip?) {
        var view = View.inflate(this@PayslipActivity, R.layout.popup_verification_payslip, null)
        val builder = AlertDialog.Builder(this@PayslipActivity)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        var btn2FA = dialog.findViewById<ImageView>(R.id.button2FA)
        var btnManualVerify = dialog.findViewById<Button>(R.id.buttonPayslipVerify)
        var editTextPassword = dialog.findViewById<TextInputLayout>(R.id.editTextLoginPassword)
        dialog.setCancelable(true)

        btnManualVerify.setOnClickListener {
            viewModal.allData?.observe(this) {
                if (it != null) {
                    email = it.email
                    password = it.password
                    if (editTextPassword.editText?.text.toString().trim() == password) {
                        downloadPdf(
                            this, payslip?.fileName, ".pdf", DIRECTORY_DOWNLOADS,
                            payslip!!
                        )
                        dialog.dismiss()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Password Entered is Invalid!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Enable biometric in user settings", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        btn2FA.setOnClickListener {
            viewModal.allData?.observe(this) {
                if (it != null) {
                    if (it.access) {
                        biometricPrompt.authenticate(promptInfo)
                        viewModal.getAuthenticatePayslip().observe(this) { success ->
                            if (success) {
                                downloadPdf(
                                    this, payslip?.fileName, ".pdf", DIRECTORY_DOWNLOADS,
                                    payslip!!
                                )
                                dialog.dismiss()
                                viewModal.authenticatePayslip(false)
                            }
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Enable biometric in user settings",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Enable biometric in user settings", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    /**
     * Download payslip to device
     */
    fun downloadPdf(
        context: Context,
        fileName: String?,
        fileExtension: String,
        destinationDirectory: String?,
        payslip: Payslip
    ) {
        payslipViewModel?.returnPayslipUri(userId, payslip)
        payslipViewModel?.getUriLiveData()?.observe(this) { uri ->
            val downloadManager: DownloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request: DownloadManager.Request = DownloadManager.Request(uri)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalFilesDir(
                context,
                destinationDirectory,
                fileName.toString() + fileExtension
            )
            downloadManager.enqueue(request)
            Toast.makeText(this, "Payslip downloading...", Toast.LENGTH_SHORT).show()
        }
    }
}
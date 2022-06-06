package com.example.nodrama.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.nodrama.R
import com.example.nodrama.model.Entities.Credential
import com.example.nodrama.model.Entities.User
import com.example.nodrama.viewmodel.CredentialViewModel
import com.example.nodrama.viewmodel.LoginRegisterViewModel
import com.google.android.material.textfield.TextInputLayout


class RegisterActivity : AppCompatActivity() {

    private var loginRegisterViewModel: LoginRegisterViewModel? = null
    private var credentialViewModel: CredentialViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        /***
         * Initialize instance of a view model
         */
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
         * On click listener to register a new user into the database
         */
        val buttonRegisterSubmit = findViewById<Button>(R.id.buttonRegisterSubmit)
        buttonRegisterSubmit.setOnClickListener {
            validateForm()
        }
    }

    /***
     * Client side validation to ensure input are valid and not empty
     */
    private fun validateForm() {
        val editTextRegisterEmail = findViewById<TextInputLayout>(R.id.editTextRegisterEmail)
        val email = editTextRegisterEmail.editText!!.text.toString().trim()
        val editTextRegisterPassword = findViewById<TextInputLayout>(R.id.editTextRegisterPassword)
        val password = editTextRegisterPassword.editText!!.text.toString().trim()
        val editTextAge = findViewById<TextInputLayout>(R.id.editTextAge)
        val age = editTextAge.editText!!.text.toString().trim()
        val editTextFullName = findViewById<TextInputLayout>(R.id.editTextFullName)
        val fullname = editTextFullName.editText!!.text.toString().trim()
        val editTextPhone = findViewById<TextInputLayout>(R.id.editTextPhone)
        val phone = editTextPhone.editText!!.text.toString().trim()
        val radioBiometric = findViewById<RadioButton>(R.id.radioBiometric)

        if (fullname.isEmpty()) {
            editTextFullName.setError("Full name cannot be left blank.")
            editTextFullName.requestFocus()
            return
        }
        if (age.isEmpty()) {
            editTextAge.setError("Age cannot be left blank.")
            editTextAge.requestFocus()
            return
        }
        if (email.isEmpty()) {
            editTextRegisterEmail.setError("Email cannot be left blank.")
            editTextRegisterEmail.requestFocus()
            return
        }
        if (phone.isEmpty()) {
            editTextPhone.setError("Mobile number cannot be left blank.")
            editTextPhone.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextRegisterEmail.setError("Please provide a valid email")
            editTextRegisterEmail.requestFocus()
            return
        }
        if (password.isEmpty()) {
            editTextRegisterPassword.setError("Password cannot be left blank.!")
            editTextRegisterPassword.requestFocus()
            return
        }
        if (password.length < 8) {
            editTextRegisterPassword.setError("Password needs to be more than 8 characters.")
            editTextRegisterPassword.requestFocus()
            return
        }
        updateToLocalDB(radioBiometric.isChecked, email, password)
        registerUser(email, password, age, fullname, phone, radioBiometric.isChecked)

        Toast.makeText(applicationContext,"Account created successfully", Toast.LENGTH_LONG).show()
        val intentLogin = Intent(this, LoginActivity::class.java)
        startActivity(intentLogin)
        finish()
    }

    /***
     * Register user to the database through viewmodel
     */
    private fun registerUser(
        email: String,
        password: String,
        age: String,
        fullname: String,
        phone: String,
        fingerprint: Boolean
    ) {
        val user = User(null, fullname, email, age, phone, fingerprint)
        loginRegisterViewModel?.register(email, password, user)
    }

    /***
     * Store a local file for the credentials to login
     */
    private fun updateToLocalDB(access: Boolean, email: String, password: String) {
        val credential = Credential(access, email, password)
        Log.d("HENRY", access.toString())
        credentialViewModel?.addData(credential)
    }
}
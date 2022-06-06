package com.example.nodrama.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.nodrama.R
import com.example.nodrama.databinding.ActivityResetPasswordBinding
import com.example.nodrama.viewmodel.LoginRegisterViewModel

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding
    private var loginRegisterViewModel: LoginRegisterViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /***
         * initialize instance of a view model
         */
        loginRegisterViewModel = ViewModelProvider(this).get(
            LoginRegisterViewModel::class.java
        )

        binding.buttonResetPassword.setOnClickListener {
            if (validateEmail()){
                loginRegisterViewModel?.resetPassword(binding.editTextFPEmail.editText?.text.toString().trim())
            }
        }

        loginRegisterViewModel?.getResetMessage()?.observe(this){
            if (it){
                Toast.makeText(this, "Please check your email to reset your password", Toast.LENGTH_LONG).show()
                val intentMain = Intent(this, LoginActivity::class.java)
                startActivity(intentMain)
            } else {
                Toast.makeText(this, "Email does not exist. Please try again!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateEmail() : Boolean{
        if (binding.editTextFPEmail.editText?.text.toString().trim().isEmpty()){
            binding.editTextFPEmail.error = "Email is required!"
            binding.editTextFPEmail.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(binding.editTextFPEmail.editText?.text.toString().trim()).matches()){
            binding.editTextFPEmail.error = "Please provide a valid email!"
            binding.editTextFPEmail.requestFocus()
            return false
        }
        return true
    }
}
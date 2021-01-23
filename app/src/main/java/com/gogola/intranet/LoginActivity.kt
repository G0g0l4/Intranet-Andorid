package com.gogola.intranet

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import com.gogola.intranet.extinsions.validateEmail
import com.gogola.intranet.extinsions.validateFirstName
import com.gogola.intranet.extinsions.validateLastName
import com.gogola.intranet.extinsions.validatePassword
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();
        setContentView(R.layout.activity_login)

        fun onLoginClick() {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }

        fun validate() {
            var valid: Boolean = false;
            val email: EditText = findViewById(R.id.loginEditTextEmail)
            val emailError = findViewById<TextInputLayout>(R.id.loginEmailError)
            val password: EditText = findViewById(R.id.loginEditTextPassword)
            val passwordError: TextInputLayout = findViewById(R.id.loginPasswordError)

            if (!validateEmail(email.text.toString()).success) {
                emailError.error = validateEmail(email.text.toString()).message
            } else {
                emailError.isErrorEnabled = false;
            }
            if (!validatePassword(password.text.toString()).success) {
                passwordError.error = validatePassword(password.text.toString()).message
            } else {
                passwordError.isErrorEnabled = false;
            }
            if (validateEmail(email.text.toString()).success and
                validatePassword(password.text.toString()).success
            ) {
                valid = true
            }
        }


        val registerText = findViewById<TextView>(R.id.registerNowText)
        val registerSideBar = findViewById<ImageView>(R.id.sidebarRegister)
        val cirLoginButton: CircularProgressButton = findViewById(R.id.cirLoginButton)

        registerText.setOnClickListener() {
            onLoginClick()
        }

        registerSideBar.setOnClickListener() {
            onLoginClick();
        }

        cirLoginButton.setOnClickListener() {
            validate()
        }

    }
}
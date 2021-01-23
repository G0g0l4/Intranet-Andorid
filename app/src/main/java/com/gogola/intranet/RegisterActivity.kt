package com.gogola.intranet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import com.gogola.intranet.extinsions.validateEmail
import com.gogola.intranet.extinsions.validateFirstName
import com.gogola.intranet.extinsions.validateLastName
import com.gogola.intranet.extinsions.validatePassword
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();
        setContentView(R.layout.activity_register)

        fun onLoginClick() {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        fun validate() {
            var valid: Boolean = false;
            val firstName: EditText = findViewById(R.id.editTextFirstName)
            val firstNameError: TextInputLayout = findViewById(R.id.firstNameError)
            val lastName: EditText = findViewById(R.id.editTextLastName)
            val lastNameError: TextInputLayout = findViewById(R.id.lastNameError)
            val email: EditText = findViewById(R.id.editTextEmail)
            val emailError = findViewById<TextInputLayout>(R.id.emailError)
            val password: EditText = findViewById(R.id.editTextPassword)
            val passwordError: TextInputLayout = findViewById(R.id.passwordError)

            if (!validateEmail(email.text.toString()).success) {
                emailError.error = validateEmail(email.text.toString()).message
            } else {
                emailError.isErrorEnabled = false;
            }
            if (!validateFirstName(firstName.text.toString()).success) {
                firstNameError.error = validateFirstName(firstName.text.toString()).message
            } else {
                firstNameError.isErrorEnabled = false;
            }
            if (!validateLastName(lastName.text.toString()).success) {
                lastNameError.error = validateLastName(lastName.text.toString()).message
            } else {
                lastNameError.isErrorEnabled = false;
            }
            if (!validatePassword(password.text.toString()).success) {
                passwordError.error = validatePassword(password.text.toString()).message
            } else {
                passwordError.isErrorEnabled = false;
            }
            if (validateEmail(email.text.toString()).success and
                validateFirstName(firstName.text.toString()).success and
                validateLastName(lastName.text.toString()).success and
                validatePassword(password.text.toString()).success
            ) {
                valid = true
            }
        }

        val haveAccountText = findViewById<TextView>(R.id.haveAccount);
        val sidebarLoginIn = findViewById<ImageView>(R.id.sidebarLoginIn)
        val registerBtn = findViewById<CircularProgressButton>(R.id.cirRegisterButton)

        haveAccountText.setOnClickListener() {
            onLoginClick()
        }

        sidebarLoginIn.setOnClickListener() {
            onLoginClick()
        }

        registerBtn.setOnClickListener() {
            validate()
        }
    }
}
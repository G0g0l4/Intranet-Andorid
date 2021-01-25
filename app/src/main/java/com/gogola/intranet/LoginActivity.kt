package com.gogola.intranet

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import com.gogola.intranet.extinsions.validateEmail
import com.gogola.intranet.extinsions.validatePassword
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var email: EditText
    private lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();
        setContentView(R.layout.activity_login)
        auth = Firebase.auth

        auth.currentUser?.let {
//      redirect to posts activity
        }

        fun onLoginClick() {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }

        fun validate(): Boolean {
            email = findViewById(R.id.loginEditTextEmail)
            val emailError = findViewById<TextInputLayout>(R.id.loginEmailError)
            password = findViewById(R.id.loginEditTextPassword)
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
                return true
            }
            return false
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
            if (validate()) {
//                Progress bar

                auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            Toast.makeText(
                                baseContext, "signIn completed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }
}
package com.gogola.intranet

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.*
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
        email = findViewById(R.id.loginEditTextEmail)
        password = findViewById(R.id.loginEditTextPassword)
        val registerSideBar = findViewById<ImageView>(R.id.sidebarRegister)

        auth.currentUser?.let {
//      redirect to posts activity
        }

        fun onLoginClick() {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }

        fun validate(): Boolean {
            val emailError = findViewById<TextInputLayout>(R.id.loginEmailError)
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
        val cirLoginButton: CircularProgressButton = findViewById(R.id.cirLoginButton)

        registerText.setOnClickListener() {
            onLoginClick()
        }

        registerSideBar.setOnClickListener() {
            onLoginClick();
        }

        cirLoginButton.setOnClickListener() {
            val progressBar: RelativeLayout = findViewById(R.id.loginProgressBar)
            val loginMainContent: ScrollView = findViewById(R.id.loginMainContent)
            val view: View = findViewById(R.id.loginView)

            if (validate()) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR;
                progressBar.visibility = View.VISIBLE
                loginMainContent.visibility = View.GONE
                view.visibility = View.GONE
                registerSideBar.visibility = View.GONE

                auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            Toast.makeText(
                                baseContext, "signIn completed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            progressBar.visibility = View.GONE
                            loginMainContent.visibility = View.VISIBLE
                            view.visibility = View.VISIBLE
                            registerSideBar.visibility = View.VISIBLE

                            Toast.makeText(
                                baseContext, "Email or Password is not correct.",
                                Toast.LENGTH_SHORT
                            ).show()
                            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
                        }
                    }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("email", email.text.toString())
        outState.putString(
            "password",
            password.text.toString()
        )
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        email.setText(savedInstanceState.getString("email"))
        password.setText(savedInstanceState.getString("password"))
    }
}
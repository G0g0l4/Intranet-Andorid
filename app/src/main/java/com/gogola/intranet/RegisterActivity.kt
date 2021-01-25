package com.gogola.intranet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import com.gogola.intranet.extinsions.validateEmail
import com.gogola.intranet.extinsions.validateFirstName
import com.gogola.intranet.extinsions.validateLastName
import com.gogola.intranet.extinsions.validatePassword
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();
        setContentView(R.layout.activity_register)
        auth = Firebase.auth

        fun onLoginClick() {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        fun validate(): Boolean {
            firstName = findViewById(R.id.editTextFirstName)
            val firstNameError: TextInputLayout = findViewById(R.id.firstNameError)
            lastName = findViewById(R.id.editTextLastName)
            val lastNameError: TextInputLayout = findViewById(R.id.lastNameError)
            email = findViewById(R.id.editTextEmail)
            val emailError = findViewById<TextInputLayout>(R.id.emailError)
            password = findViewById(R.id.editTextPassword)
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
                return true
            }
            return false
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
            val progressBar: RelativeLayout = findViewById(R.id.registerProgressBar)
            val registerMainContent: ScrollView = findViewById(R.id.registerMainContent)
            val view: View = findViewById(R.id.registerView)
            val sidebarLogin: ImageView = findViewById(R.id.sidebarLoginIn)
            if (validate()) {
                progressBar.visibility = View.VISIBLE
                sidebarLogin.visibility = View.GONE
                view.visibility = View.GONE
                registerMainContent.visibility = View.GONE

                if (validate()) {
                    auth.createUserWithEmailAndPassword(
                        email.text.toString(),
                        password.text.toString()
                    )
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    baseContext, "Registration completed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                progressBar.visibility = View.GONE
                                sidebarLogin.visibility = View.VISIBLE
                                view.visibility = View.VISIBLE
                                registerMainContent.visibility = View.VISIBLE
                                Toast.makeText(
                                    baseContext, "Registration failed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }
    }
}
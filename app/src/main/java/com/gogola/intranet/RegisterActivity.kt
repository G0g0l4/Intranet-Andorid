package com.gogola.intranet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import com.gogola.intranet.extinsions.validateEmail
import com.gogola.intranet.extinsions.validateFirstName
import com.gogola.intranet.extinsions.validateLastName
import com.gogola.intranet.extinsions.validatePassword
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var sidebarLoginIn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();
        setContentView(R.layout.activity_register)
        auth = Firebase.auth
        val db = Firebase.firestore
        sidebarLoginIn = findViewById(R.id.sidebarLoginIn)

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

            if (validate()) {
                progressBar.visibility = View.VISIBLE
                sidebarLoginIn.visibility = View.GONE
                view.visibility = View.GONE
                registerMainContent.visibility = View.GONE

                if (validate()) {
                    auth.createUserWithEmailAndPassword(
                        email.text.toString(),
                        password.text.toString()
                    )
                        .addOnCompleteListener(this) { task ->

                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid
                                val userData = hashMapOf(
                                    "UID" to userId.toString(),
                                    "email" to email.text.toString(),
                                    "firstName" to firstName.text.toString(),
                                    "lastName" to lastName.text.toString()
                                )
                                db.collection("usersData")
                                    .add(userData)
                                    .addOnSuccessListener { documentReference ->
                                        Toast.makeText(
                                            baseContext, "Registration completed.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener { e ->
                                        progressBar.visibility = View.GONE
                                        sidebarLoginIn.visibility = View.VISIBLE
                                        view.visibility = View.VISIBLE
                                        registerMainContent.visibility = View.VISIBLE
                                        Toast.makeText(
                                            baseContext, "Registration failed.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                progressBar.visibility = View.GONE
                                sidebarLoginIn.visibility = View.VISIBLE
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
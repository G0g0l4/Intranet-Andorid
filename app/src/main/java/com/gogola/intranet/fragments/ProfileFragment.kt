package com.gogola.intranet.fragments

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.gogola.intranet.LoginActivity
import com.gogola.intranet.R
import com.gogola.intranet.extinsions.validateEmail
import com.gogola.intranet.extinsions.validateFirstName
import com.gogola.intranet.extinsions.validateLastName
import com.gogola.intranet.extinsions.validatePassword
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.net.PasswordAuthentication

class ProfileFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: RelativeLayout
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var saveBtn: Button
    private lateinit var updateProfileBtn: Button
    private lateinit var singOut: Button
    private lateinit var cancel: Button
    private lateinit var profileContent: LinearLayout
    private lateinit var enterPassword: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        progressBar = view.findViewById(R.id.profile_progress_bar)
        email = view.findViewById(R.id.profile_email)
        password = view.findViewById(R.id.password)
        firstName = view.findViewById(R.id.profile_first_name)
        lastName = view.findViewById(R.id.profile_last_name)
        saveBtn = view.findViewById(R.id.save_changes)
        singOut = view.findViewById(R.id.sign_out)
        profileContent = view.findViewById(R.id.profile_content)
        enterPassword = view.findViewById(R.id.enter_password)
        updateProfileBtn = view.findViewById(R.id.update)
        cancel = view.findViewById(R.id.cancel)
        startFragment(view)
    }

    override fun onResume() {
        super.onResume()
        view?.let { startFragment(it) }
    }

    private fun startFragment(view: View) {
        progressBar.visibility = View.VISIBLE
        profileContent.visibility = View.GONE
        val db = Firebase.firestore

        db.collection("users")
            .document(auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { documentSnapshot ->
                firstName.setText(documentSnapshot.getString("firstName").toString())
                lastName.setText(documentSnapshot.getString("lastName").toString())
                email.setText(documentSnapshot.getString("email").toString())
                progressBar.visibility = View.GONE
                profileContent.visibility = View.VISIBLE
            }
            .addOnFailureListener {
                Toast.makeText(
                    context, "Something went wrong, try again letter.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        singOut.setOnClickListener() {
            if (auth.currentUser != null) {
                Firebase.auth.signOut()
                val intent = Intent(
                    activity,
                    LoginActivity::class.java
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                activity?.finish()
            } else {
                Toast.makeText(
                    context, "Please log in first to perform the task.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        fun validate(): Boolean {
            if (!validateEmail(email.text.toString()).success) {
                Toast.makeText(
                    context, validateEmail(email.text.toString()).message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (!validateFirstName(firstName.text.toString()).success) {
                Toast.makeText(
                    context, validateFirstName(firstName.text.toString()).message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (!validateLastName(lastName.text.toString()).success) {
                Toast.makeText(
                    context, validateLastName(lastName.text.toString()).message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (validateEmail(email.text.toString()).success and
                validateFirstName(firstName.text.toString()).success and
                validateLastName(lastName.text.toString()).success
            ) {
                return true
            }
            return false
        }

        fun validPassword(): Boolean {
            if (!validatePassword(password.text.toString()).success) {
                Toast.makeText(
                    context, validatePassword(password.text.toString()).message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (validatePassword(password.text.toString()).success) {
                return true
            }
            return false
        }


        saveBtn.setOnClickListener() {
            if (auth.currentUser != null) {
                if (validate()) {
                    profileContent.visibility = View.GONE
                    enterPassword.visibility = View.VISIBLE
                }
            } else {
                Toast.makeText(
                    context, "Please log in first to perform the task.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        cancel.setOnClickListener() {
            profileContent.visibility = View.VISIBLE
            enterPassword.visibility = View.GONE
        }

        updateProfileBtn.setOnClickListener() {
            if (validPassword()) {
                progressBar.visibility = View.VISIBLE
                enterPassword.visibility = View.GONE
                ActivityInfo.SCREEN_ORIENTATION_NOSENSOR
                val user = FirebaseAuth.getInstance().currentUser
                val credential = EmailAuthProvider
                    .getCredential(
                        user?.email.toString(),
                        password.text.toString()
                    )
                user!!.reauthenticate(credential)
                    .addOnSuccessListener {
                        FirebaseAuth.getInstance().currentUser!!.updateEmail(email.text.toString())
                            .addOnSuccessListener {
                                val userId = auth.currentUser!!.uid
                                val userData = hashMapOf(
                                    "UID" to userId,
                                    "email" to email.text.toString(),
                                    "firstName" to firstName.text.toString(),
                                    "lastName" to lastName.text.toString()
                                )
                                val profileData = db.collection("users").document(userId)
                                profileData.update(userData as Map<String, Any>)
                                    .addOnSuccessListener {
                                        progressBar.visibility = View.GONE
                                        enterPassword.visibility = View.GONE
                                        profileContent.visibility = View.VISIBLE
                                        Toast.makeText(
                                            context, "Your profile updated successfully",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        ActivityInfo.SCREEN_ORIENTATION_SENSOR;
                                    }
                            }
                            .addOnFailureListener {
                                progressBar.visibility = View.GONE
                                enterPassword.visibility = View.VISIBLE
                                Toast.makeText(
                                    context, "Something went wrong, try again letter.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                ActivityInfo.SCREEN_ORIENTATION_SENSOR;
                            }
                    }
                    .addOnFailureListener {
                        progressBar.visibility = View.GONE
                        enterPassword.visibility = View.VISIBLE
                        Toast.makeText(
                            context, "Password is incorrect.",
                            Toast.LENGTH_SHORT
                        ).show()
                        ActivityInfo.SCREEN_ORIENTATION_SENSOR;
                    }
            }
        }
    }
}
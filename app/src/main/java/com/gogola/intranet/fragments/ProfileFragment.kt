package com.gogola.intranet.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.gogola.intranet.LoginActivity
import com.gogola.intranet.R
import com.gogola.intranet.extinsions.*
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlin.properties.Delegates

class ProfileFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: RelativeLayout
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var saveBtn: Button
    private lateinit var updateProfileBtn: Button
    private lateinit var singOut: ImageView
    private lateinit var cancel: Button
    private lateinit var updatePassword: Button
    private lateinit var profileContent: LinearLayout
    private lateinit var enterPassword: LinearLayout
    private lateinit var updatePasswordView: LinearLayout
    private lateinit var currentPassword: EditText
    private lateinit var newPassword: EditText
    private lateinit var repeatPassword: EditText
    private lateinit var saveNewPasswordBtn: Button
    private lateinit var cancelNewPasswordBtn: Button
    private lateinit var profileImage: ImageView
    private lateinit var imageUri: Uri
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var imageProgress: RelativeLayout
    private var uploadImageIdentical by Delegates.notNull<Boolean>()

    private lateinit var generalError: String

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
        updatePassword = view.findViewById(R.id.update_password)
        cancel = view.findViewById(R.id.cancel)
        updatePasswordView = view.findViewById(R.id.change_password)
        currentPassword = view.findViewById(R.id.current_password)
        newPassword = view.findViewById(R.id.new_password)
        repeatPassword = view.findViewById(R.id.repeat_password)
        saveNewPasswordBtn = view.findViewById(R.id.update_password_btn)
        cancelNewPasswordBtn = view.findViewById(R.id.cancel_update_password)
        profileImage = view.findViewById(R.id.profile_image)
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        imageProgress = view.findViewById(R.id.image_progress_bar)
        uploadImageIdentical = false
        generalError = "Something went wrong, try again letter."

        startFragment(view)
    }

    override fun onResume() {
        super.onResume()
        view?.let { startFragment(it) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && data.data != null && requestCode == 1 && resultCode == RESULT_OK) {
            imageUri = data.data!!
            profileImage.setImageURI(imageUri)
            uploadPicture()
        }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 1)
    }

    private fun showImage() {
        storageReference.child("images/${auth.currentUser?.uid.toString()}").downloadUrl
            .addOnSuccessListener {
                Picasso.get().load(it).into(profileImage)
                imageProgress.visibility = View.GONE
                singOut.visibility = View.VISIBLE
                profileImage.visibility = View.VISIBLE
            }
            .addOnFailureListener {
                imageProgress.visibility = View.GONE
                singOut.visibility = View.VISIBLE
                profileImage.visibility = View.VISIBLE
            }
    }

    private fun uploadPicture() {
        activity?.let { blockOrientation(it) }
        imageProgress.visibility = View.VISIBLE
        singOut.visibility = View.GONE
        profileImage.visibility = View.GONE
        uploadImageIdentical = true
        val riversRef: StorageReference =
            storageReference.child("images/${auth.currentUser?.uid.toString()}")
        riversRef.putFile(imageUri)
            .addOnSuccessListener {
                context?.let { it1 -> showMessage("Image Uploaded", it1) }
                showImage()
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
            .addOnFailureListener() {
                uploadImageIdentical = false
                imageProgress.visibility = View.GONE
                singOut.visibility = View.VISIBLE
                profileImage.visibility = View.VISIBLE
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                context?.let { it1 -> showMessage("Something Went wrong, try again letter.", it1) }
            }
    }

    private fun startFragment(view: View) {
        progressBar.visibility = View.VISIBLE
        profileContent.visibility = View.GONE
        imageProgress.visibility = View.VISIBLE
        singOut.visibility = View.GONE
        profileImage.visibility = View.GONE
        val db = Firebase.firestore

        if (!uploadImageIdentical) {
            showImage()
        }

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
                context?.let { it1 -> showMessage(generalError, it1) }
            }

        profileImage.setOnClickListener() {
            chooseImage()
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
                context?.let { it1 -> showMessage("Please log in first to perform the task.", it1) }

            }
        }

        fun validate(): Boolean {
            if (!validateEmail(email.text.toString()).success) {
                context?.let { it1 ->
                    showMessage(
                        validateEmail(email.text.toString()).message,
                        it1
                    )
                }

            }
            if (!validateFirstName(firstName.text.toString()).success) {
                context?.let { it1 ->
                    showMessage(
                        validateFirstName(firstName.text.toString()).message,
                        it1
                    )
                }

            }
            if (!validateLastName(lastName.text.toString()).success) {
                context?.let { it1 ->
                    showMessage(
                        validateLastName(lastName.text.toString()).message,
                        it1
                    )
                }

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
                context?.let { it1 ->
                    showMessage(
                        validatePassword(password.text.toString()).message,
                        it1
                    )
                }

            }
            if (validatePassword(password.text.toString()).success) {
                return true
            }
            return false
        }

        fun validateUpdatingPassword(): Boolean {
            if (!validatePassword(currentPassword.text.toString()).success) {
                context?.let { it1 ->
                    showMessage(
                        validatePassword(
                            currentPassword.text.toString(),
                            "Current password"
                        ).message, it1
                    )
                }

            }
            if (!validatePassword(newPassword.text.toString()).success) {
                context?.let { it1 ->
                    showMessage(
                        validatePassword(
                            newPassword.text.toString(),
                            "New password"
                        ).message, it1
                    )
                }

            }
            if (!validatePasswordMatch(
                    newPassword.text.toString(),
                    repeatPassword.text.toString()
                ).success
            ) {
                context?.let { it1 ->
                    showMessage(
                        validatePasswordMatch(
                            newPassword.text.toString(),
                            repeatPassword.text.toString()
                        ).message,
                        it1
                    )
                }
            }

            if (validatePassword(currentPassword.text.toString()).success and
                validatePassword(newPassword.text.toString()).success and
                validatePasswordMatch(
                    newPassword.text.toString(),
                    repeatPassword.text.toString()
                ).success
            ) {
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
                context?.let { it1 ->
                    showMessage(
                        "Please log in first to perform the task.",
                        it1
                    )
                }
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
                activity?.let { blockOrientation(it) }
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
                                        context?.let { it1 ->
                                            showMessage(
                                                "Your profile updated successfully",
                                                it1
                                            )
                                        }
                                        activity?.requestedOrientation =
                                            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                                    }
                            }
                            .addOnFailureListener {
                                progressBar.visibility = View.GONE
                                enterPassword.visibility = View.VISIBLE
                                context?.let { it1 ->
                                    showMessage(
                                        generalError,
                                        it1
                                    )
                                }
                                activity?.requestedOrientation =
                                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                            }
                    }
                    .addOnFailureListener {
                        progressBar.visibility = View.GONE
                        enterPassword.visibility = View.VISIBLE
                        context?.let { it1 ->
                            showMessage(
                                "Password is incorrect.",
                                it1
                            )
                        }
                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    }
            }
        }

        updatePassword.setOnClickListener() {
            profileContent.visibility = View.GONE
            updatePasswordView.visibility = View.VISIBLE
        }

        cancelNewPasswordBtn.setOnClickListener() {
            profileContent.visibility = View.VISIBLE
            updatePasswordView.visibility = View.GONE
        }

        saveNewPasswordBtn.setOnClickListener() {
            if (validateUpdatingPassword()) {
                progressBar.visibility = View.VISIBLE
                updatePasswordView.visibility = View.GONE
                activity?.let { blockOrientation(it) }
                val user = FirebaseAuth.getInstance().currentUser
                val credential = EmailAuthProvider
                    .getCredential(
                        user?.email.toString(),
                        currentPassword.text.toString()
                    )
                user!!.reauthenticate(credential)
                    .addOnSuccessListener {
                        FirebaseAuth.getInstance().currentUser!!.updatePassword(newPassword.text.toString())
                            .addOnSuccessListener {
                                progressBar.visibility = View.GONE
                                updatePasswordView.visibility = View.GONE
                                profileContent.visibility = View.VISIBLE
                                context?.let { it1 ->
                                    showMessage(
                                        "Your password updated successfully",
                                        it1
                                    )
                                }
                                activity?.requestedOrientation =
                                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                            }
                            .addOnFailureListener {
                                progressBar.visibility = View.GONE
                                updatePasswordView.visibility = View.VISIBLE
                                context?.let { it1 ->
                                    showMessage(
                                        generalError,
                                        it1
                                    )
                                }
                                activity?.requestedOrientation =
                                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                            }
                    }
                    .addOnFailureListener {
                        progressBar.visibility = View.GONE
                        updatePasswordView.visibility = View.VISIBLE
                        context?.let { it1 ->
                            showMessage(
                                "Current password is incorrect.",
                                it1
                            )
                        }
                        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    }
            }
        }
    }
}
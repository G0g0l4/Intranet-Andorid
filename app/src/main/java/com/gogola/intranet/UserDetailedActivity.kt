package com.gogola.intranet

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.util.MutableBoolean
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.gogola.intranet.classes.User
import com.gogola.intranet.extinsions.blockOrientation
import com.gogola.intranet.extinsions.setFragment
import com.gogola.intranet.extinsions.showMessage
import com.gogola.intranet.fragments.DetailedUserFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class UserDetailedActivity : AppCompatActivity() {
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_user_detailed)
        user = intent.getSerializableExtra("user") as User
        setFragment(R.id.fragment_detailed_user, DetailedUserFragment().apply {
            arguments = Bundle().apply {
                putSerializable("user", user)
            }
        })
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        val generalError: String = "Something went wrong, try again letter."
        val addFriendBtn: Button = findViewById(R.id.add_friend_btn)
        val deleteFromFriendsBtn: Button = findViewById(R.id.delete_from_friends)
        val mainContent: LinearLayout = findViewById(R.id.detailed_main_content)
        val loader: RelativeLayout = findViewById(R.id.add_friend_progress_bar)
        val alreadyFriendsText: TextView = findViewById(R.id.already_friends)
        val userImgLoader: RelativeLayout = findViewById(R.id.user_img_progress_bar)
        val userImage: ImageView = findViewById(R.id.user_profile_img)
        val storageReference = FirebaseStorage.getInstance().reference

        val db = Firebase.firestore
        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        mainContent.visibility = View.GONE
        userImage.visibility = View.GONE
        loader.visibility = View.VISIBLE
        userImgLoader.visibility = View.VISIBLE

        storageReference.child("images/${user.UID}").downloadUrl
            .addOnSuccessListener {
                Picasso.get().load(it).into(userImage)
                userImage.visibility = View.VISIBLE
                userImgLoader.visibility = View.GONE
            }
            .addOnFailureListener {
                userImage.visibility = View.VISIBLE
                userImgLoader.visibility = View.GONE
            }

        db.collection("friends")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val firstUser = document.getString("first_user").toString()
                    val secondUser = document.getString("second_user").toString()
                    if ((firstUser == userId &&
                                secondUser == user.UID) ||
                        (secondUser == userId &&
                                firstUser == user.UID)
                    ) {
                        addFriendBtn.visibility = View.GONE
                        deleteFromFriendsBtn.visibility = View.VISIBLE
                        alreadyFriendsText.visibility = View.VISIBLE
                    }
                }
                mainContent.visibility = View.VISIBLE
                loader.visibility = View.GONE
            }
            .addOnFailureListener {
                showMessage(generalError, this)
            }

        addFriendBtn.setOnClickListener() {
            blockOrientation(this)
            val friends = hashMapOf(
                "first_user" to userId,
                "second_user" to user.UID,
                "created_at" to FieldValue.serverTimestamp()
            )
            mainContent.visibility = View.GONE
            loader.visibility = View.VISIBLE

            db.collection("friends")
                .add(friends)
                .addOnSuccessListener {
                    mainContent.visibility = View.VISIBLE
                    deleteFromFriendsBtn.visibility = View.VISIBLE
                    loader.visibility = View.GONE
                    addFriendBtn.visibility = View.GONE
                    alreadyFriendsText.visibility = View.VISIBLE
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    showMessage("You and ${user.firstName} are now friends.", this)
                }
                .addOnFailureListener {
                    showMessage(generalError, this)
                    mainContent.visibility = View.VISIBLE
                    loader.visibility = View.GONE
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
        }

        deleteFromFriendsBtn.setOnClickListener() {
            mainContent.visibility = View.GONE
            loader.visibility = View.VISIBLE
            db.collection("friends")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val firstUser = document.getString("first_user").toString()
                        val secondUser = document.getString("second_user").toString()
                        if ((firstUser == userId &&
                                    secondUser == user.UID) ||
                            (secondUser == userId &&
                                    firstUser == user.UID)
                        ) {
                            document.reference.delete()
                        }
                    }
                    mainContent.visibility = View.VISIBLE
                    addFriendBtn.visibility = View.VISIBLE
                    loader.visibility = View.GONE
                    alreadyFriendsText.visibility = View.GONE
                    deleteFromFriendsBtn.visibility = View.GONE
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    showMessage("You deleted user from friends successfully", this)
                }
                .addOnFailureListener {
                    showMessage(generalError, this)
                    mainContent.visibility = View.VISIBLE
                    loader.visibility = View.GONE
                    addFriendBtn.visibility = View.GONE
                    alreadyFriendsText.visibility = View.VISIBLE
                    deleteFromFriendsBtn.visibility = View.VISIBLE
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
        }
    }

}
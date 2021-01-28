package com.gogola.intranet

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.util.MutableBoolean
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.gogola.intranet.classes.User
import com.gogola.intranet.extinsions.setFragment
import com.gogola.intranet.fragments.DetailedUserFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserDetailedActivity : AppCompatActivity() {
    private lateinit var addFriendBtn: Button
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();
        setContentView(R.layout.activity_user_detailed)
        user = intent.getSerializableExtra("user") as User
        setFragment(R.id.fragment_detailed_user, DetailedUserFragment().apply {
            arguments = Bundle().apply {
                putSerializable("user", user)
            }
        })
    }

    private fun checkFriends(): Boolean {
        val db = Firebase.firestore
        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        var state: Boolean = false
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
                        state = true
//                        TODO state don't reassigns
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    this, "Something went wrong, try again letter.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        return state
    }


    override fun onResumeFragments() {
        super.onResumeFragments()
        addFriendBtn = findViewById(R.id.add_friend_btn)
        val alreadyFriendsText: TextView = findViewById(R.id.already_friends)

        fun hideOrShow() {
            if (checkFriends()) {
                addFriendBtn.visibility = View.GONE
                alreadyFriendsText.visibility = View.VISIBLE
            } else {
                addFriendBtn.visibility = View.VISIBLE
                alreadyFriendsText.visibility = View.GONE
            }

        }

        hideOrShow()

        addFriendBtn.setOnClickListener() {
            val db = Firebase.firestore
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR;
            val friends = hashMapOf(
                "first_user" to userId.toString(),
                "second_user" to user.UID,
                "created_at" to FieldValue.serverTimestamp()
            )
            val mainContent: LinearLayout = findViewById(R.id.detailed_main_content)
            val loader: RelativeLayout = findViewById(R.id.add_friend_progress_bar)
            mainContent.visibility = View.GONE
            loader.visibility = View.VISIBLE

            db.collection("friends")
                .add(friends)
                .addOnSuccessListener {
                    mainContent.visibility = View.VISIBLE
                    loader.visibility = View.GONE
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
                    Toast.makeText(
                        baseContext, "You and ${user.firstName} are now friends.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        baseContext, "Something went wrong, try again letter.",
                        Toast.LENGTH_SHORT
                    ).show()
                    mainContent.visibility = View.VISIBLE
                    loader.visibility = View.GONE
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
                }

            hideOrShow()
        }
    }

}
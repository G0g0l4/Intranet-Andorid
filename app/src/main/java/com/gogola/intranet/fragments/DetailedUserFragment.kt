package com.gogola.intranet.fragments

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.gogola.intranet.R
import com.gogola.intranet.classes.User
import com.gogola.intranet.extinsions.blockOrientation
import com.gogola.intranet.extinsions.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class DetailedUserFragment : Fragment() {
    private lateinit var fullName: TextView
    private lateinit var email: TextView
    private lateinit var user: User
    private lateinit var addFriendBtn: Button
    private lateinit var deleteFromFriendsBtn: Button
    private lateinit var mainContent: LinearLayout
    private lateinit var loader: RelativeLayout
    private lateinit var alreadyFriendsText: TextView
    private lateinit var userImgLoader: RelativeLayout
    private lateinit var userImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_detailed_user, container, false)
    }

    override fun onResume() {
        super.onResume()
        view?.let { startFragment(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fullName = view.findViewById(R.id.full_name)
        email = view.findViewById(R.id.user_email)
        addFriendBtn = view.findViewById(R.id.add_friend_btn)
        deleteFromFriendsBtn = view.findViewById(R.id.delete_from_friends)
        mainContent = view.findViewById(R.id.detailed_main_content)
        loader = view.findViewById(R.id.add_friend_progress_bar)
        userImgLoader = view.findViewById(R.id.user_img_progress_bar)
        alreadyFriendsText = view.findViewById(R.id.already_friends)
        userImage = view.findViewById(R.id.user_profile_img)
        user = arguments?.getSerializable("user") as User
        startFragment(view)
    }

    private fun startFragment(view: View) {
        fullName.text = user.firstName + " " + user.lastName
        email.text = user.email
        val generalError: String = "Something went wrong, try again letter."
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
                activity?.let { it1 -> showMessage(generalError, it1) }
            }

        addFriendBtn.setOnClickListener() {
            activity?.let { it1 -> blockOrientation(it1) }
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
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    activity?.let { it1 ->
                        showMessage(
                            "You and ${user.firstName} are now friends.",
                            it1
                        )
                    }
                }
                .addOnFailureListener {
                    activity?.let { it1 -> showMessage(generalError, it1) }
                    mainContent.visibility = View.VISIBLE
                    loader.visibility = View.GONE
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
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
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    activity?.let { it1 ->
                        showMessage(
                            "You deleted user from friends successfully",
                            it1
                        )
                    }
                }
                .addOnFailureListener {
                    activity?.let { it1 -> showMessage(generalError, it1) }
                    mainContent.visibility = View.VISIBLE
                    loader.visibility = View.GONE
                    addFriendBtn.visibility = View.GONE
                    alreadyFriendsText.visibility = View.VISIBLE
                    deleteFromFriendsBtn.visibility = View.VISIBLE
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
        }
    }
}
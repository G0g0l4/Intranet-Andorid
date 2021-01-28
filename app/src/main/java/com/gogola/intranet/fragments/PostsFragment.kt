package com.gogola.intranet.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gogola.intranet.CreatePostActivity
import com.gogola.intranet.R
import com.gogola.intranet.adapters.PostsAdapter
import com.gogola.intranet.adapters.SearchUsersAdapter
import com.gogola.intranet.classes.Friends
import com.gogola.intranet.classes.Post
import com.gogola.intranet.classes.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PostsFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var defaultView: RelativeLayout
    private lateinit var postField: RecyclerView
    private lateinit var progressBar: RelativeLayout
    private lateinit var newPostButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        defaultView = view.findViewById(R.id.default_view_posts)
        postField = view.findViewById(R.id.posts)
        progressBar = view.findViewById(R.id.postsProgressBar)
        newPostButton = view.findViewById(R.id.newPostButton)
        startFragment(view)
    }

    override fun onResume() {
        super.onResume()
        view?.let { startFragment(it) }
    }

    private fun startFragment(view: View) {
        defaultView.visibility = View.GONE
        postField.visibility = View.GONE
        newPostButton.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        val db = Firebase.firestore
        val users = ArrayList<User>()
        val allFriends = ArrayList<Friends>()
        val allPosts = ArrayList<Post>()
        var currentUserName = ""

//      fetch friends
        db.collection("friends")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val firstUser = document.getString("first_user").toString()
                    val secondUser = document.getString("second_user").toString()
                    val user = Friends(
                        firstUser,
                        secondUser,
                    )
                    allFriends.add(user)
                }

//              fetch users

                db.collection("users")
                    .get()
                    .addOnSuccessListener { allUser ->
                        for (user in allUser) {
                            val userId = user.getString("UID").toString()
                            for (friend in allFriends) {
                                if ((friend.firstUser == userId &&
                                            friend.secondUser == auth.currentUser?.uid.toString()) ||
                                    (friend.firstUser == auth.currentUser?.uid.toString() &&
                                            friend.secondUser == userId)
                                ) {
                                    val userData = User(
                                        userId,
                                        user.getString("firstName").toString(),
                                        user.getString("lastName").toString(),
                                        user.getString("email").toString(),
                                    )
                                    users.add(userData)
                                }
                            }
                            if (userId == auth.currentUser?.uid.toString()) {
                                currentUserName =
                                    user.getString("firstName")
                                        .toString() + " " + user.getString("lastName").toString()
                            }
                        }

//                  fetch posts
                        db.collection("posts")
                            .get()
                            .addOnSuccessListener { posts ->
                                for (post in posts) {
                                    val postOwner = post.getString("UID").toString()
                                    for (friend in users) {
                                        if (friend.UID == postOwner) {
                                            val postData = Post(
                                                friend.firstName + " " + friend.lastName,
                                                post.getString("text").toString(),
                                                post.getString("created_at").toString(),
                                            )
                                            allPosts.add(postData)
                                        }
                                    }
                                    if (postOwner == auth.currentUser?.uid) {
                                        val postData = Post(
                                            currentUserName,
                                            post.getString("text").toString(),
                                            post.getString("created_at").toString(),
                                        )
                                        allPosts.add(postData)
                                    }
                                }
                                if (allPosts.size > 0) {
                                    postField.adapter = PostsAdapter(allPosts)
                                    postField.layoutManager =
                                        LinearLayoutManager(
                                            context,
                                            LinearLayoutManager.VERTICAL,
                                            false
                                        )
                                    (postField.adapter as PostsAdapter).notifyDataSetChanged()

                                    newPostButton.visibility = View.VISIBLE
                                    postField.visibility = View.VISIBLE
                                    progressBar.visibility = View.GONE
                                    defaultView.visibility = View.GONE
                                } else {
                                    newPostButton.visibility = View.VISIBLE
                                    progressBar.visibility = View.GONE
                                    postField.visibility = View.GONE
                                    defaultView.visibility = View.VISIBLE
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context, "Something went wrong, try again letter.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }


                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context, "Something went wrong, try again letter.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

            }
            .addOnFailureListener {
                Toast.makeText(
                    context, "Something went wrong, try again letter.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        newPostButton.setOnClickListener() {
            startActivity(Intent(context, CreatePostActivity::class.java))
        }
    }
}

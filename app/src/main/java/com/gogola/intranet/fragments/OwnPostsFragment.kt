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
import com.gogola.intranet.classes.Post
import com.gogola.intranet.extinsions.showMessage
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OwnPostsFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: RelativeLayout
    private lateinit var defaultView: RelativeLayout
    private lateinit var postField: RecyclerView
    private lateinit var newPostButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_own_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        defaultView = view.findViewById(R.id.default_view_own_posts)
        postField = view.findViewById(R.id.own_posts)
        newPostButton = view.findViewById(R.id.newPostButton)
        progressBar = view.findViewById(R.id.own_posts_progress)
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
        val myPosts = ArrayList<Post>()
        var currentUserName = ""

        db.collection("users").document(auth.currentUser?.uid.toString()).get()
            .addOnSuccessListener {
                currentUserName = it.getString("firstName") + " " + it.getString("lastName")

                db.collection("posts")
                    .get()
                    .addOnSuccessListener { posts ->
                        for (post in posts) {
                            val postOwner = post.getString("UID").toString()
                            if (postOwner == auth.currentUser?.uid) {
                                val postData = Post(
                                    post.reference.id,
                                    post.getString("UID").toString(),
                                    currentUserName,
                                    post.getString("text").toString(),
                                    post.getString("created_at").toString(),
                                )
                                myPosts.add(postData)
                            }
                        }
                        if (myPosts.size > 0) {
                            postField.adapter = context?.let { PostsAdapter(myPosts, it) }
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
            }
            .addOnFailureListener {
                context?.let { it1 -> showMessage("Something went wrong, try again letter.", it1) }
            }

        newPostButton.setOnClickListener() {
            startActivity(Intent(context, CreatePostActivity::class.java))
        }
    }
}
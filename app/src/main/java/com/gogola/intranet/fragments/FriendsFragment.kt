package com.gogola.intranet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gogola.intranet.R
import com.gogola.intranet.adapters.SearchUsersAdapter
import com.gogola.intranet.classes.Friends
import com.gogola.intranet.classes.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FriendsFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var defaultView: RelativeLayout
    private lateinit var usersView: RecyclerView
    private lateinit var progressBar: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        defaultView = view.findViewById(R.id.default_view_friends)
        usersView = view.findViewById(R.id.friends)
        progressBar = view.findViewById(R.id.friendsProgressBar)
        startFragment(view)
    }

    override fun onResume() {
        super.onResume()
        view?.let { startFragment(it) }
    }

    private fun startFragment(view: View) {
        defaultView.visibility = View.GONE
        usersView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        val field: RecyclerView = view.findViewById(R.id.friends)

        val db = Firebase.firestore
        val users = ArrayList<User>()
        val allFriends = ArrayList<Friends>()

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

//            fetch users
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

                        }
                        if (users.size > 0) {
                            usersView.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                            defaultView.visibility = View.GONE
                            field.adapter = SearchUsersAdapter(users)
                            field.layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                            (field.adapter as SearchUsersAdapter).notifyDataSetChanged()
                        } else {
                            progressBar.visibility = View.GONE
                            usersView.visibility = View.GONE
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
}
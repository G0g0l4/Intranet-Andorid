package com.gogola.intranet.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gogola.intranet.R
import com.gogola.intranet.adapters.SearchUsersAdapter
import com.gogola.intranet.classes.User
import com.gogola.intranet.extinsions.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserSearchFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var defaultView: RelativeLayout
    private lateinit var usersView: RecyclerView
    private lateinit var searchBar: RelativeLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        defaultView = view.findViewById(R.id.default_view)
        usersView = view.findViewById(R.id.users)
        searchBar = view.findViewById(R.id.searchProgressBar)
        startFragment(view)
    }

    private fun search(characters: String, field: RecyclerView) {
        defaultView.visibility = View.GONE
        usersView.visibility = View.GONE
        searchBar.visibility = View.VISIBLE
        val db = Firebase.firestore
        val users = ArrayList<User>()
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val firstName = document.getString("firstName").toString()
                    val lastName = document.getString("lastName").toString()
                    val userId = document.getString("UID").toString()
                    if ((firstName == characters ||
                                lastName == characters ||
                                "$firstName $lastName" == characters) &&
                        userId != auth.currentUser?.uid.toString()
                    ) {
                        val user = User(
                            userId,
                            firstName,
                            lastName,
                            document.getString("email").toString(),
                        )
                        users.add(user)
                    }
                }
                if (users.size > 0) {
                    usersView.visibility = View.VISIBLE
                    searchBar.visibility = View.GONE
                    defaultView.visibility = View.GONE
                    field.adapter = SearchUsersAdapter(users)
                    field.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    (field.adapter as SearchUsersAdapter).notifyDataSetChanged()
                } else {
                    searchBar.visibility = View.GONE
                    usersView.visibility = View.GONE
                    defaultView.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener {
                context?.let { it1 -> showMessage("Something went wrong, try again letter.", it1) }
            }
    }

    private fun startFragment(view: View) {
        val search: EditText = view.findViewById(R.id.search)
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                search(p0.toString(), view.findViewById(R.id.users))
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}

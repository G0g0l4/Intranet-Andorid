package com.gogola.intranet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.gogola.intranet.R
import com.gogola.intranet.classes.User

class DetailedUserFragment : Fragment() {
    private lateinit var fullName: TextView
    private lateinit var email: TextView
    private lateinit var user: User
    private lateinit var addFriendBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_detailed_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fullName = view.findViewById(R.id.full_name)
        email = view.findViewById(R.id.user_email)
        addFriendBtn = view.findViewById(R.id.add_friend_btn)
        user = arguments?.getSerializable("user") as User
        startFragment(view)
    }

    private fun startFragment(view: View) {
        fullName.text = user.firstName + " " + user.lastName
        email.text = user.email

        addFriendBtn.setOnClickListener() {

        }
    }
}
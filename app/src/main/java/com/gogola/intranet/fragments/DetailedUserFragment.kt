package com.gogola.intranet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.gogola.intranet.R
import com.gogola.intranet.classes.User

class DetailedUserFragment : Fragment() {
    private lateinit var fullName: TextView
    private lateinit var email: TextView
    private lateinit var user: User

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
        user = arguments?.getSerializable("user") as User
        startFragment(view)
    }

    private fun startFragment(view: View) {
        fullName.text = user.firstName + " " + user.lastName
        email.text = user.email
    }
}
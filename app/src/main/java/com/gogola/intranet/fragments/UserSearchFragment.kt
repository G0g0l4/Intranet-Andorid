package com.gogola.intranet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gogola.intranet.R
import com.gogola.intranet.adapters.SearchUsersAdapter
import com.gogola.intranet.classes.User

class UserSearchFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startFragment(view)
    }

    private fun startFragment(view: View) {
        val usersField: RecyclerView = view.findViewById(R.id.users)
        val users = listOf<User>(
            User(
                "123",
                "Levan",
                "Gogoladze",
            ), User(
                "123",
                "Levan",
                "Gogoladze",
            ), User(
                "123",
                "Levan",
                "Gogoladze",
            ), User(
                "123",
                "Levan",
                "Gogoladze",
            ), User(
                "123",
                "Levan",
                "Gogoladze",
            ), User(
                "123",
                "Levan",
                "Gogoladze",
            ), User(
                "123",
                "Levan",
                "Gogoladze",
            ), User(
                "123",
                "Levan",
                "Gogoladze",
            ), User(
                "123",
                "Levan",
                "Gogoladze",
            ), User(
                "123",
                "Levan",
                "Gogoladze",
            ), User(
                "123",
                "Levan",
                "Gogoladze",
            )
        )
        usersField.adapter = SearchUsersAdapter(users)
        usersField.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        (usersField.adapter as SearchUsersAdapter).notifyDataSetChanged()
    }
}

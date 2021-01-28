package com.gogola.intranet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gogola.intranet.classes.User
import com.gogola.intranet.extinsions.setFragment
import com.gogola.intranet.fragments.DetailedUserFragment

class UserDetailedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();
        setContentView(R.layout.activity_user_detailed)
        val user: User = intent.getSerializableExtra("user") as User
        setFragment(R.id.fragment_detailed_user, DetailedUserFragment().apply {
            arguments = Bundle().apply {
                putSerializable("user", user)
            }
        })
    }
}
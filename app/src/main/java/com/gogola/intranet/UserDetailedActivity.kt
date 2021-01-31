package com.gogola.intranet

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.util.MutableBoolean
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.gogola.intranet.classes.User
import com.gogola.intranet.extinsions.blockOrientation
import com.gogola.intranet.extinsions.setFragment
import com.gogola.intranet.extinsions.showMessage
import com.gogola.intranet.fragments.DetailedUserFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class UserDetailedActivity : AppCompatActivity() {
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_user_detailed)
        user = intent.getSerializableExtra("user") as User
        setFragment(R.id.fragment_detailed_user, DetailedUserFragment().apply {
            arguments = Bundle().apply {
                putSerializable("user", user)
            }
        })
    }
}
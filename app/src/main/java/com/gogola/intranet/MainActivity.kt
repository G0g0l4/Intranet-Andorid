package com.gogola.intranet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.gogola.intranet.adapters.PageAdapter
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();
        setContentView(R.layout.activity_main)
        auth = Firebase.auth
        val db = Firebase.firestore

        val pager: ViewPager = findViewById(R.id.pager)
        val navBar: TabLayout = findViewById(R.id.nav_bar)
        val adapter = PageAdapter(supportFragmentManager)
        pager.adapter = adapter
        navBar.setupWithViewPager(pager)
        for (i in 0 until navBar.tabCount) {
            navBar.getTabAt(i)?.setIcon(R.drawable.ic_baseline_home_24)
        }
    }
}
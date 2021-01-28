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

        val pager: ViewPager = findViewById(R.id.pager)
        val navBar: TabLayout = findViewById(R.id.nav_bar)
        val adapter = PageAdapter(supportFragmentManager)
        pager.adapter = adapter
        navBar.setupWithViewPager(pager)
        navBar.getTabAt(0)?.setIcon(R.drawable.ic_baseline_home_24)
        navBar.getTabAt(1)?.setIcon(R.drawable.ic_search)
    }
}
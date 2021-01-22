package com.gogola.intranet

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();
        setContentView(R.layout.activity_login)

        fun onLoginClick() {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }

        val registerText = findViewById<TextView>(R.id.registerNowText)
        val registerSideBar = findViewById<ImageView>(R.id.sidebarRegister)

        registerText.setOnClickListener() {
            onLoginClick()
        }

        registerSideBar.setOnClickListener() {
            onLoginClick();
        }

    }
}
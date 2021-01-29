package com.gogola.intranet.extinsions

import android.content.Context
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

fun AppCompatActivity.setFragment(@IdRes fragmentContainerId: Int, fragment: Fragment) {
    val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
    transaction.add(fragmentContainerId, fragment)
    transaction.commit()
}

fun showMessage(message: String, context: Context) {
    Toast.makeText(
        context, message,
        Toast.LENGTH_SHORT
    ).show()
}
package com.gogola.intranet

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import com.gogola.intranet.extinsions.setFragment
import com.gogola.intranet.extinsions.validateFreePostText
import com.gogola.intranet.fragments.CreatePostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreatePostActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var valid: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();
        setContentView(R.layout.activity_create_post)

        if (savedInstanceState != null) {
            supportFragmentManager.getFragment(savedInstanceState, "fragment")?.let {
                setFragment(
                    R.id.create_post_fragment,
                    it
                )
            }
        } else {
            setFragment(R.id.create_post_fragment, CreatePostFragment())
        }

        auth = Firebase.auth
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        supportFragmentManager.putFragment(outState, "fragment", CreatePostFragment())
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        val doneBtn: FloatingActionButton = findViewById(R.id.createPostDone)
        val db = Firebase.firestore
        doneBtn.setOnClickListener() {
            val postText: EditText = findViewById(R.id.post_text)
            if (validateFreePostText(postText.text.toString()).success) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR;
                val userId = auth.currentUser?.uid
                val postData = hashMapOf(
                    "UID" to userId.toString(),
                    "text" to postText.text.toString(),
                    "created_at" to FieldValue.serverTimestamp()
                )
                val mainContent: FragmentContainerView = findViewById(R.id.create_post_fragment)
                val loader: RelativeLayout = findViewById(R.id.createPostLoader)
                mainContent.visibility = View.GONE
                loader.visibility = View.VISIBLE

                db.collection("posts")
                    .add(postData)
                    .addOnSuccessListener { documentReference ->
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                        Toast.makeText(
                            baseContext, "New post added successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                        loader.visibility = View.GONE
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            baseContext, "Adding new post failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        mainContent.visibility = View.VISIBLE
                        loader.visibility = View.GONE
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
                    }
            } else {
                Toast.makeText(
                    baseContext, validateFreePostText(postText.text.toString()).message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

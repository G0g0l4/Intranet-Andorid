package com.gogola.intranet

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import com.gogola.intranet.extinsions.setFragment
import com.gogola.intranet.extinsions.showMessage
import com.gogola.intranet.extinsions.validateFreePostText
import com.gogola.intranet.fragments.CreatePostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
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

    private fun blockOrientation() {
        requestedOrientation =
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            } else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        val doneBtn: FloatingActionButton = findViewById(R.id.createPostDone)
        val db = Firebase.firestore
        doneBtn.setOnClickListener() {
            val postText: EditText = findViewById(R.id.post_text)
            if (validateFreePostText(postText.text.toString()).success) {
                blockOrientation()
                val userId = auth.currentUser?.uid
                val timestamp = System.currentTimeMillis() / 1000
                val postData = hashMapOf(
                    "UID" to userId.toString(),
                    "text" to postText.text.toString(),
                    "created_at" to timestamp.toString()
                )
                val mainContent: FragmentContainerView = findViewById(R.id.create_post_fragment)
                val loader: RelativeLayout = findViewById(R.id.createPostLoader)
                mainContent.visibility = View.GONE
                loader.visibility = View.VISIBLE

                db.collection("posts")
                    .add(postData)
                    .addOnSuccessListener {
                        loader.visibility = View.GONE
                        showMessage("New post added successfully.", baseContext)
                        finish()
                    }
                    .addOnFailureListener {
                        showMessage("Adding new post failed.", baseContext)
                        mainContent.visibility = View.VISIBLE
                        loader.visibility = View.GONE
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
                    }
            } else {
                showMessage(validateFreePostText(postText.text.toString()).message, baseContext)
            }
        }
    }
}

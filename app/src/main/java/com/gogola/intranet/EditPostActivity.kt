package com.gogola.intranet

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.gogola.intranet.classes.Post
import com.gogola.intranet.extinsions.setFragment
import com.gogola.intranet.fragments.CreatePostFragment
import com.gogola.intranet.fragments.EditPostFragment

class EditPostActivity : AppCompatActivity() {
    private lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_edit_post)
        post = intent.getSerializableExtra("post") as Post

        if (savedInstanceState != null) {
            supportFragmentManager.getFragment(savedInstanceState, "fragment")?.let {
                setFragment(
                    R.id.fragment_edit_post,
                    it.apply {
                        arguments = Bundle().apply {
                            putSerializable("post", post)
                        }
                    }
                )
            }
        } else {
            setFragment(R.id.fragment_edit_post, EditPostFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("post", post)
                }
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        supportFragmentManager.putFragment(outState, "fragment", EditPostFragment())
    }
}
package com.gogola.intranet.fragments

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import com.gogola.intranet.MainActivity
import com.gogola.intranet.R
import com.gogola.intranet.classes.Post
import com.gogola.intranet.extinsions.blockOrientation
import com.gogola.intranet.extinsions.setFragment
import com.gogola.intranet.extinsions.showMessage
import com.gogola.intranet.extinsions.validateFreePostText
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditPostFragment : Fragment() {
    private lateinit var postText: EditText
    private lateinit var deleteBtn: ImageView
    private lateinit var updateBtn: FloatingActionButton
    private lateinit var cancelBtn: ImageView
    private lateinit var loader: RelativeLayout
    private lateinit var post: Post
    private lateinit var menu: LinearLayoutCompat
    private lateinit var editTextContainer: LinearLayoutCompat

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_edit_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postText = view.findViewById(R.id.edit_post_text)
        deleteBtn = view.findViewById(R.id.delete_post)
        updateBtn = view.findViewById(R.id.update_post_btn)
        cancelBtn = view.findViewById(R.id.cancel_edit)
        loader = view.findViewById(R.id.edit_post_loader)
        menu = view.findViewById(R.id.edit_menu)
        editTextContainer = view.findViewById(R.id.edit_text_container)
        post = arguments?.getSerializable("post") as Post
        postText.setText(post.text)
        startFragment(view)
    }

    override fun onResume() {
        super.onResume()
        view?.let { startFragment(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("post_text", postText.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            postText.setText((savedInstanceState.getString("post_text").toString()))
        }
    }

    private fun showLoader() {
        loader.visibility = View.VISIBLE
        menu.visibility = View.GONE
        editTextContainer.visibility = View.GONE
        updateBtn.visibility = View.GONE
        activity?.let { blockOrientation(it) }
    }

    private fun hideLoader() {
        loader.visibility = View.GONE
        menu.visibility = View.VISIBLE
        editTextContainer.visibility = View.VISIBLE
        updateBtn.visibility = View.VISIBLE
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    private fun startFragment(view: View) {
        val db = Firebase.firestore

        cancelBtn.setOnClickListener() {
            activity?.finish()
        }

        updateBtn.setOnClickListener() {
            if (validateFreePostText(postText.text.toString()).success) {
                showLoader()
                db.collection("posts").document(post.postID).update(
                    "text", postText.text.toString()
                ).addOnSuccessListener {
                    loader.visibility = View.GONE
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    context?.let { it1 -> showMessage("Post updated.", it1) }
                    activity?.finish()

                }.addOnFailureListener {
                    hideLoader()
                    context?.let { it1 ->
                        showMessage(
                            "Something went wrong, try again letter.",
                            it1
                        )
                    }
                }

            } else {
                context?.let { it1 ->
                    showMessage(
                        validateFreePostText(postText.text.toString()).message,
                        it1
                    )
                }
            }
        }

        deleteBtn.setOnClickListener() {
            showLoader()
            db.collection("posts").document(post.postID).delete()
                .addOnSuccessListener {
                    loader.visibility = View.GONE
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    activity?.finish()
                    context?.let { it1 -> showMessage("Post deleted.", it1) }

                }
                .addOnFailureListener {
                    hideLoader()
                    context?.let { it1 ->
                        showMessage(
                            "Something went wrong, try again letter.",
                            it1
                        )
                    }
                }
        }
    }
}




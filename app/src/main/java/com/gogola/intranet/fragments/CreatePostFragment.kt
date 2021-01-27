package com.gogola.intranet.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.gogola.intranet.MainActivity
import com.gogola.intranet.R

class CreatePostFragment : Fragment() {
    private lateinit var postText: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_create_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postText = view.findViewById(R.id.post_text)
        startFragment(view)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("post_text", postText.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        postText.setText(savedInstanceState?.getString("post_text"))
    }

    private fun startFragment(view: View) {
        val intent = Intent(context, MainActivity::class.java)
        val cancelBtn: ImageView = view.findViewById(R.id.cancel)

        cancelBtn.setOnClickListener() {
            startActivity(intent)
        }
    }
}

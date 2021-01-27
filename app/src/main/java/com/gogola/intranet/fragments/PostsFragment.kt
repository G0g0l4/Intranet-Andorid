package com.gogola.intranet.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gogola.intranet.CreatePostActivity
import com.gogola.intranet.R
import com.gogola.intranet.adapters.PostsAdapter
import com.gogola.intranet.classes.Post
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PostsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startFragment(view)
    }

    private fun startFragment(view: View) {
        val postField: RecyclerView = view.findViewById(R.id.posts)
        val newPostButton: FloatingActionButton = view.findViewById(R.id.newPostButton)
        val posts = listOf<Post>(
            Post(
                "1213",
                "11111",
                "Lorem Ipsum Dolor Sit amet",
                "4/2020/12"
            ),
            Post(
                "1213",
                "11111",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. In placerat, neque eget placerat convallis, urna justo commodo lectus, pulvinar ornare eros odio ac ipsum. Nam eget sagittis dolor, sit amet dapibus est.",
                "4/2020/12"
            ),
            Post(
                "1213",
                "11111",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. In placerat, neque eget placerat convallis, urna justo commodo lectus, pulvinar ornare eros odio ac ipsum. Nam eget sagittis dolor, sit amet dapibus est.",
                "4/2020/12"
            ),
            Post(
                "1213",
                "11111",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. In placerat, neque eget placerat convallis, urna justo commodo lectus, pulvinar ornare eros odio ac ipsum. Nam eget sagittis dolor, sit amet dapibus est.",
                "4/2020/12"
            ),
            Post(
                "1213",
                "11111",
                "Test",
                "4/2020/12"
            ),
            Post(
                "1213",
                "11111",
                "Test",
                "4/2020/12"
            ),
            Post(
                "1213",
                "11111",
                "Test",
                "4/2020/12"
            )
        )
        postField.adapter = PostsAdapter(posts)
        postField.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        (postField.adapter as PostsAdapter).notifyDataSetChanged()

        newPostButton.setOnClickListener() {
            startActivity(Intent(context, CreatePostActivity::class.java))
        }
    }
}

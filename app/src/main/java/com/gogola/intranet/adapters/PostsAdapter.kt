package com.gogola.intranet.adapters

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gogola.intranet.R
import com.gogola.intranet.classes.Post
import java.util.*

class PostsAdapter(
    private val posts: List<Post>,
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.setContent(posts[position])
    }

    override fun getItemCount(): Int = posts.size

    inner class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageContainer: ImageView = view.findViewById(R.id.userImg)
        private val name: TextView = view.findViewById(R.id.name)
        private val postText: TextView = view.findViewById(R.id.text)
        private val postDate: TextView = view.findViewById(R.id.date)

        private fun getDate(timestamp: Long): String {
            val calendar = Calendar.getInstance(Locale.ENGLISH)
            calendar.timeInMillis = timestamp * 1000L
            return DateFormat.format("dd MMM, yyyy", calendar).toString()
        }

        fun setContent(postInfo: Post) {
            name.text = postInfo.postOwner
            postText.text = postInfo.text
            postDate.text = getDate(postInfo.date.toLong())
        }
    }
}
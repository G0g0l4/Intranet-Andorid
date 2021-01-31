package com.gogola.intranet.adapters

import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gogola.intranet.EditPostActivity
import com.gogola.intranet.R
import com.gogola.intranet.classes.Post
import com.gogola.intranet.extinsions.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.*

class PostsAdapter(
    private val posts: List<Post>,
    private val context: Context
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.setContent(posts[position])
    }

    override fun getItemCount(): Int = posts.size

    inner class PostViewHolder(view: View, context: Context) : RecyclerView.ViewHolder(view) {
        private val imageContainer: ImageView = view.findViewById(R.id.post_user_img)
        private val name: TextView = view.findViewById(R.id.name)
        private val postText: TextView = view.findViewById(R.id.text)
        private val postDate: TextView = view.findViewById(R.id.date)
        var currentUID = FirebaseAuth.getInstance().currentUser?.uid
        private val storageReference = FirebaseStorage.getInstance().reference

        private fun getDate(timestamp: Long): String {
            val calendar = Calendar.getInstance(Locale.ENGLISH)
            calendar.timeInMillis = timestamp * 1000L
            return DateFormat.format("dd MMM, yyyy", calendar).toString()
        }

        fun setContent(postInfo: Post) {
            name.text = postInfo.postOwner
            postText.text = postInfo.text
            postDate.text = getDate(postInfo.date.toLong())

            storageReference.child("images/${postInfo.ownerID}").downloadUrl
                .addOnSuccessListener {
                    Picasso.get().load(it).into(imageContainer)
                }
                .addOnFailureListener {
                }

            itemView.setOnClickListener() {
                if (posts[adapterPosition].ownerID == currentUID.toString()) {
                    itemView.context.startActivity(
                        Intent(
                            itemView.context,
                            EditPostActivity::class.java
                        ).apply {
                            putExtra("post", posts[adapterPosition])
                        })
                } else {
                    showMessage("You can only edit your own posts.", context)
                }
            }
        }
    }
}
package com.gogola.intranet.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gogola.intranet.R
import com.gogola.intranet.UserDetailedActivity
import com.gogola.intranet.classes.User
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class SearchUsersAdapter(
    private val users: List<User>,
) : RecyclerView.Adapter<SearchUsersAdapter.UsersViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.search_user_item, parent, false)
        return UsersViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.setContent(users[position])
    }

    override fun getItemCount(): Int = users.size

    inner class UsersViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val userImage: ImageView = view.findViewById(R.id.search_user_image)
        private val fullName: TextView = view.findViewById(R.id.fullName)
        private val storageReference = FirebaseStorage.getInstance().reference

        fun setContent(userInfo: User) {
            with(userInfo) {
                fullName.text = this.firstName + " " + this.lastName

                storageReference.child("images/${userInfo.UID}").downloadUrl
                    .addOnSuccessListener {
                        Picasso.get().load(it).into(userImage)
                    }
                    .addOnFailureListener {
                    }

                itemView.setOnClickListener() {
                    itemView.context.startActivity(
                        Intent(
                            itemView.context,
                            UserDetailedActivity::class.java
                        ).apply {
                            putExtra("user", users[adapterPosition])
                        })
                }
            }
        }
    }
}
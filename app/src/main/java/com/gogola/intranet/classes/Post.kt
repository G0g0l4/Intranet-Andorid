package com.gogola.intranet.classes

import java.io.Serializable

data class Post(
    val postID: String,
    val ownerID: String,
    val postOwner: String,
    val text: String,
    val date: String
) : Serializable

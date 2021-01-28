package com.gogola.intranet.classes

import java.io.Serializable

data class Post(
    val postOwner: String,
    val text: String,
    val date: String
) : Serializable

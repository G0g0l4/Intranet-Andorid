package com.gogola.intranet.classes

import java.io.Serializable

data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
) : Serializable
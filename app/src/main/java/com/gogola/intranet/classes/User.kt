package com.gogola.intranet.classes

import java.io.Serializable

data class User(
    val UID: String,
    val firstName: String,
    val lastName: String,
    val email: String
) : Serializable
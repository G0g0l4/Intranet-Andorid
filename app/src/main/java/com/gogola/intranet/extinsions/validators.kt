package com.gogola.intranet.extinsions

import android.util.Patterns

fun validateEmail(email: String): Valid {
    return when {
        email.isEmpty() -> {
            Valid(
                false,
                "Email is required!"
            )
        }
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
            Valid(
                false,
                "Email is not valid!"
            )
        }
        else -> {
            Valid(
                true,
                ""
            )
        }
    }
}

fun validateFirstName(firstName: String): Valid {
    return when {
        firstName.isEmpty() -> {
            Valid(
                false,
                "First name is required!"
            )
        }
        else -> {
            Valid(
                true,
                ""
            )
        }
    }
}

fun validateLastName(lastName: String): Valid {
    return when {
        lastName.isEmpty() -> {
            Valid(
                false,
                "Last name is required!"
            )
        }
        else -> {
            Valid(
                true,
                ""
            )
        }
    }
}

fun validatePassword(password: String, fieldName: String = "Password"): Valid {
    return when {
        password.isEmpty() -> {
            Valid(
                false,
                "$fieldName is required!"
            )
        }
        password.length < 6 -> {
            Valid(
                false,
                "$fieldName length must be greater then 6 characters!"
            )
        }
        else -> {
            Valid(
                true,
                ""
            )
        }
    }
}

fun validatePasswordMatch(newPassword: String, repeatedPassword: String): Valid {
    return when {
        newPassword != repeatedPassword -> {
            Valid(
                false,
                "Passwords do not match!"
            )
        }
        else -> {
            Valid(
                true,
                ""
            )
        }
    }
}

fun validateFreePostText(text: String): Valid {
    return when {
        text.isEmpty() -> {
            Valid(
                false,
                "Text is required!"
            )
        }
        else -> {
            Valid(
                true,
                ""
            )
        }
    }
}
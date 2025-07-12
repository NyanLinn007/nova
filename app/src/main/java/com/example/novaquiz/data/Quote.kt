package com.example.novaquiz.data

import com.google.firebase.firestore.Exclude

data class Quote(
    val Category : String ="",
    val createdAt :String ="",
    val reference :String="",
    val text :String="",
    @get:Exclude
    var quoteId: String = "",
    @get:Exclude var favorite: Boolean = false
)

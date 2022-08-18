package com.fine_app.ui.myPage

data class RequestProfileData(
    val userImageNum: Int,
    val nickname: String,
    val intro: String,
    val keyword1: String,
    val keyword2: String,
    val keyword3: String
)

data class RequestAuthData(
    val id: String,
    val password: String,
    val keyword1: String,
    val keyword2: String,
    val keyword3: String
)
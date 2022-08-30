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
    val userId: String,
    val password: String,
    val nickname: String,
    val keyword1: String,
    val keyword2: String,
    val keyword3: String
)

data class RequestLoginData(
    val id: String,
    val password: String
)
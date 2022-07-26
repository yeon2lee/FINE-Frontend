package com.fine_app.ui.MyPage

data class RequestProfileData(
    val nickname: String,
    val intro: String
)

data class RequestAuthData(
    val id: String,
    val password: String
)
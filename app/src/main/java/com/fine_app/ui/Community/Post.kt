package com.fine_app.ui.Community

data class Post(
    val nickname:String, //닉네임
    val profileID:Int, //프로필사진
    val title:String, //글제목
    val content:String,  //글내용
    val comment:String,  //댓글수
    val participants:String, //참여자수
    val capacity:String  //정원
    )

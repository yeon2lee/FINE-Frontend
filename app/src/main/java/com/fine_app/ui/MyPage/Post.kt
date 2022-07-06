package com.fine_app.ui.MyPage

import com.google.gson.annotations.SerializedName

data class Post(
    val createdDate:String,
    val lastModifiedDate:String,
    val title:String,
    val content: String,
    val closingCheck:Boolean,
    val group_check:Boolean,
    val maxMember: Int,
    val comments: ArrayList<Comment>,
    val posting_id: Long,
)


data class Comment(
    @SerializedName(value = "comment_id")
    val nickname:String,
    val text:String,
    val profileID: Int
    )
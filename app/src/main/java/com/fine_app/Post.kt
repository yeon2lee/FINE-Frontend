package com.fine_app

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Post(
    val PostingID:String,
    val nickname: String,
    val title:String,
    val content: String,
    val commentCount:String,
    val createdDate:String,
    val lastModifiedDate:String,
    val closingCheck:Boolean,
    val groupCheck:Boolean,
    @SerializedName(value = "maxMember")
    val capacity:Int,
    val comments: ArrayList<Comment>
):Serializable
data class Comment(
    @SerializedName(value = "comment_id")
    val nickname:String,
    val text:String,
    val profileID: Int
    ):Serializable

data class Member (
    val id: Long,
    val nickname: Long,
    val password:String,
    val email:String,
    val userIntroduction:String,
    val userUniversity:String,
    val userCollege:String,
    val userPhoneNumber: String,
    val userResidence:String,
    val level:String,
    val report:Long
)

data class Posting(
    val title:String,
    val content:String,
    val groupCheck : Boolean
)

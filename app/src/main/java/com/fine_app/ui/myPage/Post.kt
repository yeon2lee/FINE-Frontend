package com.fine_app.ui.myPage

import com.fine_app.Comment
import com.fine_app.Recruit
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Post(
    val postingId:Long,
    @SerializedName(value = "writerId")
    val memberId: Long,
    @SerializedName(value = "writerNickname")
    val nickname: String,
    val title:String,
    val content: String,
    val commentCount:String,
    val createdDate:String,
    val lastModifiedDate:String,
    val closingCheck:Boolean,
    val group_check:Boolean,
    val checkRecruitingId:Long,
    val checkBookmarkId:Long,
    val maxMember:Int,
    @SerializedName(value = "headCount")
    val participants:Int,
    val comments: ArrayList<Comment>,
    val recruitingList:ArrayList<Recruit>,
): Serializable

data class Recruit(
    val recruitingId:Long,
    val member: RecruitMember,
    val acceptCheck:Boolean
):Serializable

data class RecruitMember(
    val memberId:Long,
    val nickname:String,
    val level:String
):Serializable

data class Comment(
    val member:CommentMember,
    val text:String, //댓글 내용
    val commentId:Long
):Serializable

data class CommentMember(
    val memberId: Long,
    val nickname:String,
    val level:String
):Serializable

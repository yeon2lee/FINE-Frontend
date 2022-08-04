package com.fine_app

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.lang.reflect.Member

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
    val groupCheck:Boolean,
    val checkRecruitingId:Long,
    val checkBookmarkId:Long,
    @SerializedName(value = "maxMember")
    val capacity:Int,
    @SerializedName(value = "headCount")
    val participants:Int,
    val comments: ArrayList<Comment>,
    val recruitingList:ArrayList<Recruit>,
):Serializable

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

data class Member (
    val id: Long,
    val userImageNum: Int,
    val nickname: String,
    val intro:String,
    val password:String,
    val email:String,
    val userIntroduction:String,
    val userUniversity:String,
    val userCollege:String,
    val userPhoneNumber: String,
    val userResidence:String,
    val level:String,
    val report:Long
):Serializable

data class Friend(
    val friendId: Long,
    val nickname: String,
    val intro: String,
    val level : String,
):Serializable

data class Posting(
    val title:String,
    val content:String,
    val groupCheck : Boolean,
    val maxMember:Int
):Serializable

data class NewComment(
    val memberId:Long,
    val postingId:Long,
    val text:String
):Serializable


data class BookMark(
    val memberId:Long,
    val postingId:Long,
):Serializable

data class Join(
    val id:Long,
    val accept_check:Boolean
):Serializable

data class MarkId(
    val bookmark_id:Long
):Serializable

data class AcceptCheck(
    val acceptCheck:Boolean
):Serializable

data class EditPost(
    val text:String
):Serializable
package com.fine_app

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
    val userID:Long,
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
    val report:Long,
    val createdDate:String,
    val lastModifiedDate:String,
    val keyword1:String,
    val keyword2:String,
    val keyword3:String,
):Serializable

data class Friend(
    val friendId: Long,
    val nickname: String,
    val intro: String,
    val level : String,
    val imageNum: Int
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

data class Test(
    val name:String,
    val content:String
):Serializable

data class CreateChatRoom(
    val roomId:Long,
    val soloCheck:Boolean,
    val updateTime:String,
    val latestMessage:String
):Serializable

data class ChatRoomList(
    val roomId:Long,
    val roomName:String,
    val lastMessageTime:String,
    val latestMessage:String,
    val imageNum:Int,
    val soloCheck:Boolean,
    val memberCount:Int,
    val unreadMessageCount:Int
):Serializable

data class ChangeChatRoom(
    val roomName:String,
    val member:Member,
    val recentOutTime:String,
    val presentPosition: Boolean,
    val chatMemberId: Long
):Serializable

data class ChatMember(
    val roomId:String,
    val chatMemberList:List<ChangeChatRoom>
):Serializable

data class Chat(
    val roomId:Long,
    val soloCheck:Boolean,
    val memberCount:Int,
    val roomName:String,
    val chatMessageList:List<ChatMessage>
):Serializable

data class ChatMessage(
    val createdDate:String,
    val lastModifiedDate:String,
    val messageId:Long,
    val sender:Member,
    val message:String,
    val unreadCount:Int
) :Serializable

data class PersonalChat(
    val myId:Long,
    val receiverId:Long
):Serializable

data class GroupChat(
    val myId:Long,
    val receiverList:List<Long>,
    val roomName:String
):Serializable

data class ChangeRoomName(
    val memberId:Long,
    val roomId:Long,
    val roomName: String
):Serializable

data class ChatRoom(
    val roomId:Long,
    val memberId: Long
):Serializable
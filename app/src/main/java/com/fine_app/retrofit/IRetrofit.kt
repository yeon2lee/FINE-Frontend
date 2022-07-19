package com.fine_app.retrofit

import com.fine_app.*
import com.fine_app.ui.MyPage.Profile
import retrofit2.Call
import retrofit2.http.*

interface IRetrofit {
    //일반커뮤니티
    @POST("/post/{memberID}") //글 작성
    fun addMainPost(@Path("memberID")  memberID:Long, @Body postInfo:Posting):Call<Post>

    @GET("/post/general") //글 목록 조회
    fun viewMainCommunity() :Call<List<Post>>

    @GET("/post/{postingId}") //글 세부 조회
    fun viewMainPosting(@Path("postingId") PostingID:Long):Call<Post>

    @DELETE("/post/{postingId}") //글 삭제
    fun deletePosting(@Path("postingId") PostingID:Long):Call<Long>

    @PUT("/post/{postingId}/text") //글 내용 수정
    fun editPosting(@Path("postingId") PostingID:Long, text:String):Call<Post>

    @PUT("/post/{postingId}/text") //글 마감여부 변경
    fun editClosing(@Path("postingId") PostingID:Long):Call<Post>

    //그룹커뮤니티
    @POST("/post/{memberID}") //글 작성
    fun addGroupPost(@Path("memberID")  memberID:Long, @Body postInfo:GroupPosting):Call<GroupPosting>

    @GET("/post/group") //글 전체 목록 조회
    fun viewGroupCommunity() : Call<List<Post>>

    @GET("/post/group/proceed") //글 진행 목록 조회
    fun viewGroupCommunityProceed() : Call<List<Post>>

    @GET("/post/group/close") //글 완료 목록 조회
    fun viewGroupCommunityClose() : Call<List<Post>>

    @GET("/post/{postingId}") //글 세부 조회
    fun viewGroupPosting(@Path("postingId") PostingID:Long):Call<Post>

    @GET("profile/{memberId}") // 유저 프로필 조회
    fun getUserProfile(@Path("memberId") memberId: Long): Call<Profile>


    @GET("/post/{postingId}") //대기자리스트 todo 대기자 리스트 api 문서 보고 수정 필요
    fun viewWaitingList(@Path("postingId") postingId:Long) : Call<List<Post>>


    @POST("/post/{postingId}/{memberId}/join") //참여하기
    fun joinGroup(@Path("postingId") postingId:Long, @Path("memberId") memberId:Long, accept_check:Boolean) :Call<Join>

    @DELETE("post/{recruitingId}/delete") //참여하기 취소
    fun cancelJoinGroup(@Path("recruitingId") recruitingId:Long)
    //참가 수락
    //대기자 리스트


    //댓글
    @POST("/comment")
    fun addComment(@Body commentInfo: Comment):Call<Comment>

    @PUT("/comment/{commentId}") //내용 수정
    fun editComment(@Path("commentId") commentId:Long, @Body commentInfo: Comment):Call<Comment>

    @DELETE("/comment/{commentId}") //삭제
    fun deleteComment(@Path("commentId") commentId:Long):Call<Comment>

    //북마크
    @POST("/bookmark")
    fun addBookMark(@Body bookMark: BookMark):Call<BookMark>//생성

    @DELETE("/bookmark/{bookmarkId}") //삭제
    fun deleteBookMark(@Path("bookMarkId") bookMarkId:Long):Call<Long>//삭제


}
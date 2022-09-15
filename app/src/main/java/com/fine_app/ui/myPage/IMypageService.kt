package com.fine_app.ui.myPage
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import retrofit2.Call
import retrofit2.http.*

// 서비스 인터페이스 생성
interface IMypageService {
    // 로그인
    @PUT("login")
    fun userLogin(@Body user:RequestLoginData): Call<Profile>

    // 로그아웃
    @POST("logout")
    fun logout(): Call<Long>

    // 회원가입
    @POST("signup")
    fun signUp(
        @Body user: RequestAuthData
    ): Call<Profile>

    // 회원 탈퇴
    @GET("mypage/{memberId}")
    fun userSecession(@Path("memberId") memberId: Long): Call<Profile>

    // 아이디 중복 확인
    @POST("/idVerification")
    fun verifyId(
        @Body userId: String
    ): Call<ResponseDto>

    // 닉네임 중복 확인
    @POST("/nickNameVerification")
    fun verifyNickname(
        @Body userNickName: String
    ): Call<ResponseDto>

    // 전화번호로 인증 번호 전송
    @POST("/mypage/phone/{memberId}")
    fun sendAuthMesssage(
        @Path("memberId") memberId: Long,
        @Body phoneNumber: String
    ): Call<Long>

    // 번호 인증 최종
    @POST("/mypage/phone/token/{memberId}")
    fun verifyAuth(
        @Path("memberId") memberId: Long,
        @Body token: String
    ): Call<Long>

    // 지역 인증
    @POST("/mypage/residence/{memberId}")
    fun verifyLocationAuth(
        @Path("memberId") memberId: Long,
        @Body residenceDto: ResidenceDto
    ): Call<Long>

    // 대학 인증
    @GET("https://www.career.go.kr/cnet/openapi/getOpenApi?apiKey=efb0890dbac3202c498b279ad01a8e28&svcType=api&svcCode=SCHOOL&contentType=json&gubun=univ_list")
    fun searchUniversity(
        @Query("apiKey") apiKey: String,
        @Query("svcType") svcType: String,
        @Query("svcCode") svcCode: String,
        @Query("contentType") contentType: String,
        @Query("gubun") gubun: String,
        @Query("searchSchulNm") searchSchulNm: String
    ): Call<DataSearch>

    // 대학 메일 보내기
    @POST("/mail")
    fun sendMail(
        @Body address: String
    ): Call<Long>

    // 대학 메일 인증
    @POST("/emailVerification/{memberId}")
    fun verifyUniversityAuth(
        @Path("memberId") memberId: Long,
        @Body UniversityDto: UniversityDto
    ): Call<UniversityDto>

    // 프로필 생성
    @GET("mypage/{memberId}")
    fun getMyProfile(@Path("memberId") memberId: Long): Call<Profile>

    // 내가 쓴 글 목록
    @GET("mypage/post/{memberId}")
    fun getMyPostList(@Path("memberId") memberId: Long): Call<List<Post>>

    // 북마크 목록
    @GET("mypage/bookmark/{memberId}")
    fun getMyBookmarkList(@Path("memberId") memberId: Long): Call<List<Post>>

    // 그룹 신청글 목록
    @GET("mypage/post/group/{memberId}")
    fun getMyGroupList(@Path("memberId") memberId: Long): Call<List<Post>>

    // 프로필 수정
    @POST("mypage/profile/{memberId}")
    fun editProfile(
        @Path("memberId") memberId: Long,
        @Body user: RequestProfileData
    ): Call<Profile>

    // 프로필 조회
    @GET("profile/{memberId}")
    fun getFriendProfile(@Path("memberId") memberId: Long): Call<Profile>
}
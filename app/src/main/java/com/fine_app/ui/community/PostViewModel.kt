/*package com.fine_app.ui.community

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fine_app.Comment
import com.fine_app.Post
import com.fine_app.R
//import com.fine_app.repository.PostRepository
import kotlinx.coroutines.launch


class PostViewModel:ViewModel() {
    val postList=listOf( //capacity가 1이면 일반, 아니면 그룹

        //일반
        Post("글1", "사용자1", "카페에서 오늘 하루 같이 공부해요",
            "열심히 하실 분! \n신촌 근처 카페에서 2시부터 같이 공부하고 저녁 먹어요","1",
            "2022-06-24T15:09:24.964117","2022-06-24T15:09:25.17818",false,
            false, 1, arrayListOf(Comment("댓글1", "안녕하세요",R.drawable.ic_launcher_foreground ))) ,
        Post("글2", "사용자2", "다음주에 같이 전시회 보러가요!", "다음주 주말에 같이 서울로 전시회 보러가실 분 구합니다 \n제가 여자라서 상대분도 여자였으면 좋겠어요! 편하게 댓글 남겨주세요:-) ","1", "2022-06-24T15:09:24.964117","2022-06-24T15:09:25.17818",false, false, 1, arrayListOf(Comment("댓글1", "안녕하세요",R.drawable.ic_launcher_foreground ))),
        //그룹 //진행중
        Post("글3", "사용자3", "아침마다 기상인증해요", "매일 정해진 시간에 기상 인증하실 분! \n 오픈카톡으로 진행할 예정입니다! 관심 있으신 분들은 댓글 남겨주세요 \n 같이 열심히 해봐요","2", "2022-06-24T15:09:24.964117","2022-06-24T15:09:25.17818",false, true, 3, arrayListOf(Comment("댓글1", "안녕하세요",R.drawable.ic_launcher_foreground ),Comment("댓글2", "안녕하세요~!!",R.drawable.ic_launcher_foreground))),
        Post("글4", "사용자4", "공모전 팀원 구해요", "빅데이터 공모전 팀원 구합니다 \n 관심 있으신 분들은 댓글 남겨주세요 \n 같이 열심히 해봐요","2", "2022-06-24T15:09:24.964117","2022-06-24T15:09:25.17818",true, true, 3, arrayListOf(Comment("댓글1", "안녕하세요",R.drawable.ic_launcher_foreground ),Comment("댓글2", "안녕하세요~!!",R.drawable.ic_launcher_foreground)))

    )
}

 */

/*
class PostViewModel:BaseObservable() {
    var post: Post?=null
    set(post){
        field=post
        notifyChange()
    }
    @get:Bindable
    val PostingID:String? get()=post?.PostingID
    val nickname:String? get()=post?.nickname
    //val profileID:Int? get()=post?.profileID
    val title:String? get()=post?.title
    val content:String? get()=post?.content
    val commentCount:String? get()=post?.commentCount
    val createdDate:String? get()=post?.createdDate
    val lastModifiedDate:String? get()=post?.lastModifiedDate
    val closingCheck:Boolean? get()=post?.closingCheck
    val groupCheck:Boolean? get()=post?.closingCheck
    //val participants:String? get()=post?.participants
    val capacity:Int? get()=post?.capacity
    val comments: ArrayList<Comment>? get()=post?.comments

}

 */


/*
class PostViewModel(private val postRepository: PostRepository):ViewModel(){

    private val _items=MutableLiveData<List<Post>>()
    val items:LiveData<List<Post>> = _items

    init{ //뷰모델이 생성되는 시점에 loadpost호출
        loadPost()
    }

    private fun loadPost(){
        //repository class에 데이터 요청 - 코틀린 코루틴 활용
        viewModelScope.launch {
            val posts = postRepository.getMainPosts() //뷰모델에서 저장할 데이터
            _items.value = posts
        }
    }
}

*/

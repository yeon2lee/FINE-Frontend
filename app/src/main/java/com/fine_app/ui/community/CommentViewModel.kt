package com.fine_app.ui.community

import androidx.lifecycle.ViewModel
import com.fine_app.Comment
import com.fine_app.R

class CommentViewModel :ViewModel(){
    var commentList= listOf<Comment>(
        Comment("닉네임", "채팅 드렸어요~!", R.drawable.ic_launcher_foreground),
        Comment("닉네임2", "아직도 모집 하시나요?! ", R.drawable.ic_launcher_foreground) ,
        Comment("닉네임3", "저 같이 하고싶습니다! ", R.drawable.ic_launcher_foreground)
    )
}
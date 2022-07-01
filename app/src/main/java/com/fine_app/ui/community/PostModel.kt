package com.fine_app.ui.community

import com.fine_app.R

class PostModel() {
    val posts:List<Post>
    init{
        posts=loadPosts()
    }
    private fun loadPosts():List<Post>{
        val posts= mutableListOf<Post>()
        posts.add(Post("사용자1", R.drawable.ic_launcher_foreground,"카페에서 오늘 하루 같이 공부해요", "열심히 하실 분! \n신촌 근처 카페에서 2시부터 같이 공부하고 저녁 먹어요","0", "0", "1"))
        posts.add(Post("사용자2", R.drawable.ic_launcher_foreground,"다음주에 같이 전시회 보러가요!", "다음주 주말에 같이 서울로 전시회 보러가실 분 구합니다 \n제가 여자라서 상대분도 여자였으면 좋겠어요! 편하게 댓글 남겨주세요:-) ","2", "0", "1"))
        posts.add(Post("사용자3", R.drawable.ic_launcher_foreground,"아침마다 기상인증해요", "매일 정해진 시간에 기상 인증하실 분! \n 오픈카톡으로 진행할 예정입니다! 관심 있으신 분들은 댓글 남겨주세요 \n 같이 열심히 해봐요","1", "4", "5"))
        posts.add(Post("사용자4", R.drawable.ic_launcher_foreground,"7월부터 캠스터디 같이 하실 분 구해요", "전원 합격을 목표로 이번에 합격 안 하면 안 된다 하는 절박한 분들만 받습니다!!!!\n 7월 중순부터 시범적으로 시작하고 말부터는 제대로 할 예정입니다.\n 줌이나 구글 미팅으로 진행할 예정입니다.","1", "1", "5"))
        posts.add(Post("사용자5", R.drawable.ic_launcher_foreground,"공모전 팀원 구해요", "빅데이터 공모전 팀원 구해요. 컴퓨터공학 4학년 재학중이고 수상 경력 있습니다! 경험 있으신분들 우대해요!!","2", "3", "3"))
        posts.add(Post("사용자6", R.drawable.ic_launcher_foreground,"cs 스터디 팀원 구해요", "cs 면접 스터디 팀원 구해요. 현재 매주 화요일 오후 6시 진행중이고, 추가 인원 두 명 구합니다!","4", "2", "2"))
        return posts
    }
}
package com.fine_app.ui.home

import androidx.fragment.app.Fragment
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fine_app.*
import com.fine_app.Post
import com.fine_app.databinding.FragmentHomeBinding
import com.fine_app.retrofit.API
import com.fine_app.retrofit.IRetrofit
import com.fine_app.retrofit.RetrofitClient
import com.fine_app.ui.community.PostDetail_Group
import com.fine_app.ui.community.PostDetail_Main
import com.fine_app.ui.community.ShowUserProfileActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    lateinit var userInfo: SharedPreferences
    var myId by Delegates.notNull<Long>()
    private var user1_id:Long=1
    private var user2_id:Long=1
    private var user3_id:Long=1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        Log.d("home", "홈 프래그먼트 create")
        return root
    }

    inner class MyViewHolder(view:View): RecyclerView.ViewHolder(view){

        private lateinit var post: Post
        private val recommendTitle:TextView =itemView.findViewById(R.id.recommendTitle)
        private val recommendContent: TextView =itemView.findViewById(R.id.recommendContent)
        private val recommendCapacity: TextView =itemView.findViewById(R.id.recommendCapacity)
        private val recommendVacancy: TextView =itemView.findViewById(R.id.recommendVacancy)
        private val recommendKeyWord2: TextView =itemView.findViewById(R.id.recommendKeyWord2)
        private val recommendKeyWord1: TextView =itemView.findViewById(R.id.recommendKeyWord1)
        private val capacityBox:LinearLayout=itemView.findViewById(R.id.capacityBox)
        private val matchingBox:LinearLayout=itemView.findViewById(R.id.matchingBox)

        fun bind(post: Post) {
            this.post=post
            recommendTitle.text=this.post.title
            recommendContent.text=this.post.content
            recommendKeyWord2.text=this.post.keyword
            if(this.post.groupCheck) {
                recommendKeyWord1.text="그룹"
                recommendCapacity.text=this.post.capacity.toString()
                recommendVacancy.text=(this.post.capacity - this.post.participants).toString()
                matchingBox.setBackgroundResource(R.drawable.recommend_background_group)
            }
            else {
                matchingBox.setBackgroundResource(R.drawable.recommend_background)
                recommendKeyWord1.text="개인"
                capacityBox.visibility=View.INVISIBLE
            }

            itemView.setOnClickListener{
                if(this.post.groupCheck){
                    val postDetail= Intent(activity, PostDetail_Group::class.java)
                    postDetail.putExtra("postingId", this.post.postingId)
                    postDetail.putExtra("memberId", this.post.memberId)
                    startActivity(postDetail)
                }else{
                    val postDetail= Intent(activity, PostDetail_Main::class.java)
                    postDetail.putExtra("postingId", this.post.postingId)
                    postDetail.putExtra("memberId", this.post.memberId)
                    startActivity(postDetail)
                }
            }
        }
    }
    inner class MyAdapter(val list:List<Post>): RecyclerView.Adapter<MyViewHolder>() {
        override fun getItemCount(): Int = list.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

            val view=layoutInflater.inflate(R.layout.item_recommend, parent, false)
            return MyViewHolder(view)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val post=list[position]
            holder.bind(post)
        }
    }

    private fun viewPopularPosting(){
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewPopularPosting() ?:return

        call.enqueue(object : Callback<List<Post>> {

            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                Log.d("retrofit", "홈 - 응답 성공 / t : ${response.raw()}")
                recyclerView=binding.recyclerView
                recyclerView.layoutManager=LinearLayoutManager(context ,LinearLayoutManager.HORIZONTAL, false)
                recyclerView.adapter=MyAdapter(response.body()!!)
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Log.d("retrofit", "홈 - 응답 실패 / t: $t")
            }
        })
    }
    private fun viewMatchingFriends(category:Int){
        if(myId==0.toLong() ) myId=6.toLong() //todo 추후 로그인 아이디로 변경

        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewMatchingFriends(myId, category = category , select="level") ?:return

        call.enqueue(object : Callback<List<MatchingFriend>> {

            override fun onResponse(call: Call<List<MatchingFriend>>, response: Response<List<MatchingFriend>>) {
                Log.d("retrofit", "홈 친구추천 - 응답 성공 / t : ${response.raw()}")
                if(response.body()!!.isNotEmpty()){
                    val friends:MatchingFriend= response.body()!![0]
                    when (category) {
                        1 -> {
                            user1_id=friends.memberId
                            binding.homeMatchingName1.text=friends.nickname
                            binding.homeMatchingKeyword1.text=friends.keyword1
                            binding.homeMatchingKeyword3.text=friends.keyword3
                            when (friends.userImageNum) {
                                0 -> binding.homeMatchingImage1.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                                1 -> binding.homeMatchingImage1.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                                2 -> binding.homeMatchingImage1.setImageResource(R.drawable.ic_noun_dooda_business_man_2019971)
                                3 -> binding.homeMatchingImage1.setImageResource(R.drawable.ic_noun_dooda_mustache_2019978)
                                4 -> binding.homeMatchingImage1.setImageResource(R.drawable.ic_noun_dooda_prince_2019982)
                                5 -> binding.homeMatchingImage1.setImageResource(R.drawable.ic_noun_dooda_listening_music_2019991)
                                6 -> binding.homeMatchingImage1.setImageResource(R.drawable.ic_noun_dooda_in_love_2019979)
                                else -> binding.homeMatchingImage1.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                            }
                            //todo 유저 레벨 이미지
                        }
                        2 -> {
                            user2_id=friends.memberId
                            binding.homeMatchingName2.text=friends.nickname
                            binding.homeMatchingKeyword4.text=friends.keyword1
                            binding.homeMatchingKeyword5.text=friends.keyword2
                            when (friends.userImageNum) {
                                0 -> binding.homeMatchingImage2.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                                1 -> binding.homeMatchingImage2.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                                2 -> binding.homeMatchingImage2.setImageResource(R.drawable.ic_noun_dooda_business_man_2019971)
                                3 -> binding.homeMatchingImage2.setImageResource(R.drawable.ic_noun_dooda_mustache_2019978)
                                4 -> binding.homeMatchingImage2.setImageResource(R.drawable.ic_noun_dooda_prince_2019982)
                                5 -> binding.homeMatchingImage2.setImageResource(R.drawable.ic_noun_dooda_listening_music_2019991)
                                6 -> binding.homeMatchingImage2.setImageResource(R.drawable.ic_noun_dooda_in_love_2019979)
                                else -> binding.homeMatchingImage2.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                            }
                            //todo 유저 레벨 이미지
                        }
                        else -> {
                            user3_id=friends.memberId
                            binding.homeMatchingName3.text=friends.nickname
                            binding.homeMatchingKeyword8.text=friends.keyword2
                            binding.homeMatchingKeyword9.text=friends.keyword3
                            when (friends.userImageNum) {
                                0 -> binding.homeMatchingImage3.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                                1 -> binding.homeMatchingImage3.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                                2 -> binding.homeMatchingImage3.setImageResource(R.drawable.ic_noun_dooda_business_man_2019971)
                                3 -> binding.homeMatchingImage3.setImageResource(R.drawable.ic_noun_dooda_mustache_2019978)
                                4 -> binding.homeMatchingImage3.setImageResource(R.drawable.ic_noun_dooda_prince_2019982)
                                5 -> binding.homeMatchingImage3.setImageResource(R.drawable.ic_noun_dooda_listening_music_2019991)
                                6 -> binding.homeMatchingImage3.setImageResource(R.drawable.ic_noun_dooda_in_love_2019979)
                                else -> binding.homeMatchingImage3.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                            }
                            //todo 유저 레벨 이미지
                        }
                    }
                }


            }

            override fun onFailure(call: Call<List<MatchingFriend>>, t: Throwable) {
                Log.d("retrofit", "홈 친추구천 - 응답 실패 / t: $t")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        userInfo = this.requireActivity().getSharedPreferences("userInfo", AppCompatActivity.MODE_PRIVATE)
        myId = userInfo.getString("userInfo", "2")!!.toLong()
        binding.showButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_home_recommend)
        }
        viewPopularPosting()


        binding.friendBox1.setOnClickListener{
            val userProfile = Intent(context, ShowUserProfileActivity::class.java)
            userProfile.putExtra("memberId",user1_id)
            startActivity(userProfile)
        }
        binding.friendBox2.setOnClickListener{
            val userProfile = Intent(context, ShowUserProfileActivity::class.java)
            userProfile.putExtra("memberId",user2_id)
            startActivity(userProfile)
        }
        binding.friendBox3.setOnClickListener{
            val userProfile = Intent(context, ShowUserProfileActivity::class.java)
            userProfile.putExtra("memberId",user3_id)
            startActivity(userProfile)
        }
        viewMatchingFriends(1)
        viewMatchingFriends(2)
        viewMatchingFriends(3)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
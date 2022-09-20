package com.fine_app.ui.home

import androidx.fragment.app.Fragment
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
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
    private var myId by Delegates.notNull<Long>()
    private val user1_id:Long=1
    lateinit var user1_name: String
    lateinit var user1_levelImage:ImageView
    lateinit var user1_profileImage:ImageView
    lateinit var user1_keyword1:String
    lateinit var user1_keyword2:String
    lateinit var user1_keyword3:String
    private val user2_id:Long=1
    lateinit var user2_name: String
    lateinit var user2_levelImage:ImageView
    lateinit var user2_profileImage:ImageView
    lateinit var user2_keyword1:String
    lateinit var user2_keyword2:String
    lateinit var user2_keyword3:String
    private val user3_id:Long=1
    lateinit var user3_name: String
    lateinit var user3_levelImage:ImageView
    lateinit var user3_profileImage:ImageView
    lateinit var user3_keyword1:String
    lateinit var user3_keyword2:String
    lateinit var user3_keyword3:String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        userInfo = this.requireActivity().getSharedPreferences("userInfo", AppCompatActivity.MODE_PRIVATE)
        myId = userInfo.getString("userInfo", "2")!!.toLong()

        binding.showButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_home_recommend)
        }
        viewPopularPosting()
        //todo 친구추천 서버 올라온 후 확인
        //viewMatchingFriends(1)
        //viewMatchingFriends(2)
        //viewMatchingFriends(3)

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
            //recommendKeyWord2.text=this.post.keyword //todo 키워드 서버 올라오면 실행
            if(this.post.groupCheck) {
                recommendKeyWord1.text="그룹"
                recommendCapacity.text=this.post.capacity.toString()
                recommendVacancy.text=(this.post.capacity - this.post.participants).toString()
                matchingBox.setBackgroundResource(R.drawable.recommend_background_group)
                Log.d("home", "${this.post.capacity}, ${this.post.participants}, ${this.post.capacity - this.post.participants}")
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
        val iRetrofit : IRetrofit? =
            RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.viewMatchingFriends(myId, category = category , select="level") ?:return

        call.enqueue(object : Callback<List<MatchingFriend>> {

            override fun onResponse(call: Call<List<MatchingFriend>>, response: Response<List<MatchingFriend>>) {
                Log.d("retrofit", "홈 친구추천 - 응답 성공 / t : ${response.raw()}")
                val friends:MatchingFriend= response.body()!![0]
                if(category==1){
                    user1_name=friends.nickname
                    user1_keyword1=friends.keyword1
                    user1_keyword2=friends.keyword2
                    when (friends.userImageNum) {
                        0 -> user1_profileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                        1 -> user1_profileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                        2 -> user1_profileImage.setImageResource(R.drawable.ic_noun_dooda_business_man_2019971)
                        3 -> user1_profileImage.setImageResource(R.drawable.ic_noun_dooda_mustache_2019978)
                        4 -> user1_profileImage.setImageResource(R.drawable.ic_noun_dooda_prince_2019982)
                        5 -> user1_profileImage.setImageResource(R.drawable.ic_noun_dooda_listening_music_2019991)
                        6 -> user1_profileImage.setImageResource(R.drawable.ic_noun_dooda_in_love_2019979)
                        else -> user1_profileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                    }
                    //todo 유저 레벨 이미지
                    // user1_keyword3=friends. todo 유저 키워드3 추가
                }else if(category==2){
                    user2_name=friends.nickname
                    user2_keyword1=friends.keyword1
                    user2_keyword2=friends.keyword2
                    when (friends.userImageNum) {
                        0 -> user2_profileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                        1 -> user2_profileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                        2 -> user2_profileImage.setImageResource(R.drawable.ic_noun_dooda_business_man_2019971)
                        3 -> user2_profileImage.setImageResource(R.drawable.ic_noun_dooda_mustache_2019978)
                        4 -> user2_profileImage.setImageResource(R.drawable.ic_noun_dooda_prince_2019982)
                        5 -> user2_profileImage.setImageResource(R.drawable.ic_noun_dooda_listening_music_2019991)
                        6 -> user2_profileImage.setImageResource(R.drawable.ic_noun_dooda_in_love_2019979)
                        else -> user2_profileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                    }
                    //todo 유저 레벨 이미지
                    // user2_keyword3=friends. todo 유저 키워드3 추가
                }else{
                    user3_name=friends.nickname
                    user3_keyword1=friends.keyword1
                    user3_keyword2=friends.keyword2
                    when (friends.userImageNum) {
                        0 -> user3_profileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                        1 -> user3_profileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                        2 -> user3_profileImage.setImageResource(R.drawable.ic_noun_dooda_business_man_2019971)
                        3 -> user3_profileImage.setImageResource(R.drawable.ic_noun_dooda_mustache_2019978)
                        4 -> user3_profileImage.setImageResource(R.drawable.ic_noun_dooda_prince_2019982)
                        5 -> user3_profileImage.setImageResource(R.drawable.ic_noun_dooda_listening_music_2019991)
                        6 -> user3_profileImage.setImageResource(R.drawable.ic_noun_dooda_in_love_2019979)
                        else -> user3_profileImage.setImageResource(R.drawable.ic_noun_dooda_angry_2019970)
                    }
                    //todo 유저 레벨 이미지
                    // user3_keyword3=friends. todo 유저 키워드3 추가
                }
            }

            override fun onFailure(call: Call<List<MatchingFriend>>, t: Throwable) {
                Log.d("retrofit", "홈 친추구천 - 응답 실패 / t: $t")
            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
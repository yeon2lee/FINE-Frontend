package com.fine_app.ui.community

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable


class PostViewModel:BaseObservable() {
    var post:Post?=null
    set(post){
        field=post
        notifyChange()
    }
    @get:Bindable
    val nickname:String? get()=post?.nickname
    val profileID:Int? get()=post?.profileID
    val title:String? get()=post?.title
    val content:String? get()=post?.content
    val comment:String? get()=post?.comment
    val participants:String? get()=post?.participants
    val capacity:String? get()=post?.capacity



}
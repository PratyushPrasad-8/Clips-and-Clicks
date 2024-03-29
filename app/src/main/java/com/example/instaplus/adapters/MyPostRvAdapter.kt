package com.example.instaplus.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.instaplus.Models.Post
import com.example.instaplus.databinding.MyPostRvDesignBinding
import com.squareup.picasso.Picasso

class MyPostRvAdapter(var context: Context, var postList: ArrayList<Post>) :
    RecyclerView.Adapter<MyPostRvAdapter.ViewHolder>() {
    inner class ViewHolder(var binding: MyPostRvDesignBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = MyPostRvDesignBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(postList.get(position).postUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.binding.postImage);
    }
}
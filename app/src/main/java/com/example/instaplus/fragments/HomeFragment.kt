package com.example.instaplus.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instaplus.Models.Post
import com.example.instaplus.Models.User
import com.example.instaplus.R
import com.example.instaplus.adapters.FollowAdapter
import com.example.instaplus.adapters.PostAdapter
import com.example.instaplus.databinding.FragmentHomeBinding
import com.example.instaplus.utils.FOLLOW
import com.example.instaplus.utils.POST
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding
    private var postlist=ArrayList<Post>()
    private lateinit var adapter:PostAdapter
    private var followList= ArrayList<User>()
    private lateinit var  followAdapter: FollowAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentHomeBinding.inflate(inflater , container, false)
        adapter=PostAdapter(requireContext(),postlist)
        binding.postRv.layoutManager=LinearLayoutManager(requireContext())
        binding.postRv.adapter=adapter

        followAdapter = FollowAdapter(requireContext(),followList)
        binding.followRv.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding.followRv.adapter=followAdapter
        setHasOptionsMenu(true)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.materialToolbar2)

        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid+ FOLLOW).get().addOnSuccessListener {
            var tempList=ArrayList<User>()
            followList.clear()
            for( i in it.documents){
                var user: User=i.toObject<User>()!!
                tempList.add(user)
            }
            followList.addAll(tempList)
            followAdapter.notifyDataSetChanged()
        }

        Firebase.firestore.collection(POST).get().addOnSuccessListener {
           var tempList=ArrayList<Post>()
            postlist.clear()
            for( i in it.documents){
                var post:Post=i.toObject<Post>()!!
                tempList.add(post)
            }
            postlist.addAll(tempList)
            adapter.notifyDataSetChanged()
        }

        return binding.root
    }

    companion object {

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}
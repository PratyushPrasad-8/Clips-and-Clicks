package com.example.instaplus.Post

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.instaplus.HomeActivity
import com.example.instaplus.Models.Post
import com.example.instaplus.Models.User
import com.example.instaplus.databinding.ActivityPostBinding
import com.example.instaplus.utils.POST
import com.example.instaplus.utils.POST_FOLDER
import com.example.instaplus.utils.USER_NODE
import com.example.instaplus.utils.USER_PROFILE_FOLDER
import com.example.instaplus.utils.uploadImage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject


class PostActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityPostBinding.inflate(layoutInflater)
    }

    var imageUrl: String? = null
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadImage(uri, POST_FOLDER) { url ->
                if (url != null) {
                    binding.selectImage.setImageURI(uri)
                    binding.AddPostImageLoader.alpha=0f
                    imageUrl = url
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.materialToolbar)

        binding.AddPostImageLoader.alpha=0f
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        binding.materialToolbar.setNavigationOnClickListener {
            startActivity(Intent(this@PostActivity, HomeActivity::class.java))
            finish()
        }

        binding.selectImage.setOnClickListener {
            binding.AddPostImageLoader.alpha=1f
            launcher.launch("image/*")
        }

        binding.cancelButton.setOnClickListener {
            startActivity(Intent(this@PostActivity, HomeActivity::class.java))
            finish()
        }

        binding.postButton.setOnClickListener {
            Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
                .addOnSuccessListener {

                    val user  = it.toObject<User>()
                    if (user != null) {
                        val post: Post = Post(postUrl = imageUrl!!, caption = binding.caption.editText?.text.toString(), uid = Firebase.auth.currentUser!!.uid,
                            time = System.currentTimeMillis().toString()
                        )

                        Firebase.firestore.collection(POST).document().set(post).addOnSuccessListener {
                            Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document()
                                .set(post).addOnSuccessListener {
                                    Toast.makeText(
                                        this@PostActivity,
                                        "YOUR POST HAS BEEN UPLOADED SUCCESSFULLY!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(Intent(this@PostActivity, HomeActivity::class.java))
                                    finish()
                                }
                        }
                    } else {
                        // Handle the case where 'toObject<User>()' returns null
                        Toast.makeText(this@PostActivity, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
                    }

                }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@PostActivity, HomeActivity::class.java))
        finish()
    }
}
package com.example.instaplus.Post

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.instaplus.HomeActivity
import com.example.instaplus.Models.Reel
import com.example.instaplus.Models.User
import com.example.instaplus.databinding.ActivityReelsBinding
import com.example.instaplus.utils.REEL
import com.example.instaplus.utils.REEL_FOLDER
import com.example.instaplus.utils.USER_NODE
import com.example.instaplus.utils.uploadReel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class ReelsActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityReelsBinding.inflate(layoutInflater)
    }
    private lateinit var reelsUrl: String
    lateinit var progressDialog: ProgressDialog
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadReel(uri, REEL_FOLDER, progressDialog) { url ->
                if (url != null) {

                    reelsUrl = url
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        progressDialog = ProgressDialog(this)

        binding.selectReels.setOnClickListener {
            launcher.launch("video/*")
        }

        binding.cancelButton.setOnClickListener {
            startActivity(Intent(this@ReelsActivity, HomeActivity::class.java))
            finish()
        }

        binding.postButton.setOnClickListener {
            Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    var user: User = it.toObject<User>()!!

                    val reel: Reel =
                        Reel(reelsUrl!!, binding.caption.editText?.text.toString(), user.image!!)

                    Firebase.firestore.collection(REEL).document().set(reel).addOnSuccessListener {
                        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + REEL)
                            .document().set(reel).addOnSuccessListener {
                            Toast.makeText(
                                this@ReelsActivity, "YOUR REEL HAS BEEN UPLOADED SUCCESFULLY!",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this@ReelsActivity, HomeActivity::class.java))
                            finish()
                        }
                    }
                }

        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@ReelsActivity, HomeActivity::class.java))
        finish()
    }
}
package com.example.instaplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.instaplus.Models.User
import com.example.instaplus.databinding.ActivitySignUpBinding
import com.example.instaplus.utils.USER_NODE
import com.example.instaplus.utils.USER_PROFILE_FOLDER
import com.example.instaplus.utils.uploadImage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.squareup.picasso.Picasso


class SignUpActivity : AppCompatActivity() {
    val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    lateinit var user: User
    private val launcher= registerForActivityResult(ActivityResultContracts.GetContent()){
        uri->
        uri?.let{
           uploadImage(uri, USER_PROFILE_FOLDER){
                if(it!=null){
                    user.image=it
                    binding.profileImage.setImageURI(uri)
                    binding.signupProgressBarLoadingImage.alpha=0f
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.signupProgressBarLoadingImage.alpha=0f
        val text= "<font color=#FF000000> Already have an Account </font> <font color=#1E88E5>Login ?</font>"
        binding.login.setText(Html.fromHtml(text))
        user = User()
        if(intent.hasExtra("MODE")){
            if(intent.getIntExtra("MODE",-1)==1){
                binding.signUpBtn.text="Update Profile"

                binding.name.alpha=0f
                binding.name.isEnabled=false
                binding.email.alpha=0f
                binding.email.isEnabled=false
                binding.password.alpha=0f
                binding.password.isEnabled=false
                binding.login.alpha=0f
                binding.login.isEnabled=false
                Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
                    user=it.toObject<User>()!!
                    if(!user.image.isNullOrEmpty()){
                        Picasso.get().load(user.image).into(binding.profileImage)
                    }
//                    binding.name.editText?.setText(user.name)
//                    binding.email.editText?.setText(user.email)
//                    binding.password.editText?.setText(user.password)
                }
            }
        }

        binding.signUpBtn.setOnClickListener{
            if(intent.hasExtra("MODE")){
                if(intent.getIntExtra("MODE",-1)==1){
                    Firebase.firestore.collection(USER_NODE)
                        .document(Firebase.auth.currentUser!!.uid).set(user)
                        .addOnSuccessListener {
                            startActivity(Intent(this@SignUpActivity,HomeActivity::class.java))
                            finish()
                        }
                }
            }else{
                if(binding.name.editText?.text.toString().equals("") or
                    binding.email.editText?.text.toString().equals("") or
                    binding.password.editText?.text.toString().equals("")){
                    Toast.makeText(this@SignUpActivity,"Please Fill All The Above Information...",Toast.LENGTH_SHORT).show()
                }else{
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                        binding.email.editText?.text.toString(),
                        binding.password.editText?.text.toString()
                    ).addOnCompleteListener {
                            result->

                        if(result.isSuccessful){
                            user.name=binding.name.editText?.text.toString()
                            user.password=binding.password.editText?.text.toString()
                            user.email=binding.email.editText?.text.toString()
                            Firebase.firestore.collection(USER_NODE).
                            document(Firebase.auth.currentUser!!.uid).set(user)
                                .addOnSuccessListener {
                                    startActivity(Intent(this@SignUpActivity,HomeActivity::class.java))
                                    finish()
                                }

                        }else{
                            Toast.makeText(this@SignUpActivity,result.exception?.localizedMessage,Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }



        }

        binding.addImage.setOnClickListener{
            binding.signupProgressBarLoadingImage.alpha=1f
            launcher.launch("image/*")
        }

        binding.login.setOnClickListener {
            startActivity(Intent(this@SignUpActivity,LoginActivity::class.java))
        }
    }
}
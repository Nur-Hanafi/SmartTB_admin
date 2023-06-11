package com.hans.smartTB_admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hans.smartTB_admin.databinding.ActivityUserProfileBinding

class user_Profile : AppCompatActivity() {
    lateinit var foto : String
    lateinit var binding : ActivityUserProfileBinding
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //fetch data
        val bundle = intent.extras
        val emailuser = bundle?.getString("email")
        val name = bundle?.getString("name")
        foto = bundle?.getString("foto").toString()
        Glide.with(this)
            .load(foto)
            .into(binding.imageProfile)

        auth = FirebaseAuth.getInstance()
        val email = auth.currentUser?.email

        val Fstore = db.collection("admin").document(email!!)
        Fstore.get().addOnSuccessListener {
            binding.namaUser.text = name
            binding.emailUser.text = emailuser
            binding.nomorUser.text = it.getString("phone")
            val gender = it.getString("gender")
            binding.genderUser.text = gender
            if (gender != "Pria"){
                val drawableLeft = resources.getDrawable(R.drawable.baseline_woman_24, null)
                binding.genderUser.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null)
            }

        }

        binding.updateprofile.setOnClickListener {
            val intent = Intent (this, updateProfile::class.java)
                .putExtra("nama",name)
                .putExtra("phone", binding.nomorUser.text)
                .putExtra("gender", binding.genderUser.text)
                .putExtra("email", emailuser)
                .putExtra("foto", foto)
            startActivity(intent)
        }


    }
}
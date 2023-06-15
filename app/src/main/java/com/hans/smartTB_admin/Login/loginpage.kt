package com.hans.smartTB_admin.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hans.smartTB_admin.MainActivity
import com.hans.smartTB_admin.databinding.ActivityLoginpageBinding

class loginpage : AppCompatActivity() {

    private lateinit var binding: ActivityLoginpageBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginpageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.ForgetPassword.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            if (binding.edtEmailLogin.text != null)
                intent.putExtra("email", binding.edtEmailLogin.text.toString()) else
                intent.putExtra("email", "")
            startActivity(intent)
        }

        binding.tekslogo.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener{
            val email = binding.edtEmailLogin.text.toString().lowercase()
            val password = binding.edtPasswordLogin.text.toString()

            //Validasi Email
            if (email.isEmpty()) {
                binding.edtEmailLogin.error = "Email Tidak Boleh Kosong !"
                binding.edtEmailLogin.requestFocus()
                return@setOnClickListener
            }
            //Validasi Email Tidak Sesuai
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtEmailLogin.error = "Email Tidak Valid"
                binding.edtEmailLogin.requestFocus()
                return@setOnClickListener
            }
            //Validasi password
            if (password.isEmpty()) {
                binding.edtPasswordLogin.error = "Password Tidak Boleh Kosong !"
                binding.edtPasswordLogin.requestFocus()
                return@setOnClickListener
            }

            LoginFirebase(email,password)
        }

    }

    private fun LoginFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val usermail = auth.currentUser?.email!!.lowercase()
                    val db = FirebaseFirestore.getInstance()
                    val cekDoc = db.collection("admin").document(usermail!!)
                    cekDoc.get().addOnSuccessListener {

                        //mencocokkan sesi login dengan data user di firestore
                        if (it.getString("email") == auth.currentUser?.email){
                            val nama = it.getString("name")
                            Toast.makeText(this, "Selamat datang, $nama ", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java).also {
                                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                        }else
                        {
                            auth.signOut()
                            Toast.makeText(this, "DATA TIDAK DITEMUKAN, SILAHKAN LOGIN ULANG", Toast.LENGTH_SHORT).show()
                        }
                    }
                        .addOnFailureListener(){
                            Toast.makeText(this, "GAGAL MEMBACA DATA. SILAHKAN LOGIN ULANG", Toast.LENGTH_SHORT).show()

                        }
                } else {
                    Toast.makeText(this, "Email atau password salah, mohon periksa kembali", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null){
            val intent = Intent (this, MainActivity::class.java).also {
//                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }
}
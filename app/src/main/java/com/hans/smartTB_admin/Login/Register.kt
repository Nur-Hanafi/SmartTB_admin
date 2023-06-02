package com.hans.smartTB_admin.Login

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.RadioButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.hans.smartTB_admin.databinding.ActivityRegisterBinding
import java.io.ByteArrayOutputStream
import java.util.*

lateinit var binding : ActivityRegisterBinding
lateinit var auth : FirebaseAuth
lateinit var firestore: FirebaseFirestore

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //inisialisasi firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, loginpage::class.java)
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener {
            val bitmap = (binding.fotobawaan.getDrawable() as BitmapDrawable).getBitmap()
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val data = baos.toByteArray()

            val email = binding.edtEmailRegister.text.toString().lowercase()
            val password = binding.edtPasswordRegister.text.toString()
            val repassword = binding.edtPassword2Register.text.toString()
            val nama = binding.edtNamaRegister.text.toString()
            val phone = binding.edtNomorhpRegister.text.toString()

            val cekGender = findViewById<RadioButton>(binding.rgGender.checkedRadioButtonId)
            val hasilGender = "${cekGender.text}"


            //Validasi Email
            if (email.isEmpty()) {
                binding.edtEmailRegister.error = "Email Harus Di isi"
                binding.edtEmailRegister.requestFocus()
                return@setOnClickListener
            }
            //Validasi Email Tidak Sesuai
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtEmailRegister.error = "Email Tidak Valid"
                binding.edtEmailRegister.requestFocus()
                return@setOnClickListener
            }
            //Validasi password
            if (password.isEmpty()) {
                binding.edtPasswordRegister.error = "Password Harus Diisi"
                binding.edtPasswordRegister.requestFocus()
                return@setOnClickListener
            }

            //Validasi panjang password
            if (password.length < 6) {
                binding.edtPasswordRegister.error = "Password Minimal 6 Karakter"
                binding.edtPasswordRegister.requestFocus()
                return@setOnClickListener
            }

            if (password != repassword) {
                binding.edtPasswordRegister.error = "Password Tidak Sama!!"
                binding.edtPasswordRegister.requestFocus()
                return@setOnClickListener
            }

            //Upload foto ke firebase
            val storage = FirebaseStorage.getInstance()
            val reference = storage.getReference("images_user").child("IMG"+ Date().time +".jpeg")
            var uploadTask = reference.putBytes(data)
            uploadTask.addOnFailureListener {
                Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener { taskSnapshot ->
                if(taskSnapshot.metadata !=null){
                    if(taskSnapshot.metadata!!.reference !=null){
                        taskSnapshot.metadata!!.reference!!.downloadUrl.addOnCompleteListener {
                            //setelah berhasil upload foto, upload data user
                            var foto = it.getResult().toString()
                            RegisterFirebase(email,password, nama, phone, hasilGender, foto)
                        }
                    }else{
                        Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                }
            }


        }

    }

    private fun RegisterFirebase(email: String, password: String, nama: String, phone: String, hasilGender: String, foto : String) {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
//                    Toast.makeText(this, "Register Berhasil", Toast.LENGTH_SHORT).show()
                    val user = hashMapOf<String, Any>(
                        "email" to email,
                        "name" to nama,
                        "phone" to phone,
                        "gender" to hasilGender,
                        "foto" to foto,
                    )
                    val email = auth.currentUser?.email!!.lowercase()
                    firestore.collection("admin").document(email!!)
                        .set(user)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(this, "Register Berhasil", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener { exception ->
                            Log.w(ContentValues.TAG, "Error adding document $exception")
                        }
                    val intent = Intent (this, loginpage::class.java)
                    auth.signOut()
                    startActivity(intent)
                }else{
                    Toast.makeText(this,"${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
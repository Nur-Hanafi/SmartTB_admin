package com.hans.smartTB_admin

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.hans.smartTB_admin.databinding.ActivityDetailRiwayatBinding

class detailRiwayat : AppCompatActivity() {
    private lateinit var binding : ActivityDetailRiwayatBinding
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailRiwayatBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        val bundle = intent.extras
        val docID = bundle?.getString("docID").toString()
        val tanggal = bundle?.getString("tanggal")
        val total = bundle?.getString("total")
        firestore.collection("riwayat").document(docID).get().addOnSuccessListener{
            val status = it.getString("status").toString()
            binding.tvStatus.text = status
            // Tambahkan setOnTouchListener ke TextView untuk menampilkan drawable & cardview jika belum lunas
            if (status != "TERBAYARKAN"){
                binding.tvStatus.setOnClickListener {
                    binding.cvStatus.visibility = View.VISIBLE
                    binding.checkBox.isChecked = false
                }
            }else{
                binding.tvStatus.setOnClickListener {
                    binding.cvStatus.visibility = View.VISIBLE }
                binding.checkBox.isChecked = true
                binding.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }

        binding.tanggalInput.text = "Tanggal Input: $tanggal"
        binding.namaNasabah.text = bundle?.getString("nama")
        binding.totalHarga.text = total

        //ambil data riwayat
        firestore.collection("riwayat").document(docID).collection("detailRiwayat").document("detail").get()
            .addOnSuccessListener {
                binding.jumlahBotolK.text = it.getDouble("jumlahBotolKaca")?.toInt().toString()
                binding.jumlahBotolP.text = it.getDouble("jumlahBotolPlastik")?.toInt().toString()
                binding.jumlahDuplex.text = it.getDouble("jumlahDuplex")?.toInt().toString()
                binding.jumlahKardus.text = it.getDouble("jumlahKardus")?.toInt().toString()
                binding.jumlahLogam.text = it.getDouble("jumlahLogam")?.toInt().toString()

                binding.hargaBotolK.text = it.getDouble("hargaBotolK")?.toInt().toString()
                binding.hargaBotolP.text = it.getDouble("hargaBotolP")?.toInt().toString()
                binding.hargaDuplex.text = it.getDouble("hargaDuplex")?.toInt().toString()
                binding.hargaKardus.text = it.getDouble("hargaKardus")?.toInt().toString()
                binding.hargaLogam.text = it.getDouble("hargaLogam")?.toInt().toString()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }

        //update status
        //operasi dalam cardview
        binding.btnConfirm.setOnClickListener {
            var dataStatus = hashMapOf<String, Any>()
            if (binding.checkBox.isChecked){
                dataStatus = hashMapOf("status" to "TERBAYARKAN")
            }else dataStatus = hashMapOf("status" to "BELUM DIBAYAR")

            firestore.collection("riwayat").document(docID).update(dataStatus)
                .addOnSuccessListener {
                    Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show()
                    binding.cvStatus.visibility = View.GONE
                    recreate()
                }
        }

        binding.btnCancel.setOnClickListener {
            binding.cvStatus.visibility = View.GONE
        }


    }
}
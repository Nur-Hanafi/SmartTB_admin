package com.hans.smartTB_admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        val bundle = intent.extras
        val docID = bundle?.getString("docID").toString()
        val tanggal = bundle?.getString("tanggal")

        binding.tanggalInput.text = "Tanggal Input: $tanggal"
        binding.namaNasabah.text = bundle?.getString("nama")

        firestore = FirebaseFirestore.getInstance()
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


    }
}
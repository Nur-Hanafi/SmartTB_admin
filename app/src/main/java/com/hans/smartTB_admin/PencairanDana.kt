package com.hans.smartTB_admin

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.ControlsProviderService
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.R
import com.hans.smartTB_admin.Adapter.RiwayatAdapter
import com.hans.smartTB_admin.Model.dataRiwayat
import com.hans.smartTB_admin.databinding.ActivityPencairanDanaBinding

class PencairanDana : AppCompatActivity() {
    private lateinit var binding: ActivityPencairanDanaBinding
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPencairanDanaBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        binding.toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.recyclerViewPencairan.apply {
            layoutManager = LinearLayoutManager(context)

        }

        //searchview
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Tambahkan logika untuk mencari data di Firestore ketika tombol search ditekan
                firestore.collection("users")
                    .whereEqualTo("email", query)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (task.result.isEmpty)
                                Toast.makeText(this@PencairanDana,"Email Tidak ditemukan, coba lagi", Toast.LENGTH_SHORT).show()
                            else
                            {
                                for (document in task.result!!)
                                {
                                    binding.recyclerViewPencairan.visibility=View.VISIBLE
                                    binding.tvJudulRiwayat.visibility = View.VISIBLE
                                    binding.tabelNama.visibility = View.VISIBLE
                                    val email = document.getString("email")
                                    binding.namaNasabah.text = document.getString("name")
                                    riwayatPenyetoran(email.toString())
                                }
                            }
                        } else {
                            Log.w(ControlsProviderService.TAG, "Data tidak ditemukan")
                        }
                    }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Tambahkan logika untuk mencari data di Firestore ketika teks pada SearchView beruba
                binding.recyclerViewPencairan.visibility = View.GONE
                binding.tvJudulRiwayat.visibility = View.GONE
                binding.tabelNama.visibility = View.GONE
                return false
            }
        })
    }

    private fun riwayatPenyetoran(email:String) {
        val listRiwayat = FirebaseFirestore.getInstance().collection("riwayat").whereEqualTo("email", email)
        listRiwayat.addSnapshotListener { snapshots, error ->
            if (error != null) {
                // Jika terjadi error pada listener
                return@addSnapshotListener
            }

            // Jika tidak ada error, kita cek apakah snapshot berisi data
            if (snapshots != null && !snapshots.isEmpty) {
                val riwayat = snapshots.toObjects(dataRiwayat::class.java)
                Log.d("Berhasil", "$riwayat")
                (binding.recyclerViewPencairan.adapter as? RiwayatAdapter)?.clearData()
                binding.recyclerViewPencairan.adapter = this.let { RiwayatAdapter(it, riwayat) }
            }else {
                (binding.recyclerViewPencairan.adapter as? RiwayatAdapter)?.clearData()
                binding.recyclerViewPencairan.visibility = View.GONE
            }
        }
    }
}
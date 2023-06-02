package com.hans.smartTB_admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import com.hans.smartTB_admin.databinding.ActivityHargaSampahBinding
import java.text.SimpleDateFormat
import java.util.Locale

class hargaSampah : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    lateinit var binding: ActivityHargaSampahBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityHargaSampahBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        firestore.collection("sampah").document("dataHarga").get()
            .addOnSuccessListener{
                val waktu = it.getTimestamp("tanggal")
                binding.hargaBotolK.text = it.getDouble("botolKaca")?.toInt().toString()
                binding.hargaBotolP.text = it.getDouble("botolPlastik")?.toInt().toString()
                binding.hargaDuplex.text = it.getDouble("duplex")?.toInt().toString()
                binding.hargaKardus.text = it.getDouble("kardus")?.toInt().toString()
                binding.hargaLogam.text = it.getDouble("logam")?.toInt().toString()

                // Menampilkan timestamp pada TextView
                val formattedTimestamp = formatTimestamp(waktu)
                binding.tanggalUpdate.text = "Harga Terbaru Per: $formattedTimestamp"
            }


    }

    private fun formatTimestamp(timestamp: com.google.firebase.Timestamp?): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        return if (timestamp != null) {
            val date = timestamp.toDate()
            sdf.format(date)
        } else {
            ""
        }
    }
}
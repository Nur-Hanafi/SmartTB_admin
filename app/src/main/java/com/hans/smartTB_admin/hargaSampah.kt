package com.hans.smartTB_admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.ContentInfoCompat.Flags
import com.google.firebase.firestore.FieldValue
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

        binding.toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        firestore.collection("sampah").document("dataHarga").get()
            .addOnSuccessListener{
                val waktu = it.getTimestamp("tanggal")
                val hargaBotolK = it.getDouble("botolKaca")?.toInt().toString()
                val hargaBotolP = it.getDouble("botolPlastik")?.toInt().toString()
                val hargaDuplex = it.getDouble("duplex")?.toInt().toString()
                val hargaLogam = it.getDouble("logam")?.toInt().toString()
                val hargaKardus = it.getDouble("kardus")?.toInt().toString()
                //memasukkan data pada tabel utama
                binding.hargaBotolK.text = hargaBotolK
                binding.hargaBotolP.text = hargaBotolP
                binding.hargaDuplex.text = hargaDuplex
                binding.hargaKardus.text = hargaKardus
                binding.hargaLogam.text = hargaLogam

                //menampilkan data sebagai harga lama di cardview popup
                binding.hargaBotolKLama.text = hargaBotolK
                binding.hargaBotolPLama.text = hargaBotolP
                binding.hargaDuplexLama.text = hargaDuplex
                binding.hargaKardusLama.text = hargaKardus
                binding.hargaLogamLama.text = hargaLogam

                // Menampilkan timestamp pada TextView
                val formattedTimestamp = formatTimestamp(waktu)
                binding.tanggalUpdate.text = "Harga Terbaru Per: $formattedTimestamp"
            }

        binding.ubahHarga.setOnClickListener {
            binding.tabelUpdate.visibility = View.VISIBLE
        }

        //membuat filter input di editText
        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (source[i] == ',' || source[i] == '.') {
                    return@InputFilter ""
                }
            }
            null
        }

        binding.simpanData.setOnClickListener {
            var hargaBotolKacaUpdate: Int
            var hargaBotolPlastikUpdate: Int
            var hargaDuplexUpdate: Int
            var hargaKardusUpdate: Int
            var hargaLogamUpdate: Int

            if (binding.etHargaKardusUpdate.text.isNotEmpty() && binding.etHargaKardusUpdate.text.first() != '.' ){
                if(binding.etHargaKardusUpdate.text.contains('.'))
                    binding.etHargaKardusUpdate.filters = arrayOf(filter)
                hargaKardusUpdate = binding.etHargaKardusUpdate.text.toString().toInt()
            }else hargaKardusUpdate = binding.hargaKardusLama.text.toString().toInt()

            if (binding.etHargaDuplexUpdate.text.isNotEmpty() && binding.etHargaDuplexUpdate.text.first() != '.' ){
                if(binding.etHargaKardusUpdate.text.contains(','))
                    binding.etHargaDuplexUpdate.filters = arrayOf(filter)
                hargaDuplexUpdate = binding.etHargaDuplexUpdate.text.toString().toInt()
            }else hargaDuplexUpdate = binding.hargaDuplexLama.text.toString().toInt()

            if (binding.etHargaBotolKUpdate.text.isNotEmpty() && binding.etHargaBotolKUpdate.text.first() != '.'){
                if(binding.etHargaBotolKUpdate.text.contains(','))
                    binding.etHargaBotolKUpdate.filters = arrayOf(filter)
                hargaBotolKacaUpdate = binding.etHargaBotolKUpdate.text.toString().toInt()
            }else hargaBotolKacaUpdate = binding.hargaBotolKLama.text.toString().toInt()

            if (binding.etHargaBotolPUpdate.text.isNotEmpty() && binding.etHargaBotolPUpdate.text.first() != '.'){
                if (binding.etHargaBotolPUpdate.text.contains(','))
                    binding.etHargaBotolPUpdate.filters = arrayOf(filter)
                hargaBotolPlastikUpdate = binding.etHargaBotolPUpdate.text.toString().toInt()
            }else hargaBotolPlastikUpdate = binding.hargaBotolPLama.text.toString().toInt()

            if (binding.etHargaLogamUpdate.text.isNotEmpty() && binding.etHargaLogamUpdate.text.first() != '.'){
                if (binding.etHargaLogamUpdate.text.contains(','))
                    binding.etHargaLogamUpdate.filters = arrayOf(filter)
                hargaLogamUpdate = binding.etHargaLogamUpdate.text.toString().toInt()
            }else hargaLogamUpdate = binding.hargaLogamLama.text.toString().toInt()

            //upload data
            updateData(hargaKardusUpdate, hargaDuplexUpdate, hargaBotolKacaUpdate, hargaBotolPlastikUpdate, hargaLogamUpdate)

            binding.tabelUpdate.visibility = View.GONE
        }

        binding.btnBatal.setOnClickListener {
            binding.tabelUpdate.visibility = View.GONE
        }



    }

    private fun updateData(hargaKardusUpdate: Int, hargaDuplexUpdate: Int, hargaBotolKacaUpdate: Int, hargaBotolPlastikUpdate: Int, hargaLogamUpdate: Int) {
        val  dataStatus = hashMapOf(
            "tanggal" to FieldValue.serverTimestamp(),
            "botolKaca" to hargaBotolKacaUpdate,
            "botolPlastik" to hargaBotolPlastikUpdate,
            "duplex" to hargaDuplexUpdate,
            "kardus" to hargaKardusUpdate,
            "logam" to hargaLogamUpdate,
        )

        val db = firestore.collection("sampah").document("dataHarga")
        //mengambil data sampah saat ini
            db.get()
            .addOnSuccessListener {
                val data = it.data
                if (data != null) {
                    //membuat salinan harga saat ini dalam dokumen lain
                    firestore.collection("sampah").document(System.currentTimeMillis().toString()).set(data)
                        .addOnSuccessListener {
                            //memperbarui data harga saat ini
                            db.update(dataStatus)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Harga Sampah Berhasil Diperbarui", Toast.LENGTH_SHORT).show()
                                    recreate()
                                }
                        }.addOnFailureListener { Log.e("ERROR UPDATE HARGA", it.message.toString()) }
                }
            }
            .addOnFailureListener { Log.e("GAGAL MENDAPATKAN DATA HARGA DARI DB", it.message.toString()) }
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
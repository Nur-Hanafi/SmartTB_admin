package com.hans.smartTB_admin

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.hans.smartTB_admin.databinding.ActivityInputDataPenimbanganBinding
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import java.lang.reflect.Field
import java.util.Date
import kotlin.time.Duration.Companion.microseconds


class inputDataPenimbangan : AppCompatActivity() {
    private lateinit var binding: ActivityInputDataPenimbanganBinding
    private lateinit var firestore: FirebaseFirestore
    private var hargaBotolK:Double=0.0
    private var hargaBotolP:Double=0.0
    private var hargaDuplex:Double=0.0
    private var hargaKardus:Double=0.0
    private var hargaLogam:Double=0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityInputDataPenimbanganBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        binding.toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
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
                                Toast.makeText(this@inputDataPenimbangan,"Email Tidak ditemukan, coba lagi", Toast.LENGTH_SHORT).show()
                            else
                            {
                                for (document in task.result!!)
                                {
                                    binding.emailNasabah.text = document.id
                                    binding.namaNasabah.text = document.getString("name")
                                    binding.tanggalUpdate.visibility = View.VISIBLE
                                    binding.tabelInput.visibility = View.VISIBLE
                                    cekHargaSampah()

                                    //// Panggil fungsi hitungTotal() ketika teks pada EditText berubah
                                    binding.etJumlahKardus.addTextChangedListener(object : TextWatcher {
                                        override fun afterTextChanged(s: Editable?) {
                                            hitungTotal() }
                                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                                    })
                                    binding.etJumlahBotolK.addTextChangedListener(object : TextWatcher {
                                        override fun afterTextChanged(s: Editable?) {
                                            hitungTotal() }
                                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                                    })
                                    binding.etJumlahBotolP.addTextChangedListener(object : TextWatcher {
                                        override fun afterTextChanged(s: Editable?) {
                                            hitungTotal() }
                                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                                    })
                                    binding.etJumlahDuplex.addTextChangedListener(object : TextWatcher {
                                        override fun afterTextChanged(s: Editable?) {
                                            hitungTotal() }
                                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                                    })
                                    binding.etJumlahLogam.addTextChangedListener(object : TextWatcher {
                                        override fun afterTextChanged(s: Editable?) {
                                            hitungTotal() }
                                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                                    })

                                    binding.simpanData.setOnClickListener {
                                        ShowConfirmationDialog()
                                    }
                                }
                            }
                        } else {
                            Log.w(TAG, "Data tidak ditemukan")
                        }
                    }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Tambahkan logika untuk mencari data di Firestore ketika teks pada SearchView beruba
                binding.tanggalUpdate.visibility = View.GONE
                binding.tabelInput.visibility = View.GONE
                return true
            }
        })


    }

    private fun cekHargaSampah() {
        //cek harga sampah
        firestore.collection("sampah").document("dataHarga").get()
            .addOnSuccessListener{
                val waktu = it.getTimestamp("tanggal")
                hargaBotolK = it.getDouble("botolKaca")!!.toDouble()
                hargaBotolP = it.getDouble("botolPlastik")!!.toDouble()
                hargaDuplex = it.getDouble("duplex")!!.toDouble()
                hargaKardus = it.getDouble("kardus")!!.toDouble()
                hargaLogam = it.getDouble("logam")!!.toDouble()

                //mengatur tampilan harga
                binding.hargaBotolK.text = hargaBotolK.toInt().toString()
                binding.hargaBotolP.text = hargaBotolP.toInt().toString()
                binding.hargaDuplex.text = hargaDuplex.toInt().toString()
                binding.hargaKardus.text =hargaKardus.toInt().toString()
                binding.hargaLogam.text = hargaLogam.toInt().toString()
                // Menampilkan timestamp pada TextView
                val formattedTimestamp = formatTimestamp(waktu)
                binding.tanggalUpdate.text = "Harga Terbaru Per: $formattedTimestamp"
            }

    }

    private fun hitungTotal() {
        val totalKardus =  if (binding.etJumlahKardus.text.toString().isNotEmpty() && binding.etJumlahKardus.text.first() != '.') {
            binding.etJumlahKardus.text.toString().toDouble() * hargaKardus
        } else {
            0.0
        }
        val totalBotolP =  if (binding.etJumlahBotolP.text.toString().isNotEmpty() && binding.etJumlahBotolP.text.first() != '.') {
            binding.etJumlahBotolP.text.toString().toDouble() * hargaBotolP
        } else {
            0.0
        }
        val totalBotolK =  if (binding.etJumlahBotolK.text.toString().isNotEmpty()&& binding.etJumlahBotolK.text.first() != '.') {
            binding.etJumlahBotolK.text.toString().toDouble() * hargaBotolK
        } else {
            0.0
        }
        val totalDuplex =  if (binding.etJumlahDuplex.text.toString().isNotEmpty()&& binding.etJumlahDuplex.text.first() != '.') {
            binding.etJumlahDuplex.text.toString().toDouble() * hargaDuplex
        } else {
            0.0
        }
        val totalLogam = if (binding.etJumlahLogam.text.toString().isNotEmpty() && binding.etJumlahLogam.text.first() != '.') {
            binding.etJumlahLogam.text.toString().toDouble() * hargaLogam
        } else {
            0.0
        }
        val total = totalBotolK + totalDuplex + totalLogam + totalBotolP + totalKardus
        binding.totalHarga.text = total.toInt().toString()
    }

    private fun uploadData() {
        val tanggal = System.currentTimeMillis().toString()
        val data = hashMapOf<String, Any>(
            "email" to binding.emailNasabah.text.toString(),
            "namaNasabah" to binding.namaNasabah.text.toString(),
            "pendapatan" to binding.totalHarga.text.toString(),
            "status" to "BELUM DIBAYAR",
            "tanggal" to FieldValue.serverTimestamp()
        )
        firestore.collection("riwayat").document(tanggal).set(data)
            .addOnSuccessListener {
                var jumlahBotolKaca = 0.0
                var jumlahBotolPlastik = 0.0
                var jumlahKardus = 0.0
                var jumlahDuplex = 0.0
                var jumlahLogam = 0.0

                if (binding.etJumlahKardus.text.isNotEmpty() && binding.etJumlahKardus.text.first() != '.'){
                    jumlahKardus = binding.etJumlahKardus.text.toString().toDouble()
                }
                if (binding.etJumlahDuplex.text.isNotEmpty() && binding.etJumlahDuplex.text.first() != '.'){
                    jumlahDuplex = binding.etJumlahDuplex.text.toString().toDouble()
                }
                if (binding.etJumlahBotolK.text.isNotEmpty() && binding.etJumlahBotolK.text.first() != '.'){
                    jumlahBotolKaca = binding.etJumlahBotolK.text.toString().toDouble()
                }
                if (binding.etJumlahBotolP.text.isNotEmpty() && binding.etJumlahBotolP.text.first() != '.'){
                    jumlahBotolPlastik = binding.etJumlahBotolP.text.toString().toDouble()
                }
                if (binding.etJumlahLogam.text.isNotEmpty() && binding.etJumlahLogam.text.first() != '.'){
                    jumlahLogam= binding.etJumlahLogam.text.toString().toDouble()
                }



                val detail = hashMapOf<String, Any>(
                    "jumlahBotolKaca" to jumlahBotolKaca,
                    "jumlahBotolPlastik" to jumlahBotolPlastik,
                    "jumlahDuplex" to jumlahDuplex,
                    "jumlahKardus" to jumlahKardus,
                    "jumlahLogam" to jumlahLogam,
                    "hargaBotolK" to binding.hargaBotolK.text.toString().toDouble(),
                    "hargaBotolP" to binding.hargaBotolP.text.toString().toDouble(),
                    "hargaDuplex" to binding.hargaDuplex.text.toString().toDouble(),
                    "hargaKardus" to binding.hargaKardus.text.toString().toDouble(),
                    "hargaLogam" to binding.hargaLogam.text.toString().toDouble(),
                )
                firestore.collection("riwayat").document(tanggal.toString()).collection("detailRiwayat").document("detail")
                    .set(detail)
                    .addOnSuccessListener {
                        Toast.makeText(this,"Data Berhasil Disimpan", Toast.LENGTH_SHORT).show()
                        binding.etJumlahKardus.setText("")
                        binding.etJumlahDuplex.setText("")
                        binding.etJumlahBotolK.setText("")
                        binding.etJumlahBotolP.setText("")
                        binding.etJumlahLogam.setText("")
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("direct", "true")
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this,"Data Detail Gagal Disimpan $it", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this,"Data Gagal Disimpan $it", Toast.LENGTH_SHORT).show()
            }
    }

    private fun ShowConfirmationDialog() {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Konfirmasi Simpan Data")
        alertDialogBuilder.setMessage("Apakah Anda yakin menyimpan data?")

        val positiveText = "Simpan"
        val negativeText = "Batal"

        // Membuat SpannableString untuk menerapkan warna pada teks pilihan
        val spannablePositive = SpannableString(positiveText)
        spannablePositive.setSpan(
            ForegroundColorSpan(Color.parseColor("#42b752")),
            0,
            positiveText.length,
            0
        )

        val spannableNegative = SpannableString(negativeText)
        spannableNegative.setSpan(
            ForegroundColorSpan(Color.parseColor("#FF9800")),
            0,
            negativeText.length,
            0
        )

        alertDialogBuilder.setPositiveButton(spannablePositive) { dialog: DialogInterface, _: Int ->
            // Tindakan yang akan dilakukan jika tombol "Ya" ditekan
            uploadData()
        }

        alertDialogBuilder.setNegativeButton(spannableNegative) { dialog: DialogInterface, _: Int ->
            // Tindakan yang akan dilakukan jika tombol "Batal" ditekan
            dialog.dismiss()
        }

        // Membuat bentuk dengan radius 20dp dan background berwarna putih
        val radius = resources.getDimensionPixelSize(R.dimen.dialog_corner_radius).toFloat()
        val outerRadii = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        val shapeDrawable = ShapeDrawable(RoundRectShape(outerRadii, null, null))
        shapeDrawable.paint.color = Color.WHITE
        shapeDrawable.paint.style = Paint.Style.FILL

        // Membuat drawable dengan stroke berwarna hijau
        val strokeWidth = 10f
        val strokeColor = Color.parseColor("#42b752")
        val strokeDrawable = ShapeDrawable(RoundRectShape(outerRadii, null, null))
        strokeDrawable.paint.color = Color.TRANSPARENT
        strokeDrawable.paint.style = Paint.Style.STROKE
        strokeDrawable.paint.strokeWidth = strokeWidth
        strokeDrawable.paint.color = strokeColor

        // Menggabungkan background dan stroke menjadi satu drawable
        val layers: Array<Drawable> = arrayOf(shapeDrawable, strokeDrawable)
        val layerDrawable = LayerDrawable(layers)

        // Mengatur background drawable pada dialog
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(layerDrawable)
        alertDialog.show()
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
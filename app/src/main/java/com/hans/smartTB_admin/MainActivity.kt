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
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.hans.smartTB_admin.Fragment.riwayat
import com.hans.smartTB_admin.Login.loginpage
import com.hans.smartTB_admin.databinding.ActivityMainBinding


lateinit var binding : ActivityMainBinding
lateinit var auth: FirebaseAuth
private lateinit var foto : String
lateinit var database: FirebaseDatabase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Langkah 1: Menghubungkan aplikasi Android ke Realtime Database Firebase
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        fetchData()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //navbar
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> {
                    replaceFragment(Fragment())
                    hidefragment(riwayat())
                }
                R.id.riwayat -> {
                    replaceFragment(riwayat())
                }
                R.id.logout -> {
                    logout()
                }
            }
            true
        }

        //lihat profil
        binding.imageProfile.setOnClickListener{
            val intent = Intent(this, user_Profile::class.java)
                .putExtra("name", binding.tvUsername.text.toString().trim())
                .putExtra("email", binding.emailuser.text.toString().trim())
                .putExtra("foto", foto.trim())
            startActivity(intent)
        }

        //Harga Sampah
        val harga = findViewById<CardView>(R.id.cvHarga)
        harga.setOnClickListener {
            val intent = Intent(this@MainActivity, hargaSampah::class.java)
            startActivity(intent)
        }

        val kategori = findViewById<CardView>(R.id.cvKategori)
        kategori.setOnClickListener{
            val intent = Intent(this@MainActivity, jenisSampah::class.java)
            startActivity(intent)
        }

    }

    private fun hidefragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.hide(fragment)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.commit()
    }

    private fun logout() {
        showLogoutConfirmationDialog()
    }

    private fun showLogoutConfirmationDialog() {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Konfirmasi Keluar")
        alertDialogBuilder.setMessage("Apakah Anda yakin ingin log out?")

        val positiveText = "Logout"
        val neutralText = "Keluar Aplikasi"
        val negativeText = "Batal"

        // Membuat SpannableString untuk menerapkan warna pada teks pilihan
        val spannablePositive = SpannableString(positiveText)
        spannablePositive.setSpan(
            ForegroundColorSpan(Color.RED),
            0,
            positiveText.length,
            0
        )

        val spannableNeutral = SpannableString(neutralText)
        spannableNeutral.setSpan(
            ForegroundColorSpan(Color.parseColor("#FF9800")),
            0,
            neutralText.length,
            0
        )

        val spannableNegative = SpannableString(negativeText)
        spannableNegative.setSpan(
            ForegroundColorSpan(Color.parseColor("#42b752")),
            0,
            negativeText.length,
            0
        )

        alertDialogBuilder.setPositiveButton(spannablePositive) { dialog: DialogInterface, _: Int ->
            // Tindakan yang akan dilakukan jika tombol "Ya" ditekan
            auth.signOut()
            val intent = Intent(this, loginpage::class.java)
            startActivity(intent)
        }

        alertDialogBuilder.setNeutralButton(spannableNeutral) { dialog: DialogInterface, _: Int ->
            finishAffinity()
            dialog.dismiss()
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

    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.show(fragment)
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.commit()
    }

    private fun fetchData() {
        val email = auth.currentUser?.email!!.lowercase()
        val reference = database.getReference("Node")

        var Fstore = FirebaseFirestore.getInstance().collection("users").document(email!!)
        Fstore.get().addOnSuccessListener{
         if (it != null) {
             val name = it.getString("name")
             foto = it.getString("foto").toString()

             binding.tvUsername.text = name
             binding.emailuser.text = "Email : $email"

             Glide.with(this)
                 .load(foto)
                 .into(binding.imageProfile)
         }else{
             Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
         }
        }

        reference.orderByChild("Email").equalTo(email).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children)
                {
                    val node = snapshot.key
                    binding.tvNodeID.text = "Node ID : $node"

                    //cek kapasitas tempat sampah
                    val jarak = snapshot.child("jarak").getValue(String::class.java)?.toFloat()
                    val Maxsampah = 100
                    if (jarak != null && jarak <= Maxsampah)
                    {
                        val persentase = (Maxsampah - jarak!!).toInt()
                        binding.pbKapasitas.progress = persentase
                        binding.tvProgress.text = "$persentase%"
                        binding.tvKapasitas.text = "Kapasitas Terpakai : $persentase%"
                    }

                    //update GPS
                    val latt = snapshot.child("Lattitude").getValue(String::class.java)
                    val long = snapshot.child("Longitude").getValue(String::class.java)
                    if (latt != null && long != null){
                        binding.tvLattitude.text = "lattitude : $latt"
                        binding.tvLongitude.text = "longitude : $long"
                    }

                    //cek kapasitas baterai
                    val baterai = snapshot.child("Baterai").getValue(String::class.java)?.toFloat()
                    val persen = ((baterai!! - 3) / 1.2) * 100
                    binding.tvBaterai.text = "Baterai : ${persen?.toInt()}%"
                    binding.tvBateraibar.text = "${persen?.toInt()}%"
                    updateIconBaterai(persen.toFloat())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Error reading data from Realtime Database: " + databaseError.message)            }

        })
    }

    private fun updateIconBaterai(baterai: Float?) {
        if (baterai != null) {
            when
            {
                baterai <= 7 -> binding.ivBaterai.setImageResource(R.drawable.baseline_battery_0_bar_24)
                baterai <= 20 -> binding.ivBaterai.setImageResource(R.drawable.baseline_battery_1_bar_24)
                baterai <= 35 -> binding.ivBaterai.setImageResource(R.drawable.baseline_battery_2_bar_24)
                baterai <= 50 -> binding.ivBaterai.setImageResource(R.drawable.baseline_battery_3_bar_24)
                baterai <= 60 -> binding.ivBaterai.setImageResource(R.drawable.baseline_battery_4_bar_24)
                baterai <= 75 -> binding.ivBaterai.setImageResource(R.drawable.baseline_battery_5_bar_24)
                baterai <= 87 -> binding.ivBaterai.setImageResource(R.drawable.baseline_battery_6_bar_24)
                baterai > 87 -> binding.ivBaterai.setImageResource(R.drawable.baseline_battery_full_24)
            }
        }
    }

}
package com.hans.smartTB_admin

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.hans.smartTB_admin.Adapter.RecyclerNodeAdapter
import com.hans.smartTB_admin.Fragment.riwayat
import com.hans.smartTB_admin.Login.loginpage
import com.hans.smartTB_admin.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    lateinit var auth: FirebaseAuth
    private lateinit var foto : String
    private lateinit var database: FirebaseDatabase
    private lateinit var adapter: RecyclerNodeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        // Langkah 1: Menghubungkan aplikasi Android ke Database Firebase
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //cek intent
        val bundle = intent.extras
        if(bundle != null) {
            if (bundle?.getString("direct") == "true") {
                replaceFragment(riwayat())
            }
        }

        // Membuat adapter untuk Recyclerview
        adapter = RecyclerNodeAdapter()

        // Mengatur adapter ke GridView
        binding.recyclerNode.adapter = adapter

        //mengatur layout manager recyclerview menjadi 2 kolom
        val layoutManager= GridLayoutManager(this, 2)
        binding.recyclerNode.layoutManager = layoutManager

        val itemDecoration = object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildAdapterPosition(view)
                val column = position % 2
                if (column == 0) {
                    outRect.right = 10.dpToPx(parent.context)
                } else {
                    outRect.left = 10.dpToPx(parent.context)
                }
            }
        }
        binding.recyclerNode.addItemDecoration(itemDecoration)

        //mengambil data dari dB
        fetchData()
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

        val jemput = findViewById<CardView>(R.id.cvJemputSampah)
        jemput.setOnClickListener{
            val intent = Intent(this@MainActivity, jemputSampah::class.java)
            startActivity(intent)
        }

        val inputData = findViewById<CardView>(R.id.cvInputData)
        inputData.setOnClickListener{
            val intent = Intent(this@MainActivity, inputDataPenimbangan::class.java)
            startActivity(intent)
        }

    }

    private fun fetchData() {
        val email = auth.currentUser?.email!!.lowercase()
        val reference = database.getReference("Node")

        val Fstore = FirebaseFirestore.getInstance().collection("admin").document(email)
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

        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val id = dataSnapshot.key
                val jarak = dataSnapshot.child("jarak").getValue(String::class.java)
                val batt = dataSnapshot.child("jarak").getValue(String::class.java)
                adapter.add("$id, $jarak, $batt")
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // Handle changes to data
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                // Handle removal of data
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                // Handle movement of data
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
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

    }

fun Int.dpToPx(context: Context): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics).toInt()
}

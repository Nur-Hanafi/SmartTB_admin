package com.hans.smartTB_admin

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hans.smartTB_admin.Adapter.RecyclerJemput
import com.hans.smartTB_admin.Model.DCRecyclerNode
import com.hans.smartTB_admin.databinding.ActivityJemputSampahBinding

class jemputSampah : AppCompatActivity() {
    private lateinit var binding: ActivityJemputSampahBinding
    private lateinit var realtimeDB: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityJemputSampahBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        realtimeDB = FirebaseDatabase.getInstance()

        fetchData()

        binding.toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }


    }

    private fun fetchData() {

        val listNode = mutableListOf<DCRecyclerNode>()
        val adapter = RecyclerJemput(listNode)
        binding.recyclerJemputSampah.adapter = adapter
        binding.recyclerJemputSampah.layoutManager = LinearLayoutManager(this)


        realtimeDB.getReference("Node").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listNode.clear()
                for (data in snapshot.children)
                {
                    val NodeID = data.key
                    val Baterai = data.child("Baterai").getValue(String::class.java)
                    val Email = data.child("Email").getValue(String::class.java)
                    val Lattitude = data.child("Lattitude").getValue(String::class.java)
                    val Longitude = data.child("Longitude").getValue(String::class.java)
                    val jarak = data.child("jarak").getValue(String::class.java)
                    listNode.add(DCRecyclerNode(Baterai, Email, Lattitude, Longitude, jarak, NodeID))
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })

    }
}
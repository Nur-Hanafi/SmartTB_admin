package com.hans.smartTB_admin.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hans.smartTB_admin.Adapter.RiwayatAdapter
import com.hans.smartTB_admin.Model.dataRiwayat
import com.hans.smartTB_admin.databinding.FragmentRiwayatBinding


class riwayat : Fragment() {
    private lateinit var binding: FragmentRiwayatBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRiwayatBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewRiwayat.apply {
            layoutManager = LinearLayoutManager(context)

        }
        riwayatPesanan()
    }

    private fun riwayatPesanan() {
        val usermail = FirebaseAuth.getInstance().currentUser?.email
        val listPesananRiwayat = FirebaseFirestore.getInstance().collection("users").document(usermail.toString()).collection("riwayat")
        listPesananRiwayat.get()
            .addOnSuccessListener { documents ->
                for (document in documents)
                {
                    val riwayat = documents.toObjects(dataRiwayat::class.java)
                    binding.recyclerViewRiwayat.adapter = context?.let { RiwayatAdapter(it, riwayat) }

                }

            }
    }
}
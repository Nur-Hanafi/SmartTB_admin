package com.hans.smartTB_admin.Adapter

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.hans.smartTB_admin.Model.DCRecyclerNode
import com.hans.smartTB_admin.R
import com.hans.smartTB_admin.databinding.RecylerJemputsampahBinding

class RecyclerJemput(private var listNode: MutableList<DCRecyclerNode>) : RecyclerView.Adapter<RecyclerJemput.ViewHolder>() {

    class ViewHolder(val binding: RecylerJemputsampahBinding) : RecyclerView.ViewHolder(binding.root){
        fun initMapView(context: Context){
            binding.mapView.onCreate(null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecylerJemputsampahBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)
        holder.initMapView(parent.context)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val data = listNode[position]
        holder.binding.emailNasabah.isSelected = true
        holder.binding.emailNasabah.text = "${data.Email?.lowercase()}"
        holder.binding.tvNodeID.text = "Node ID: " + data.NodeID
        holder.binding.tvBaterai.text = "Baterai: ${data.Baterai}%"
        //seleksi kondisi biar ga null ygy
        if(data.Alamat != "null")
            holder.binding.alamatNasabah.text = "Alamat: \n"+data.Alamat
        else holder.binding.alamatNasabah.text = "Alamat: "
        Log.w("alamatRecycler", "alamat: ${data.Alamat}")

        val Maxsampah = 55
        val jarak = data.jarak?.toFloat()
        if (jarak != null && jarak <= Maxsampah)
        {
            var kapasitas = (((Maxsampah - jarak!!)/(Maxsampah-10))*100).toInt()
            when {
                kapasitas <= 1 -> kapasitas = 1
                kapasitas >100 -> kapasitas =100
            }
            holder.binding.tvKapasitas.text = "Kapasitas Terpakai: $kapasitas%"
            holder.binding.tvProgress.text = "$kapasitas%"
            holder.binding.pbKapasitas.progress=kapasitas
        }
        val latt = data.Lattitude
        val long = data.Longitude
        if (latt != null && long != null){
            holder.binding.tvLattitude.text = "lattitude : $latt"
            holder.binding.tvLongitude.text = "longitude : $long"
            holder.binding.mapView.getMapAsync {
                //menyesuaikan tema
                val isDarkTheme = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
                if (isDarkTheme) {
                    val mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_night)
                    it.setMapStyle(mapStyleOptions)
                } else {
                    it.setMapStyle(null)
                }
                //menambahkan titik
                Log.d("TAG", "Menampilkan marker pada peta")
                val koordinat = com.google.android.gms.maps.model.LatLng(latt.toDouble(), long.toDouble())
                it.addMarker(MarkerOptions().position(koordinat).title("${data.NodeID}"))
                it.moveCamera(CameraUpdateFactory.newLatLngZoom(koordinat, 15f))
            }
        }else holder.binding.mapView.visibility = View.GONE

        var baterai = data.Baterai?.toFloat()
        if (baterai != null) {
            if (baterai > 100) baterai = 100f
        }
        holder.binding.tvBaterai.text = "Baterai : ${baterai?.toInt()}%"
        holder.binding.tvBateraibar.text = "${baterai?.toInt()}%"
        updateIconBaterai(holder, baterai)
    }

    private fun updateIconBaterai(holder:ViewHolder, baterai: Float?) {
        if (baterai != null) {
            when
            {
                baterai <= 7 -> holder.binding.ivBaterai.setImageResource(R.drawable.baseline_battery_0_bar_24)
                baterai <= 20 -> holder.binding.ivBaterai.setImageResource(R.drawable.baseline_battery_1_bar_24)
                baterai <= 35 -> holder.binding.ivBaterai.setImageResource(R.drawable.baseline_battery_2_bar_24)
                baterai <= 50 -> holder.binding.ivBaterai.setImageResource(R.drawable.baseline_battery_3_bar_24)
                baterai <= 60 -> holder.binding.ivBaterai.setImageResource(R.drawable.baseline_battery_4_bar_24)
                baterai <= 75 -> holder.binding.ivBaterai.setImageResource(R.drawable.baseline_battery_5_bar_24)
                baterai <= 87 -> holder.binding.ivBaterai.setImageResource(R.drawable.baseline_battery_6_bar_24)
                baterai > 87 -> holder.binding.ivBaterai.setImageResource(R.drawable.baseline_battery_full_24)
            }
        }
    }

    override fun getItemCount(): Int {
        return listNode.size
    }
}
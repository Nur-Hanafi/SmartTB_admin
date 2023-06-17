package com.hans.smartTB_admin.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.hans.smartTB_admin.Model.dataRiwayat
import com.hans.smartTB_admin.R
import com.hans.smartTB_admin.detailRiwayat
import java.text.SimpleDateFormat
import java.util.Locale


class RiwayatAdapter(private val context: Context, private var ListRiwayat: MutableList<dataRiwayat>) : RecyclerView.Adapter<RiwayatAdapter.MyViewHolder>() {

    class MyViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {
        val namaNasabah :TextView = itemView.findViewById(R.id.recNamaNasabah)
        val tanggal : TextView  = itemView.findViewById(R.id.recTanggal)
        val status: TextView = itemView.findViewById(R.id.recstatus)
        val pendapatan : TextView = itemView.findViewById(R.id.recPendapatan)
        val email : TextView = itemView.findViewById(R.id.recEmail)
        val cardRiwayat: CardView = itemView.findViewById(R.id.recCardRiwayat)
        val docID : TextView = itemView.findViewById(R.id.docID)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val CardListRiwayat =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_riwayat, parent, false)
        return MyViewHolder(CardListRiwayat)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.namaNasabah.text = ListRiwayat[position].namaNasabah
        holder.email.text = ListRiwayat[position].email
        holder.pendapatan.text = ListRiwayat[position].pendapatan.toString()
        holder.status.text = ListRiwayat[position].status
        holder.docID.text = ListRiwayat[position].docID

        val formattedTimestamp = formatTimestamp(ListRiwayat[position].tanggal)
        holder.tanggal.text = "$formattedTimestamp"

        holder.cardRiwayat.setOnClickListener {
            val intent = Intent(context, detailRiwayat::class.java)
            intent.putExtra("docID", ListRiwayat[position].docID)
            intent.putExtra("tanggal", formattedTimestamp)
            intent.putExtra("nama", ListRiwayat[position].namaNasabah)
            context.startActivity(intent)
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

    override fun getItemCount(): Int {
        return ListRiwayat.size
    }

    fun clearData() {
        ListRiwayat.clear()
        notifyDataSetChanged()
    }
}
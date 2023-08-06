package com.hans.smartTB_admin.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hans.smartTB_admin.R
import com.hans.smartTB_admin.databinding.RecyclerItemBinding
import com.hans.smartTB_admin.jemputSampah


class RecyclerNodeAdapter : RecyclerView.Adapter<RecyclerNodeAdapter.ViewHolder>() {
    val items = mutableListOf<String>()

    class ViewHolder(val binding: RecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            val data = item.split(",")
            val id = data?.get(0)
            val jarak = data?.get(1)?.trim()?.toFloat()
            val Maxsampah = 55
            var persentase = (((Maxsampah - jarak!!)/(Maxsampah-10))*100).toInt()
            when {
                persentase <= 1 -> persentase = 1
                persentase >= 100 -> persentase = 100
            }


            var baterai = data?.get(2)?.trim()?.toFloat()
            if (baterai != null) {
                if (baterai >= 100) baterai = 100F
            }

            binding.tvBateraibar.text = "${baterai?.toInt()}%"
            binding.pbKapasitas.progress = persentase
            binding.tvProgress.text = "$persentase%"
            binding.tvNode.text = id
            updateIconBaterai(binding, baterai)

            binding.cvNodeItem.setOnClickListener{
                val intent = Intent(it.context, jemputSampah::class.java )
                it.context.startActivity(intent)
            }
        }

        private fun updateIconBaterai(itemBinding: RecyclerItemBinding,baterai: Float?) {
            if (baterai != null) {
                when
                {
                    baterai <= 7 -> itemBinding.ivBaterai.setImageResource(R.drawable.baseline_battery_0_bar_24)
                    baterai <= 20 -> itemBinding.ivBaterai.setImageResource(R.drawable.baseline_battery_1_bar_24)
                    baterai <= 35 -> itemBinding.ivBaterai.setImageResource(R.drawable.baseline_battery_2_bar_24)
                    baterai <= 50 -> itemBinding.ivBaterai.setImageResource(R.drawable.baseline_battery_3_bar_24)
                    baterai <= 60 -> itemBinding.ivBaterai.setImageResource(R.drawable.baseline_battery_4_bar_24)
                    baterai <= 75 -> itemBinding.ivBaterai.setImageResource(R.drawable.baseline_battery_5_bar_24)
                    baterai <= 87 -> itemBinding.ivBaterai.setImageResource(R.drawable.baseline_battery_6_bar_24)
                    baterai > 87 -> itemBinding.ivBaterai.setImageResource(R.drawable.baseline_battery_full_24)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun add(item: String) {
        items.add(item)
        notifyItemInserted(items.size - 1)
        notifyDataSetChanged()
    }
}
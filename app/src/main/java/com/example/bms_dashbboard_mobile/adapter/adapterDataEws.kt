package com.example.bms_dashbboard_mobile.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.bms_dashbboard_mobile.DetailEWS
import com.example.bms_dashbboard_mobile.R
import com.example.bms_dashbboard_mobile.dataclass.dataEws

class adapterDataEws (private val context: Context, private val detailewsList : ArrayList<dataEws>) : RecyclerView.Adapter<adapterDataEws.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): adapterDataEws.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fetch_overview, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: adapterDataEws.MyViewHolder, position: Int) {
        val currentItem = detailewsList[position]

        val getnamaews = currentItem.nama_ews
        val getalamat = currentItem.alamat
        val id = currentItem.id

        holder.nama.text = currentItem.nama_ews
        holder.alamat.text = currentItem.alamat

        holder.card.setOnClickListener {
            val intent = Intent(context, DetailEWS::class.java)
            intent.putExtra("id", id)
            intent.putExtra("nama_ews", getnamaews)
            intent.putExtra("alamat", getalamat)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return detailewsList.size
    }
    class MyViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){

        val nama : TextView = itemView.findViewById(R.id.txt_namaews)
        val alamat : TextView = itemView.findViewById(R.id.txt_alamatews)
        val card : CardView = itemView.findViewById(R.id.cardoverview)

    }
}

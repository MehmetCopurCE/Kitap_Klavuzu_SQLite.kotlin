package com.mehmetcopur.kitapklavuzusqlite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_row.view.*
import java.util.zip.Inflater

class ListeRecyclerAdapter(val kitapListesi :ArrayList<String>, val idListesi :ArrayList<Int>) : RecyclerView.Adapter<ListeRecyclerAdapter.KitapHolder>() {

    class KitapHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KitapHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_row,parent,false)
        return  KitapHolder(view)
    }

    override fun onBindViewHolder(holder: KitapHolder, position: Int) {
        holder.itemView.recycler_row_text.text = kitapListesi[position]
        holder.itemView.setOnClickListener {
            val action = KitapListDirections.actionKitapListToKitapIcerigi("recyclerdangeldim",idListesi[position])
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return kitapListesi.size
    }
}
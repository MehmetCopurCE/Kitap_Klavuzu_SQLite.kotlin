package com.mehmetcopur.kitapklavuzusqlite

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_kitap_icerigi.*
import kotlinx.android.synthetic.main.fragment_kitap_list.*


class KitapList : Fragment() {

    var kitapIsmiListesi = ArrayList<String>()
    var kitapIdListesi = ArrayList<Int>()
    private lateinit var listeAdapter : ListeRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kitap_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listeAdapter = ListeRecyclerAdapter(kitapIsmiListesi,kitapIdListesi)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = listeAdapter
        sqlVeriAlma()
    }


    fun sqlVeriAlma() {

        try {

            activity?.let {
                val database = it.openOrCreateDatabase("Kitaplar", Context.MODE_PRIVATE, null)


                val cursor = database.rawQuery("SELECT * FROM kitaplar", null)
                val kitapIsmiIndex = cursor.getColumnIndex("kitapismi")
                val kitapIdIndex = cursor.getColumnIndex("id")

                kitapIsmiListesi.clear()
                kitapIdListesi.clear()

                while (cursor.moveToNext()) {
                    kitapIsmiListesi.add(cursor.getString(kitapIsmiIndex))
                    kitapIdListesi.add(cursor.getInt(kitapIdIndex))

                }

                listeAdapter.notifyDataSetChanged() //Bu eğer yeni bir veri eklersek yemek gibi onu bize gösterecek
                cursor.close()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
package com.mehmetcopur.kitapklavuzusqlite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val manuInflater = menuInflater
        menuInflater.inflate(R.menu.kitap_ekle,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { //item a tıklandığında ne yapılacağıını yazacağız fakat her zzaman itemı kontrol etme alışkanlığımızın olması lazım

        if (item.itemId == R.id.kitap_ekle_item){
            val action = KitapListDirections.actionKitapListToKitapIcerigi("menudengeldim",0)   //argumanları girdikten sonra bizden buraddan arrtık onları isteyecek
            Navigation.findNavController(this,R.id.fragmentContainerView).navigate(action)  // MAİNDEKİNİN İD SİNİ EKLİYORUZ
        }
        return super.onOptionsItemSelected(item)
    }
}
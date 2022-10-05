package com.mehmetcopur.kitapklavuzusqlite

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_kitap_icerigi.*
import java.io.ByteArrayOutputStream


class KitapIcerigi : Fragment() {

    var secilenGorsel : Uri? =null
    var secilenBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kitap_icerigi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button.setOnClickListener {
            kaydet(it)
        }

        imageView.setOnClickListener {
            gorselSec(it)
        }

        arguments?.let {
            var gelenBilgi = KitapIcerigiArgs.fromBundle(it).bilgi
            if (gelenBilgi.equals("menudengeldim")){
                //yeni bir yemek eklemeye geldi
                kitapIsmiText.setText("")
                yazarIsmiText.setText("")
                button.visibility = View.VISIBLE  // buton görünür yaptık

                val gorselSecmeArkaPlani = BitmapFactory.decodeResource(context?.resources,R.drawable.gorselsecimi)
                imageView.setImageBitmap(gorselSecmeArkaPlani)   //gorsellerin aynı olmasını iistiyoruz
            }else{
                //daha önceden oluşturulan kitabı görnmeye geldi
                button.visibility =View.INVISIBLE    //butonu gizledim

                val secilenId = KitapIcerigiArgs.fromBundle(it).id

                context?.let {
                    try {

                        val db = it.openOrCreateDatabase("Kitaplar",Context.MODE_PRIVATE,null)
                        val cursor = db.rawQuery("SELECT * FROM kitaplar WHERE id = ?", arrayOf(secilenId.toString())) //id yi string istediğinden ona cevirdik

                        val kitapIsmiIndex = cursor.getColumnIndex("kitapismi")
                        val kitapIcerigiIndex = cursor.getColumnIndex("kitapicerigi")
                        val kitapGorseli = cursor.getColumnIndex("gorsel")

                        while (cursor.moveToNext()){
                            kitapIsmiText.setText(cursor.getString(kitapIsmiIndex))
                            yazarIsmiText.setText(cursor.getString(kitapIcerigiIndex))

                            val byteDizisi = cursor.getBlob(kitapGorseli)
                            val bitmap = BitmapFactory.decodeByteArray(byteDizisi,0,byteDizisi.size)
                            imageView.setImageBitmap(bitmap)
                        }
                        cursor.close()

                    }catch (e : Exception){
                        e.printStackTrace()
                    }
                }
            }
        }

    }

    fun kaydet(view: View) {
        val kitapIsmi = kitapIsmiText.text.toString()
        val kitapIcerigi = yazarIsmiText.text.toString()

        if (secilenBitmap != null){
            val kucukBitmap = kucukBitmapOlustur(secilenBitmap!!,300)

            val outputStream = ByteArrayOutputStream()        // görselleri veriye dönüştürüyorum
            kucukBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteDizisi = outputStream.toByteArray()


            try {
                context?.let {
                    val database = it.openOrCreateDatabase("Kitaplar", Context.MODE_PRIVATE,null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS kitaplar(id INTEGER PRIMARY KEY, kitapismi VARCAHR, kitapicerigi VARCHAR, gorsel BLOB)")

                    val sqlString = "INSERT INTO kitaplar(kitapismi, kitapicerigi, gorsel) VALUES (?,?,?)" //
                    val statement = database.compileStatement(sqlString)  //soru işaretleri yerlerine değer atamaya olanak sağlar.
                    statement.bindString(1,kitapIsmi)   //burada index 0'dan değil 1'den başlar
                    statement.bindString(2, kitapIcerigi)
                    statement.bindBlob(3,byteDizisi)     //gorsellerde bindBlob kullanılır.
                    statement.execute()
                }


            }catch (e : Exception){
                e.printStackTrace()
            }

            //Kaydetme işlemi bittikten sonra listeye dönmemizi sağlar
            val action = KitapIcerigiDirections.actionKitapIcerigiToKitapList()
            Navigation.findNavController(view).navigate(action)
        }
    }

    fun gorselSec(view: View){
        println("Gorsel tıklandı")

        activity?.let {
            if (ContextCompat.checkSelfPermission(it.applicationContext,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            // İzin verilmedi, İzin istememiz gerekiyor
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1) //Bu tam sayı bizim iznimizin kodu olacak ve ileride kullanacağız
            }else{
            // İzin zaten verilmiş izin istemeden galeriye git
                val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI) //Buradaki uri i ezberlememiz lazım
                startActivityForResult(galeriIntent, 2) //Bunu seçmemin ve sadece startActivity i seçmememin sebebi biz burada bir değer döndüreceğiz.
            }


        }

    }

    override fun onRequestPermissionsResult( //İSTENİLEN İZNİN SONUÇLARI
        requestCode: Int,                    //Requestcodde burada kullanılıyor
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode ==1){
            if (requestCode>1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //izni aldık
                val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI) // Galeriye gitnek için yukarıdan alıp kopyalıyoruz.
                startActivityForResult(galeriIntent, 2)

            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {  //Burada galeriye gidip ne yapacağımızı yazıyoruz.

        if (requestCode == 2 && resultCode == Activity.RESULT_OK  && data !=null) {
            secilenGorsel = data.data // Bu işlem görseli almamızı sağlayacak

            try {

                context?.let {
                    if (secilenGorsel != null){
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(it.contentResolver, secilenGorsel!!)
                            secilenBitmap = ImageDecoder.decodeBitmap(source)
                            imageView.setImageBitmap(secilenBitmap)
                        } else {
                            secilenBitmap = MediaStore.Images.Media.getBitmap(it.contentResolver, secilenGorsel)
                            imageView.setImageBitmap(secilenBitmap)
                        }
                    }

                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun kucukBitmapOlustur(kullanicininSectigiBitmap: Bitmap,maximumBoyut :Int) : Bitmap{ //görseli küçültme işlemi
        var width = kullanicininSectigiBitmap.width                                     // SQLite'a kaydedeceğimiz verilerin 1mg'dan büyük olması bize sıkıntı oluşturur. Şimdi oluşturmazsa ileride oluşturur.
        var height = kullanicininSectigiBitmap.height

        val bitmapOrani : Double = width.toDouble() / height.toDouble()   //resmi aynı oranda küçültmek istiyoruz.

        if (bitmapOrani > 1){
            // görselimiz yatay
            width = maximumBoyut
            val kisaltilmisHeight = width / bitmapOrani
            height = kisaltilmisHeight.toInt()
        }else{
            // gorselimiz Dikey
            height = maximumBoyut
            val kisaltilmisWidth = height * bitmapOrani
            width = kisaltilmisWidth.toInt()
        }
        return Bitmap.createScaledBitmap(kullanicininSectigiBitmap,width,height,true) //Burası gorseli daha küçük bir halde almamızı sağlıyor
     }
}

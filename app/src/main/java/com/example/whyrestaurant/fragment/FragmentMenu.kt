package com.example.whyrestaurant.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whyrestaurant.R
import com.example.whyrestaurant.adapter.ItemAdapter
import com.example.whyrestaurant.adapter.MenuAdapter
import com.example.whyrestaurant.model.ItemModel
import com.example.whyrestaurant.model.MenuModel
import com.example.whyrestaurant.server.Method
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class FragmentMenu: Fragment(), MenuAdapter.MenuClickListener {

    lateinit var menuAdapter: MenuAdapter
    lateinit var listMenu: RecyclerView
    val addMenu: MutableList<MenuModel> = ArrayList()

    lateinit var itemAdapter: ItemAdapter
    lateinit var listItem: RecyclerView
    val addItem: MutableList<ItemModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_menu, container, false)
        init(view)

        menuAdapter = MenuAdapter(requireContext(), this)
        itemAdapter = ItemAdapter(requireContext())
        setupRecycle()

        getMenu()
        getItemAyam()
        return view
    }

    fun init(view: View){
        listMenu = view.findViewById(R.id.listMenu)
        listItem = view.findViewById(R.id.listItem)
    }

    fun getMenu(){
        listMenu.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        listMenu.adapter = menuAdapter
        addMenu.add(MenuModel("Ayam"))
        addMenu.add(MenuModel("Cemilan"))
        addMenu.add(MenuModel("Daging Sapi"))
        addMenu.add(MenuModel("Happy Meal"))
        addMenu.add(MenuModel("Ikan"))
        addMenu.add(MenuModel("Makanan Penuntun"))
        addMenu.add(MenuModel("Minuman"))
        addMenu.add(MenuModel("Paket Family"))
        addMenu.add(MenuModel("Sarapan Pagi"))

        menuAdapter.setMenu(addMenu)
    }

    fun setupRecycle(){
        listItem.layoutManager = GridLayoutManager(activity, 2)

        listItem.adapter = itemAdapter
        itemAdapter.notifyDataSetChanged()
    }

    private fun getItemAyam(){
        val url = URL(Method.baseUrl + "/api/menu/category/Ayam")
        val handler = Handler(Looper.getMainLooper())
        Executors.newSingleThreadExecutor().execute(object: Runnable{
            override fun run() {
                var result = ""
                val client = url.openConnection() as HttpURLConnection
                client.requestMethod = "GET"

                try{
                    result = client.inputStream.bufferedReader().use { it.readText() }
                } catch (e: Exception){
                    result = client.errorStream.bufferedReader().use{it.readText()}
                }

                handler.post(object: Runnable{
                    override fun run() {
                        if(result != ""){
                            val jsonArray = JSONArray(result)

                            for (i in 0 until jsonArray.length()){
                                val item = jsonArray.getJSONObject(i)
                                val itemId = item.getString("id")
                                val itemName = item.getString("name")
                                val itemPrice = item.getString("price")

                                val imageUrl = URL(Method.baseUrl + "/api/menu/$itemId/photo")
                                val handler = Handler(Looper.getMainLooper())
                                Executors.newSingleThreadExecutor().execute(object: Runnable{
                                    override fun run() {
                                        var resultImage = ""
                                        val client = imageUrl.openConnection() as HttpURLConnection
                                        client.requestMethod = "GET"

                                        try{
                                            val inputStream: InputStream = client.inputStream

                                            // Mendapatkan bitmap dari input stream
                                            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

                                            // Mengonversi bitmap ke format base64
                                            val byteArrayOutputStream = ByteArrayOutputStream()
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                                            val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                                            val base64Image: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
                                            resultImage = base64Image
                                        } catch (e: Exception){
                                            if (client.errorStream != null) {
                                                result = client.errorStream.bufferedReader().use { it.readText() }
                                            } else {
                                                Log.e("Error", "An exception occurred: ${e.message}")
                                                // You might want to provide user feedback or handle the error in a more appropriate way
                                            }
                                        }

                                        handler.post(object: Runnable{
                                            override fun run() {
                                                if(resultImage != ""){
                                                    addItem.add(ItemModel(itemId,
                                                        itemName,
                                                        resultImage,
                                                        convertToRupiah(itemPrice))
                                                    )
                                                    itemAdapter.setItem(addItem)
                                                }
                                            }
                                        })
                                    }
                                })
                            }
                        }
                    }
                })
            }
        })
    }

    fun getItemCemilan() {
        addItem.clear()
        val url = URL(Method.baseUrl + "/api/menu/category/Camilan")
        val handler = Handler(Looper.getMainLooper())
        Executors.newSingleThreadExecutor().execute(object: Runnable{
            override fun run() {
                var result = ""
                val client = url.openConnection() as HttpURLConnection
                client.requestMethod = "GET"

                try{
                    result = client.inputStream.bufferedReader().use { it.readText() }
                } catch (e: Exception){
                    result = client.errorStream.bufferedReader().use{it.readText()}
                }

                handler.post(object: Runnable{
                    override fun run() {
                        if(result != ""){
                            val jsonArray = JSONArray(result)

                            for (i in 0 until jsonArray.length()){
                                val item = jsonArray.getJSONObject(i)
                                val itemId = item.getString("id")
                                val itemName = item.getString("name")
                                val itemPrice = item.getString("price")

                                val imageUrl = URL(Method.baseUrl + "/api/menu/$itemId/photo")
                                val handler = Handler(Looper.getMainLooper())
                                Executors.newSingleThreadExecutor().execute(object: Runnable{
                                    override fun run() {
                                        var resultImage = ""
                                        val client = imageUrl.openConnection() as HttpURLConnection
                                        client.requestMethod = "GET"

                                        try{
                                            val inputStream: InputStream = client.inputStream

                                            // Mendapatkan bitmap dari input stream
                                            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

                                            // Mengonversi bitmap ke format base64
                                            val byteArrayOutputStream = ByteArrayOutputStream()
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                                            val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                                            val base64Image: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
                                            resultImage = base64Image
                                        } catch (e: Exception){
                                            resultImage = client.errorStream.bufferedReader().use{it.readText()}
                                        }

                                        handler.post(object: Runnable{
                                            override fun run() {
                                                if(resultImage != ""){
                                                    addItem.add(ItemModel(itemId,
                                                        itemName,
                                                        resultImage,
                                                        convertToRupiah(itemPrice))
                                                    )
                                                    itemAdapter.setItem(addItem)
                                                }
                                            }
                                        })
                                    }
                                })
                            }
                        }
                    }
                })
            }
        })
        }

    fun getItemDagingSapi() {
        addItem.clear()
        val url = URL(Method.baseUrl + "/api/menu/category/DagingSapi")
        val handler = Handler(Looper.getMainLooper())
        Executors.newSingleThreadExecutor().execute(object: Runnable{
            override fun run() {
                var result = ""
                val client = url.openConnection() as HttpURLConnection
                client.requestMethod = "GET"

                try{
                    result = client.inputStream.bufferedReader().use { it.readText() }
                } catch (e: Exception){
                    result = client.errorStream.bufferedReader().use{it.readText()}
                }

                handler.post(object: Runnable{
                    override fun run() {
                        if(result != ""){
                            val jsonArray = JSONArray(result)

                            for (i in 0 until jsonArray.length()){
                                val item = jsonArray.getJSONObject(i)
                                val itemId = item.getString("id")
                                val itemName = item.getString("name")
                                val itemPrice = item.getString("price")

                                val imageUrl = URL(Method.baseUrl + "/api/menu/$itemId/photo")
                                val handler = Handler(Looper.getMainLooper())
                                Executors.newSingleThreadExecutor().execute(object: Runnable{
                                    override fun run() {
                                        var resultImage = ""
                                        val client = imageUrl.openConnection() as HttpURLConnection
                                        client.requestMethod = "GET"

                                        try{
                                            val inputStream: InputStream = client.inputStream

                                            // Mendapatkan bitmap dari input stream
                                            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

                                            // Mengonversi bitmap ke format base64
                                            val byteArrayOutputStream = ByteArrayOutputStream()
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                                            val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                                            val base64Image: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
                                            resultImage = base64Image
                                        } catch (e: Exception){
                                            resultImage = client.errorStream.bufferedReader().use{it.readText()}
                                        }

                                        handler.post(object: Runnable{
                                            override fun run() {
                                                if(resultImage != ""){
                                                    addItem.add(ItemModel(itemId,
                                                        itemName,
                                                        resultImage,
                                                        convertToRupiah(itemPrice))
                                                    )
                                                    itemAdapter.setItem(addItem)
                                                }
                                            }
                                        })
                                    }
                                })
                            }
                        }
                    }
                })
            }
        })
    }

    fun getItemHappyMeal() {
        addItem.clear()
        val url = URL(Method.baseUrl + "/api/menu/category/HappyMeal")
        val handler = Handler(Looper.getMainLooper())
        Executors.newSingleThreadExecutor().execute(object: Runnable{
            override fun run() {
                var result = ""
                val client = url.openConnection() as HttpURLConnection
                client.requestMethod = "GET"

                try{
                    result = client.inputStream.bufferedReader().use { it.readText() }
                } catch (e: Exception){
                    result = client.errorStream.bufferedReader().use{it.readText()}
                }

                handler.post(object: Runnable{
                    override fun run() {
                        if(result != ""){
                            val jsonArray = JSONArray(result)

                            for (i in 0 until jsonArray.length()){
                                val item = jsonArray.getJSONObject(i)
                                val itemId = item.getString("id")
                                val itemName = item.getString("name")
                                val itemPrice = item.getString("price")

                                val imageUrl = URL(Method.baseUrl + "/api/menu/$itemId/photo")
                                val handler = Handler(Looper.getMainLooper())
                                Executors.newSingleThreadExecutor().execute(object: Runnable{
                                    override fun run() {
                                        var resultImage = ""
                                        val client = imageUrl.openConnection() as HttpURLConnection
                                        client.requestMethod = "GET"

                                        try{
                                            val inputStream: InputStream = client.inputStream

                                            // Mendapatkan bitmap dari input stream
                                            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

                                            // Mengonversi bitmap ke format base64
                                            val byteArrayOutputStream = ByteArrayOutputStream()
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                                            val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                                            val base64Image: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
                                            resultImage = base64Image
                                        } catch (e: Exception){
                                            resultImage = client.errorStream.bufferedReader().use{it.readText()}
                                        }

                                        handler.post(object: Runnable{
                                            override fun run() {
                                                if(resultImage != ""){
                                                    addItem.add(ItemModel(itemId,
                                                        itemName,
                                                        resultImage,
                                                        convertToRupiah(itemPrice))
                                                    )
                                                    itemAdapter.setItem(addItem)
                                                }
                                            }
                                        })
                                    }
                                })
                            }
                        }
                    }
                })
            }
        })
    }

    fun getItemIkan() {
        addItem.clear()
        val url = URL(Method.baseUrl + "/api/menu/category/Ikan")
        val handler = Handler(Looper.getMainLooper())
        Executors.newSingleThreadExecutor().execute(object: Runnable{
            override fun run() {
                var result = ""
                val client = url.openConnection() as HttpURLConnection
                client.requestMethod = "GET"

                try{
                    result = client.inputStream.bufferedReader().use { it.readText() }
                } catch (e: Exception){
                    result = client.errorStream.bufferedReader().use{it.readText()}
                }

                handler.post(object: Runnable{
                    override fun run() {
                        if(result != ""){
                            val jsonArray = JSONArray(result)

                            for (i in 0 until jsonArray.length()){
                                val item = jsonArray.getJSONObject(i)
                                val itemId = item.getString("id")
                                val itemName = item.getString("name")
                                val itemPrice = item.getString("price")

                                val imageUrl = URL(Method.baseUrl + "/api/menu/$itemId/photo")
                                val handler = Handler(Looper.getMainLooper())
                                Executors.newSingleThreadExecutor().execute(object: Runnable{
                                    override fun run() {
                                        var resultImage = ""
                                        val client = imageUrl.openConnection() as HttpURLConnection
                                        client.requestMethod = "GET"

                                        try{
                                            val inputStream: InputStream = client.inputStream

                                            // Mendapatkan bitmap dari input stream
                                            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

                                            // Mengonversi bitmap ke format base64
                                            val byteArrayOutputStream = ByteArrayOutputStream()
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                                            val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                                            val base64Image: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
                                            resultImage = base64Image
                                        } catch (e: Exception){
                                            resultImage = client.errorStream.bufferedReader().use{it.readText()}
                                        }

                                        handler.post(object: Runnable{
                                            override fun run() {
                                                if(resultImage != ""){
                                                    addItem.add(ItemModel(itemId,
                                                        itemName,
                                                        resultImage,
                                                        convertToRupiah(itemPrice))
                                                    )
                                                    itemAdapter.setItem(addItem)
                                                }
                                            }
                                        })
                                    }
                                })
                            }
                        }
                    }
                })
            }
        })
    }

    fun getItemMakananPenuntun() {
        addItem.clear()
        val url = URL(Method.baseUrl + "/api/menu/category/MakananPenutup")
        val handler = Handler(Looper.getMainLooper())
        Executors.newSingleThreadExecutor().execute(object: Runnable{
            override fun run() {
                var result = ""
                val client = url.openConnection() as HttpURLConnection
                client.requestMethod = "GET"

                try{
                    result = client.inputStream.bufferedReader().use { it.readText() }
                } catch (e: Exception){
                    result = client.errorStream.bufferedReader().use{it.readText()}
                }

                handler.post(object: Runnable{
                    override fun run() {
                        if(result != ""){
                            val jsonArray = JSONArray(result)

                            for (i in 0 until jsonArray.length()){
                                val item = jsonArray.getJSONObject(i)
                                val itemId = item.getString("id")
                                val itemName = item.getString("name")
                                val itemPrice = item.getString("price")

                                val imageUrl = URL(Method.baseUrl + "/api/menu/$itemId/photo")
                                val handler = Handler(Looper.getMainLooper())
                                Executors.newSingleThreadExecutor().execute(object: Runnable{
                                    override fun run() {
                                        var resultImage = ""
                                        val client = imageUrl.openConnection() as HttpURLConnection
                                        client.requestMethod = "GET"

                                        try{
                                            val inputStream: InputStream = client.inputStream

                                            // Mendapatkan bitmap dari input stream
                                            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

                                            // Mengonversi bitmap ke format base64
                                            val byteArrayOutputStream = ByteArrayOutputStream()
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                                            val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                                            val base64Image: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
                                            resultImage = base64Image
                                        } catch (e: Exception){
                                            resultImage = client.errorStream.bufferedReader().use{it.readText()}
                                        }

                                        handler.post(object: Runnable{
                                            override fun run() {
                                                if(resultImage != ""){
                                                    addItem.add(ItemModel(itemId,
                                                        itemName,
                                                        resultImage,
                                                        convertToRupiah(itemPrice))
                                                    )
                                                    itemAdapter.setItem(addItem)
                                                }
                                            }
                                        })
                                    }
                                })
                            }
                        }
                    }
                })
            }
        })
    }

    fun getItemMinuman() {
        addItem.clear()
        val url = URL(Method.baseUrl + "/api/menu/category/Minuman")
        val handler = Handler(Looper.getMainLooper())
        Executors.newSingleThreadExecutor().execute(object: Runnable{
            override fun run() {
                var result = ""
                val client = url.openConnection() as HttpURLConnection
                client.requestMethod = "GET"

                try{
                    result = client.inputStream.bufferedReader().use { it.readText() }
                } catch (e: Exception){
                    result = client.errorStream.bufferedReader().use{it.readText()}
                }

                handler.post(object: Runnable{
                    override fun run() {
                        if(result != ""){
                            val jsonArray = JSONArray(result)

                            for (i in 0 until jsonArray.length()){
                                val item = jsonArray.getJSONObject(i)
                                val itemId = item.getString("id")
                                val itemName = item.getString("name")
                                val itemPrice = item.getString("price")

                                val imageUrl = URL(Method.baseUrl + "/api/menu/$itemId/photo")
                                val handler = Handler(Looper.getMainLooper())
                                Executors.newSingleThreadExecutor().execute(object: Runnable{
                                    override fun run() {
                                        var resultImage = ""
                                        val client = imageUrl.openConnection() as HttpURLConnection
                                        client.requestMethod = "GET"

                                        try{
                                            val inputStream: InputStream = client.inputStream

                                            // Mendapatkan bitmap dari input stream
                                            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

                                            // Mengonversi bitmap ke format base64
                                            val byteArrayOutputStream = ByteArrayOutputStream()
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                                            val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                                            val base64Image: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
                                            resultImage = base64Image
                                        } catch (e: Exception){
                                            resultImage = client.errorStream.bufferedReader().use{it.readText()}
                                        }

                                        handler.post(object: Runnable{
                                            override fun run() {
                                                if(resultImage != ""){
                                                    addItem.add(ItemModel(itemId,
                                                        itemName,
                                                        resultImage,
                                                        convertToRupiah(itemPrice))
                                                    )
                                                    itemAdapter.setItem(addItem)
                                                }
                                            }
                                        })
                                    }
                                })
                            }
                        }
                    }
                })
            }
        })
    }

    fun getItemPaketFamily() {
        addItem.clear()
        val url = URL(Method.baseUrl + "/api/menu/category/PaketFamily")
        val handler = Handler(Looper.getMainLooper())
        Executors.newSingleThreadExecutor().execute(object: Runnable{
            override fun run() {
                var result = ""
                val client = url.openConnection() as HttpURLConnection
                client.requestMethod = "GET"

                try{
                    result = client.inputStream.bufferedReader().use { it.readText() }
                } catch (e: Exception){
                    result = client.errorStream.bufferedReader().use{it.readText()}
                }

                handler.post(object: Runnable{
                    override fun run() {
                        if(result != ""){
                            val jsonArray = JSONArray(result)

                            for (i in 0 until jsonArray.length()){
                                val item = jsonArray.getJSONObject(i)
                                val itemId = item.getString("id")
                                val itemName = item.getString("name")
                                val itemPrice = item.getString("price")

                                val imageUrl = URL(Method.baseUrl + "/api/menu/$itemId/photo")
                                val handler = Handler(Looper.getMainLooper())
                                Executors.newSingleThreadExecutor().execute(object: Runnable{
                                    override fun run() {
                                        var resultImage = ""
                                        val client = imageUrl.openConnection() as HttpURLConnection
                                        client.requestMethod = "GET"

                                        try{
                                            val inputStream: InputStream = client.inputStream

                                            // Mendapatkan bitmap dari input stream
                                            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

                                            // Mengonversi bitmap ke format base64
                                            val byteArrayOutputStream = ByteArrayOutputStream()
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                                            val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                                            val base64Image: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
                                            resultImage = base64Image
                                        } catch (e: Exception){
                                            resultImage = client.errorStream.bufferedReader().use{it.readText()}
                                        }

                                        handler.post(object: Runnable{
                                            override fun run() {
                                                if(resultImage != ""){
                                                    addItem.add(ItemModel(itemId,
                                                        itemName,
                                                        resultImage,
                                                        convertToRupiah(itemPrice))
                                                    )
                                                    itemAdapter.setItem(addItem)
                                                }
                                            }
                                        })
                                    }
                                })
                            }
                        }
                    }
                })
            }
        })
    }

    fun getItemSarapanPagi() {
        addItem.clear()
        val url = URL(Method.baseUrl + "/api/menu/category/SarapanPagi")
        val handler = Handler(Looper.getMainLooper())
        Executors.newSingleThreadExecutor().execute(object: Runnable{
            override fun run() {
                var result = ""
                val client = url.openConnection() as HttpURLConnection
                client.requestMethod = "GET"

                try{
                    result = client.inputStream.bufferedReader().use { it.readText() }
                } catch (e: Exception){
                    result = client.errorStream.bufferedReader().use{it.readText()}
                }

                handler.post(object: Runnable{
                    override fun run() {
                        if(result != ""){
                            val jsonArray = JSONArray(result)

                            for (i in 0 until jsonArray.length()){
                                val item = jsonArray.getJSONObject(i)
                                val itemId = item.getString("id")
                                val itemName = item.getString("name")
                                val itemPrice = item.getString("price")

                                val imageUrl = URL(Method.baseUrl + "/api/menu/$itemId/photo")
                                val handler = Handler(Looper.getMainLooper())
                                Executors.newSingleThreadExecutor().execute(object: Runnable{
                                    override fun run() {
                                        var resultImage = ""
                                        val client = imageUrl.openConnection() as HttpURLConnection
                                        client.requestMethod = "GET"

                                        try{
                                            val inputStream: InputStream = client.inputStream

                                            // Mendapatkan bitmap dari input stream
                                            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

                                            // Mengonversi bitmap ke format base64
                                            val byteArrayOutputStream = ByteArrayOutputStream()
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                                            val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                                            val base64Image: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
                                            resultImage = base64Image
                                        } catch (e: Exception){
                                            resultImage = client.errorStream.bufferedReader().use{it.readText()}
                                        }

                                        handler.post(object: Runnable{
                                            override fun run() {
                                                if(resultImage != ""){
                                                    addItem.add(ItemModel(itemId,
                                                        itemName,
                                                        resultImage,
                                                        convertToRupiah(itemPrice))
                                                    )
                                                    itemAdapter.setItem(addItem)
                                                }
                                            }
                                        })
                                    }
                                })
                            }
                        }
                    }
                })
            }
        })
    }

    fun convertToRupiah(amount: String): String {
        val amountValue = amount.toDoubleOrNull()

        if (amountValue != null) {
            val formatRupiah = DecimalFormat.getCurrencyInstance() as DecimalFormat
            val symbols = formatRupiah.decimalFormatSymbols
            symbols.currencySymbol = "Rp. "
            formatRupiah.decimalFormatSymbols = symbols

            return formatRupiah.format(amountValue)
        }

        return ""
    }

    override fun onClick(position: Int, name: String) {
        if(name == "Ayam"){
            getItemAyam()
        }else if(name == "Cemilan"){
            getItemCemilan()
        }else if(name == "Daging Sapi"){
            getItemDagingSapi()
        }else if(name == "Happy Meal"){
            getItemHappyMeal()
        }else if(name == "Ikan"){
            getItemIkan()
        }else if(name == "Makanan Penuntun"){
            getItemMakananPenuntun()
        }else if(name == "Minuman"){
            getItemMinuman()
        }else if(name == "Paket Family"){
            getItemPaketFamily()
        }else if(name == "Sarapan Pagi"){
            getItemSarapanPagi()
        }
    }
}
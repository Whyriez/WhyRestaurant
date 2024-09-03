package com.example.whyrestaurant.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whyrestaurant.R
import com.example.whyrestaurant.adapter.CartAdapter
import com.example.whyrestaurant.helper.Constant
import com.example.whyrestaurant.helper.PreferenceHelper
import com.example.whyrestaurant.model.CartModel
import com.example.whyrestaurant.server.Method
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class FragmentCart: Fragment(), CartAdapter.ButtonClickListener {
    private lateinit var sharedPref : PreferenceHelper
    lateinit var cartAdapter: CartAdapter
    lateinit var listCart: RecyclerView
    lateinit var totalPrice: TextView
    lateinit var btnOrder: Button

    var addCart: MutableList<CartModel> = ArrayList()

    val decimalFormat = DecimalFormat.getInstance(Locale("id", "ID"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        init(view)
        sharedPref = PreferenceHelper(requireActivity())

        cartAdapter = CartAdapter(requireContext(), this)

        initButton()
        return view
    }

    override fun onResume() {
        super.onResume()
        loadshared()
        setupRecycle()
        calculateTotalPrice()
    }

    private fun initButton() {
        btnOrder.setOnClickListener {
            insertOrder()
        }
    }

    private fun insertOrder(){
        val table = sharedPref.getString(Constant.PREF_TABLE)
        val url = URL(Method.baseUrl + "/api/table/$table/order")
        val handler = Handler(Looper.getMainLooper())
        Executors.newSingleThreadExecutor().execute(object: Runnable{
            override fun run() {
                var result = ""
                val client = url.openConnection() as HttpURLConnection
                client.requestMethod = "POST"
                client.setRequestProperty("Content-Type", "application/json")
                client.doOutput = true

                try{
                    val jsonArray = JSONArray()
                    val json = sharedPref.getCartItems()

                    for (c: CartModel in json) {
                        val itemObject = JSONObject()
                        itemObject.put("menuId", c.getId())
                        itemObject.put("quantity", c.getQuantity())
                        jsonArray.put(itemObject)
                    }

                    val outputStream = client.outputStream
                    val writer = OutputStreamWriter(outputStream)
                    writer.write(jsonArray.toString())
                    writer.flush()

                    val responseCode = client.responseCode
                    Log.e("Response", responseCode.toString())
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        result = client.inputStream.bufferedReader().use { it.readText() }
                    } else {
                        result = client.errorStream.bufferedReader().use { it.readText() }
                    }
                } catch (e: Exception){
                    Log.e("Error", "Exception: ${e.message}")
                } finally {
                    client.disconnect()
                }

                handler.post(object: Runnable{
                    override fun run() {
                        try{
                            if(result != ""){
                                val data = JSONObject(result)

//                                Log.e("Hasil", data.toString())
                                refreshView()
                            }

                        } catch (e: Exception){
                            Method.message("Error", requireActivity())
                        }
                    }
                })
            }
        })
    }

    private fun refreshView() {
        sharedPref.removeDataFromSharedPreference(requireContext(), Constant.CART)
        addCart.clear()
        cartAdapter.setCart(addCart)
        cartAdapter.notifyDataSetChanged()
        var total = 0.0
        totalPrice.text = total.toString()
    }

    private fun init(view: View) {
        listCart = view.findViewById(R.id.listCart)
        totalPrice = view.findViewById(R.id.totalPriceCart)
        btnOrder = view.findViewById(R.id.btnOrder)
    }

    fun setupRecycle(){
        addCart.clear()
        listCart.layoutManager = LinearLayoutManager(context)
        listCart.adapter = cartAdapter
        cartAdapter.notifyDataSetChanged()
    }

    fun loadshared(){
        val json = sharedPref.getCartItems()
        for (c in json){
            addCart.add(
                CartModel(
                    c.getId(),
                    c.getName(),
                    c.getImage(),
                    c.getPrice(),
                    c.getQuantity(),
                )
            )
            cartAdapter.setCart(addCart)
            cartAdapter.notifyDataSetChanged()
        }
    }

    fun calculateTotalPrice() {
        var total = 0.0

        val itemOnCart = sharedPref.getCartItems()
        for (m in itemOnCart) {
            val cleanPrice = m.getPrice().replace("Rp. ", "").replace(",", "").replace(".00", "").trim()
            val priceValue = decimalFormat.parse(cleanPrice)?.toDouble()
            if(priceValue != null){
                val itemTotal = priceValue * m.getQuantity()
                total += itemTotal
                totalPrice.text = convertToRupiah(total.toString())
            }
        }
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

    override fun onClick(position: Int, status: String) {
        if(status == "tambah"){
            calculateTotalPrice()
        }else if(status == "kurang"){
            calculateTotalPrice()
        }else{
            calculateTotalPrice()
        }
    }

}
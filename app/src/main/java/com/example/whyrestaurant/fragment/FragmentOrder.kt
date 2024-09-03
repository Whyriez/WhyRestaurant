package com.example.whyrestaurant.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whyrestaurant.R
import com.example.whyrestaurant.adapter.OrderAdapter
import com.example.whyrestaurant.helper.Constant
import com.example.whyrestaurant.helper.PreferenceHelper
import com.example.whyrestaurant.mod.Menu
import com.example.whyrestaurant.mod.Order
import com.example.whyrestaurant.mod.OrderDetail
import com.example.whyrestaurant.server.Method
import org.json.JSONArray
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.Executors

class FragmentOrder: Fragment() {
    lateinit var listOrders: RecyclerView
    lateinit var orderAdapter: OrderAdapter
    lateinit var sharedPref: PreferenceHelper
    lateinit var totalOrder: TextView

    val decimalFormat = DecimalFormat.getInstance(Locale("id", "ID"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order, container, false)
        init(view)
        sharedPref = PreferenceHelper(requireContext())

        getOrders()
        return view
    }

    private fun init(view: View) {
        listOrders = view.findViewById(R.id.listOrders)
        totalOrder = view.findViewById(R.id.totalOrder)
    }

    private fun getOrders(){
        val code = sharedPref.getString(Constant.PREF_TABLE)
        val url = URL(Method.baseUrl + "/api/table/$code/orders")
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
                            var grandTotal = 0
                            var Total = 0
                            val jsonArray = JSONArray(result)


                            val orderList = mutableListOf<Order>()


                            for (i in 0 until jsonArray.length()){
                                val item = jsonArray.getJSONObject(i)

                                val orderId = item.getString("orderId")
                                val status = item.getString("status")
                                val createdAt = item.getString("createdAt")

                                val orderDetailsArray = item.getJSONArray("orderDetails")
                                val orderDetailsList = mutableListOf<OrderDetail>()

                                if (!orderDetailsArray.isNull(0)) {
                                    for (j in 0 until orderDetailsArray.length()) {
                                        val orderDetailObject = orderDetailsArray.getJSONObject(j)

                                        val quantity = orderDetailObject.getInt("quantity")
                                        val subTotal = orderDetailObject.getInt("subTotal")

                                        val menuObject = orderDetailObject.getJSONObject("menu")

                                        val menuId = menuObject.getString("menuId")
                                        val name = menuObject.getString("name")

                                        val menu = Menu(menuId, name)
                                        val orderDetail = OrderDetail(quantity, subTotal, menu)
                                        orderDetailsList.add(orderDetail)
                                    }

                                    Total = orderDetailsList.sumBy { it.subTotal }

                                    grandTotal += Total


                                    val order = Order(orderId, status, createdAt, orderDetailsList)
                                    orderList.add(order)


                                    listOrders.layoutManager = LinearLayoutManager(requireContext())
                                    orderAdapter = OrderAdapter(requireContext(), orderList)
                                    listOrders.adapter = orderAdapter
                                    orderAdapter.notifyDataSetChanged()

                                }




                            }

//                            Log.e("TOTAL", grandTotal.toString())
                            totalOrder.text = convertToRupiah(grandTotal.toString())

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



}
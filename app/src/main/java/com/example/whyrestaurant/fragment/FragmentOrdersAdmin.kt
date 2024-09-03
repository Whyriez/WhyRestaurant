package com.example.whyrestaurant.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whyrestaurant.R
import com.example.whyrestaurant.adapter.OrderAdminAdapter
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
import java.util.concurrent.Executors

class FragmentOrdersAdmin: Fragment(), OrderAdminAdapter.OrderClickListener {
    private lateinit var onBackPressedCallback: OnBackPressedCallback

    lateinit var sharedPref: PreferenceHelper
    lateinit var numberOrders: TextView
    lateinit var codeOrders: TextView
    lateinit var listOrdersAdmin: RecyclerView
    lateinit var totalOrderAdmin: TextView
    lateinit var btnCloseTable: Button

    lateinit var orderAdminAdapter: OrderAdminAdapter
    var b : Bundle? = null

    val orderList = mutableListOf<Order>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_orders_admin, container, false)
        init(view)
        initAction()
        b = arguments
        sharedPref = PreferenceHelper(requireContext())

        get()
        getOrders()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Remove the current fragment from the back stack
                requireActivity().supportFragmentManager.popBackStack()

                // Replace with a new fragment
                val newFragment = FragmentListTable()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.containerHome, newFragment) // Replace with your container ID
                    .commit()
            }
        }

        // Attach the onBackPressedCallback to the activity's onBackPressedDispatcher
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }

    private fun initAction() {
        btnCloseTable.setOnClickListener {
            closeTable()
        }
    }

    private fun closeTable(){
        val idTable =  b!!.getString("idTable").toString()
        val url = URL(Method.baseUrl + "/api/table/$idTable/close")
        val handler = Handler(Looper.getMainLooper())
        Executors.newSingleThreadExecutor().execute(object: Runnable{
            override fun run() {
                var result = ""
                val client = url.openConnection() as HttpURLConnection
                client.requestMethod = "PUT"
                client.addRequestProperty("Authorization", sharedPref.getString(Constant.TOKEN))
                client.addRequestProperty("Accept", "application/json")
                client.addRequestProperty("Content-Type", "application/json")

                try{
                    result = client.inputStream.bufferedReader().use { it.readText() }
                } catch (e: java.lang.Exception){
                    result = client.errorStream.bufferedReader().use{it.readText()}
                }

                handler.post(object: Runnable{
                    override fun run() {
                        try{
                            if(result != ""){
                                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                                transaction.replace(R.id.containerHome, FragmentListTable())
                                transaction.commit()
                            }

                        } catch (e: java.lang.Exception){
                            Method.message("Code Invalid", requireActivity())
                        }
                    }
                })
            }
        })
    }


    private fun get(){
        if(b != null) {
            val numberOrder =  b!!.getString("number").toString()
            val codeOrder = b!!.getString("code").toString()

            numberOrders.text = "Table " + numberOrder
            codeOrders.text = codeOrder
        }
    }

    fun setupRecycle(){
        listOrdersAdmin.layoutManager = LinearLayoutManager(requireContext())
        orderAdminAdapter = OrderAdminAdapter(requireContext(), orderList, this)
        listOrdersAdmin.adapter = orderAdminAdapter
        orderAdminAdapter.notifyDataSetChanged()
    }

    private fun getOrders(){
        val codeOrder = b!!.getString("code").toString()
        val url = URL(Method.baseUrl + "/api/table/$codeOrder/orders")
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

                                    setupRecycle()
                                    orderAdminAdapter.notifyDataSetChanged()
                                }
                            }

                            totalOrderAdmin.text = convertToRupiah(grandTotal.toString())

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

    private fun init(view: View) {
        totalOrderAdmin = view.findViewById(R.id.totalOrderAdmin)
        listOrdersAdmin = view.findViewById(R.id.listOrdersAdmin)
        numberOrders = view.findViewById(R.id.numberOrdersAdmin)
        codeOrders = view.findViewById(R.id.codeOrdersAdmin)
        btnCloseTable = view.findViewById(R.id.btnCloseTable)
    }

    fun updateStatus(orderId: String, selectedData: String){
        val idTable =  b!!.getString("idTable").toString()
        val url = URL(Method.baseUrl + "/api/table/$idTable/order/$orderId?status=$selectedData")
        val handler = Handler(Looper.getMainLooper())
        Executors.newSingleThreadExecutor().execute(object: Runnable{
            override fun run() {
                var result = ""
                val client = url.openConnection() as HttpURLConnection
                client.requestMethod = "PUT"
                client.addRequestProperty("Authorization", sharedPref.getString(Constant.TOKEN))
                client.addRequestProperty("Accept", "application/json")
                client.addRequestProperty("Content-Type", "application/json")

                try{
                    result = client.inputStream.bufferedReader().use { it.readText() }
                } catch (e: java.lang.Exception){
                    result = client.errorStream.bufferedReader().use{it.readText()}
                }

                handler.post(object: Runnable{
                    override fun run() {
                        try{
                            if(result != ""){
                                getOrders()
                            }

                        } catch (e: java.lang.Exception){
                            Method.message("Code Invalid", requireActivity())
                        }
                    }
                })
            }
        })
    }

    override fun onClick(position: Int, status: String, orderId: String, action: Boolean) {
        if (action){
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.ly_dialog_change_status)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            val btnCencel : Button = dialog.findViewById(R.id.btnCencelChange)
            val btnSubmit : Button = dialog.findViewById(R.id.btnSubmitChange)
            val changeStatus: Spinner = dialog.findViewById(R.id.changeStatus)

            val data = listOf("Ordered", "OnCooking", "Cooked", "Done")

            val adapter = com.example.whyrestaurant.adapter.SpinnerAdapter(requireContext(), data)

            changeStatus.adapter = adapter

            val desiredValue = status
            val position = adapter.getPosition(desiredValue)
            if (position != -1) {
                changeStatus.setSelection(position)
            }

            var selectedData = ""

            changeStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    // Ambil data yang dipilih dari Spinner
                    selectedData = data[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Jika tidak ada item yang dipilih, Anda bisa menangani aksi di sini.
                }
            }

            btnSubmit.setOnClickListener {
                updateStatus(orderId, selectedData)
                orderList.clear()
                dialog.dismiss()
            }
            btnCencel.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

}
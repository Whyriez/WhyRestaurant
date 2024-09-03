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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whyrestaurant.R
import com.example.whyrestaurant.adapter.TableAdapter
import com.example.whyrestaurant.helper.Constant
import com.example.whyrestaurant.helper.PreferenceHelper
import com.example.whyrestaurant.mod.TableModel
import com.example.whyrestaurant.server.Method

import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class FragmentListTable: Fragment() {
    lateinit var sharedPref: PreferenceHelper
    lateinit var tableAdapter: TableAdapter
    lateinit var listTable:RecyclerView
    lateinit var btn_addTable: ImageButton

    val addTable: MutableList<TableModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list_table, container, false)
        init(view)

        sharedPref = PreferenceHelper(requireContext())
        initButton()

        setupRecycle()
        return view
    }

    override fun onResume() {
        super.onResume()

        getTable()
    }

    private fun initButton() {
        btn_addTable.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.ly_dialog_add)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            val btnCencel : Button = dialog.findViewById(R.id.btnCencel)
            val btnOpenTable : Button = dialog.findViewById(R.id.btnOpenTable)
            val inputTable:EditText = dialog.findViewById(R.id.inputTable)

            val value = inputTable.text

            btnOpenTable.setOnClickListener {
                if(value.isNotEmpty()) {
                    if(value.toString().toInt() <= 50){
                        addTable(value.toString().toInt())
                        dialog.dismiss()
                        setupRecycle()
                        getTable()
                    }else{
                        Toast.makeText(requireContext(), "Table number must between 1 and 50", Toast.LENGTH_SHORT).show()
                    }

                }else{
                    Toast.makeText(requireContext(), "Please enter some number!!", Toast.LENGTH_SHORT).show()
                }
            }
            btnCencel.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun init(view: View) {
        listTable = view.findViewById(R.id.listTable)
        btn_addTable = view.findViewById(R.id.btnAdd)
    }

    private fun setupRecycle(){
        addTable.clear()
        listTable.layoutManager = LinearLayoutManager(requireActivity())
        tableAdapter = TableAdapter(requireContext())
        listTable.adapter = tableAdapter
        tableAdapter.notifyDataSetChanged()
    }



    private fun getTable(){
        val url = URL(Method.baseUrl + "/api/table")
        val handler = Handler(Looper.getMainLooper())

        Executors.newSingleThreadExecutor().execute(object: Runnable{
            override fun run() {
                var result = ""
                val client = url.openConnection() as HttpURLConnection
                client.requestMethod = "GET"
                client.addRequestProperty("Authorization", sharedPref.getString(Constant.TOKEN))
                client.addRequestProperty("Accept", "application/json")
                client.addRequestProperty("Content-Type", "application/json")

                try{
                    result = client.inputStream.bufferedReader().use { it.readText() }
                } catch (e: Exception){
                    result = client.errorStream.bufferedReader().use{it.readText()}
                }

                handler.post(object: Runnable{
                    override fun run() {
                        if(result != ""){
                            val jsonArray = JSONArray(result)
                            for (c in 0 until jsonArray.length()){
                                val table = jsonArray.getJSONObject(c)
                                addTable.add(TableModel(
                                    table.getString("id"),
                                    table.getString("number"),
                                    table.getString("code"),
                                    table.getString("total")
                                ))
                                tableAdapter.setTable(addTable)
                            }


                        }
                    }
                })
            }
        })
    }

    private fun addTable(inputTable: Int){
        val url = URL(Method.baseUrl + "/api/table?number=$inputTable")
        val handler = Handler(Looper.getMainLooper())
        Executors.newSingleThreadExecutor().execute(object: Runnable{
            override fun run() {
                var result = ""
                val client = url.openConnection() as HttpURLConnection
                client.requestMethod = "POST"
                client.setRequestProperty("Content-Type", "application/json")
                client.setRequestProperty("Authorization", sharedPref.getString(Constant.TOKEN))

                try{
                    if (client.responseCode == HttpURLConnection.HTTP_OK) {
                        result = client.inputStream.bufferedReader().use { it.readText() }
                    } else {
                        result = client.errorStream.bufferedReader().use { it.readText() }
                    }
                } catch (e: Exception){
                    e.printStackTrace()
                }

                handler.post(object: Runnable{
                    override fun run() {
                        if (client.responseCode == HttpURLConnection.HTTP_BAD_REQUEST){
                            val data = JSONObject(result)
                            val numberErrors = data.getJSONObject("errors").getJSONArray("number")
                            val errorMessage = numberErrors.getString(0)
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        })
    }
}
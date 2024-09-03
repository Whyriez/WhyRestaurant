package com.example.whyrestaurant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.whyrestaurant.helper.Constant
import com.example.whyrestaurant.helper.PreferenceHelper
import com.example.whyrestaurant.server.Method
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPref : PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref = PreferenceHelper(this)

        actionButton()
    }

    private fun actionButton(){
        btnToLogin.setOnClickListener{
            val i = Intent(this, LoginStaffActivity::class.java)
            startActivity(i)
        }
        btnSubmit.setOnClickListener {
//            getTable()
            getTable()
//            AsyncTaskExample(this).execute()
        }
    }

    private fun getTable(){
        val table = inputTable.text!!.trim().toString()
        val url = URL(Method.baseUrl + "/api/table/" + table)
        val handler =Handler(Looper.getMainLooper())
        Executors.newSingleThreadExecutor().execute(object: Runnable{
            override fun run() {
                var result = ""
                val client = url.openConnection() as HttpURLConnection
                client.requestMethod = "POST"

                try{
                    result = client.inputStream.bufferedReader().use { it.readText() }
                } catch (e: Exception){
                    result = client.errorStream.bufferedReader().use{it.readText()}
                }

                handler.post(object: Runnable{
                    override fun run() {
                        try{
                            if(result != ""){
                                val data = JSONObject(result)

                                sharedPref.put(Constant.PREF_TABLE, table)
                                sharedPref.put(Constant.PREF_NUMBER, data.get("number").toString())
                               if(sharedPref.getString(Constant.PREF_TABLE) != null && sharedPref.getString(Constant.PREF_NUMBER) != null){
                                   startActivity(Intent(this@MainActivity, MenuActivity::class.java))
                                   finish()
                               }
                            }

                        } catch (e: Exception){
                            Method.message("Code Invalid", this@MainActivity)
                        }
                    }
                })
            }
        })
    }

//    @SuppressLint("StaticFieldLeak")
//    class AsyncTaskExample(private var activity: MainActivity?) : AsyncTask<String, String, String>() {
//
//        override fun onPreExecute() {
//            super.onPreExecute()
//
//        }
//
//        override fun doInBackground(vararg p0: String?): String {
//            val table = activity!!.inputTable.text!!.trim().toString()
//
//            var result = ""
//            try {
//                val url = URL("http://10.0.2.2:5000/api/table/" + table)
//                val httpURLConnection = url.openConnection() as HttpURLConnection
//
//                httpURLConnection.readTimeout = 8000
//                httpURLConnection.connectTimeout = 8000
//                httpURLConnection.doOutput = true
//                httpURLConnection.requestMethod = "POST"
//                httpURLConnection.connect()
//
//
//                val responseCode: Int = httpURLConnection.responseCode
//                Log.d(activity?.tag, "responseCode - " + responseCode)
//
//                if (responseCode == 200) {
//                    val inStream: InputStream = httpURLConnection.inputStream
//                    val isReader = InputStreamReader(inStream)
//                    val bReader = BufferedReader(isReader)
//                    var tempStr: String?
//
//                    try {
//                        while (true) {
//                            tempStr = bReader.readLine()
//                            if (tempStr == null) {
//                                break
//                            }
//                            result += tempStr
//                        }
//                    } catch (Ex: Exception) {
//                        Log.e(activity?.tag, "Error in convertToString " + Ex.printStackTrace())
//                    }
//                }
//            } catch (ex: Exception) {
//                Log.d("", "Error in doInBackground " + ex.message)
//            }
//            return result
//        }
//
//        override fun onPostExecute(result: String?) {
//            super.onPostExecute(result)
////            activity?.MyprogressBar?.visibility = View.INVISIBLE
//            if (result == "") {
//                Toast.makeText(activity, "Invalid Code", Toast.LENGTH_SHORT).show()
////                activity?.my_text?.text = activity?.getString(R.string.network_error)
//            } else {
////                var parsedResult = ""
////                var jsonObject: JSONObject? = JSONObject(result)
//                activity!!.startActivity(Intent(activity, MenuActivity::class.java))
//                activity!!.finish()
////                jsonObject = jsonObject?.getJSONObject("data")
////                parsedResult += "Code Name : " + (jsonObject?.get("number")) + "\n"
////                Log.e("hasil", parsedResult)
////                parsedResult += "Version Number : " + (jsonObject?.get("version_number")) + "\n"
////                parsedResult += "API Level : " + (jsonObject?.get("api_level"))
////                activity?.my_text?.text = parsedResult
//            }
//        }
//    }

//    private fun getTable(){
//        val table = inputTable.text!!.trim().toString()
//
//        val retro = Client().getRetroClientInstance().create(TabelApi::class.java)
//
//        retro.getTable(getCode = table).enqueue(object : Callback<TableResponse> {
//            override fun onResponse(call: Call<TableResponse>, response: Response<TableResponse>) {
//                if(response.isSuccessful){
//                    val table = response.body()
//
//                    startActivity(Intent(this@MainActivity, MenuActivity::class.java))
//                    finish()
//                }else{
//                    Toast.makeText(this@MainActivity, "Invalid Code", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<TableResponse>, t: Throwable) {
//                t.message?.let { Log.e("Error", it) }
//            }
//
//        })
//    }
}
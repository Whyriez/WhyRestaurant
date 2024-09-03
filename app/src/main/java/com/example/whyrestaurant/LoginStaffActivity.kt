package com.example.whyrestaurant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.whyrestaurant.helper.Constant
import com.example.whyrestaurant.helper.PreferenceHelper
import com.example.whyrestaurant.server.Method
import kotlinx.android.synthetic.main.activity_login_staff.*
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class LoginStaffActivity : AppCompatActivity() {
    lateinit var sharedPref: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_staff)

        sharedPref = PreferenceHelper(this)

        actionButton()
    }

    private fun actionButton(){
        btnToCustomer.setOnClickListener{
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
        btnLogin.setOnClickListener {
            login()
        }

    }

    private fun login(){
        val email = inputEmail.text!!.trim().toString()
        val password = inputPassword.text!!.trim().toString()
        val url = URL(Method.baseUrl + "/api/auth")
        val handler = Handler(Looper.getMainLooper())
        Executors.newSingleThreadExecutor().execute(object: Runnable{
            override fun run() {
                var result = ""
                val client = url.openConnection() as HttpURLConnection
                client.requestMethod = "POST"
                client.setRequestProperty("Content-Type", "application/json")
                client.doOutput = true

                try{
                    val requestBody = JSONObject()
                    requestBody.put("email", email)
                    requestBody.put("password", password)

                    val outputStream = client.outputStream
                    outputStream.write(requestBody.toString().toByteArray(Charsets.UTF_8))
                    outputStream.close()

                    // Baca response dari server
                    if (client.responseCode == HttpURLConnection.HTTP_OK) {
                        result = client.inputStream.bufferedReader().use { it.readText() }
                    } else {
                        result = client.errorStream.bufferedReader().use { it.readText() }
                    }
                } catch (e: Exception){
                    e.printStackTrace()
                } finally {
                    client.disconnect()
                }

                handler.post(object: Runnable{
                    override fun run() {
                        try{
                            if(result != ""){
                                val data = JSONObject(result)
                                val token = "Bearer " + data.getString("token")
                                sharedPref.put(Constant.TOKEN, token)

                                if (sharedPref.getString(Constant.TOKEN) != ""){
                                    startActivity(Intent(this@LoginStaffActivity, HomeActivity::class.java))
                                    finish()
                                }

                            }else{
                                Toast.makeText(this@LoginStaffActivity, "Email or Password is invalid", Toast.LENGTH_SHORT).show()
                            }

                        } catch (e: Exception){
                            Method.message("Email or Password is invalid", this@LoginStaffActivity)
                        }
                    }
                })
            }
        })
    }
}
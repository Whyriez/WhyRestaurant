package com.example.whyrestaurant.server

import android.app.Activity
import android.widget.Toast

object Method {
    val baseUrl = "https://dbd8-103-26-12-152.ngrok-free.app"
//    val baseUrl = "http://10.0.2.2:5000"

    fun message(message: String, activity: Activity){
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
//        val alertDialog = AlertDialog.Builder(activity)
//            .setTitle("Message")
//            .setMessage(message)
//            .setCancelable(false)
//            .setPositiveButton("OK"){s, t->
//                if(st){
//                    activity.finish()
//                }
//            }
//        alertDialog.show()
    }
}
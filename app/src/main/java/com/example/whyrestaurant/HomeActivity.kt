package com.example.whyrestaurant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.whyrestaurant.fragment.FragmentListTable

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.containerHome, FragmentListTable())
        fragmentTransaction.commit()
    }
}
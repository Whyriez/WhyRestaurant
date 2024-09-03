package com.example.whyrestaurant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.whyrestaurant.fragment.FragmentCart
import com.example.whyrestaurant.fragment.FragmentMenu
import com.example.whyrestaurant.fragment.FragmentOrder
import com.example.whyrestaurant.helper.Constant
import com.example.whyrestaurant.helper.PreferenceHelper
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {
    private lateinit var sharedPref : PreferenceHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        sharedPref = PreferenceHelper(this)
        titleMenu.text = "Esemka Restaurant - Table "+ sharedPref.getString(Constant.PREF_NUMBER)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.container, FragmentMenu())
        fragmentTransaction.commit()

        initAction()
    }

    fun initAction(){
        btnMenu.setOnClickListener {
            replaceFragment(FragmentMenu())
        }
        btnCart.setOnClickListener {
            replaceFragment(FragmentCart())
        }
        btnOrder.setOnClickListener {
            replaceFragment(FragmentOrder())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.commit()
    }


}
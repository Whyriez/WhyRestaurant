package com.example.whyrestaurant.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.whyrestaurant.R
import com.example.whyrestaurant.helper.Constant
import com.example.whyrestaurant.helper.PreferenceHelper
import com.example.whyrestaurant.model.CartModel
import com.example.whyrestaurant.server.Method
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class FragmentItemDetail: Fragment() {
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    var b : Bundle? = null
    var countity : Int = 1
    lateinit var title: TextView
    lateinit var price: TextView
    lateinit var description: TextView
    lateinit var image: ImageView
    lateinit var btnMinus: Button
    lateinit var btnPlus: Button
    lateinit var addToCart: Button
    lateinit var count: TextView

    lateinit var sharedPref:PreferenceHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_item_detail, container, false)
        init(view)
        sharedPref = PreferenceHelper(requireActivity())
        b = arguments
        setData()
        getItemDescription()
        initAction()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Remove the current fragment from the back stack
                requireActivity().supportFragmentManager.popBackStack()

                // Replace with a new fragment
                val newFragment = FragmentMenu()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.container, newFragment) // Replace with your container ID
                    .commit()
            }
        }

        // Attach the onBackPressedCallback to the activity's onBackPressedDispatcher
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }

    private fun initAction() {
        btnMinus.setOnClickListener {
            if (countity <= 1){
                countity = 1
            }else{
                countity = countity - 1
            }
            displayCount(countity)
        }
        btnPlus.setOnClickListener {
            countity = countity + 1
            displayCount(countity)
        }
        addToCart.setOnClickListener {
            if(b != null) {
                val idVal =  b!!.getString("id").toString()
                val nameVal =  b!!.getString("name").toString()
                val priceVal = b!!.getString("price").toString()
                val img = b!!.getString("image").toString()
                title.text = nameVal
                price.text = priceVal

                val crt = CartModel(idVal,
                    nameVal,
                    img,
                    priceVal,
                    countity,
                )
                sharedPref.addToCart(crt)
            }


        }
    }

    fun displayImageFromBase64(base64String: String, imageView: ImageView) {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        imageView.setImageBitmap(bitmap)
    }

    fun displayCount(number: Int){
        count.text = number.toString()
    }

    fun getItemDescription() {
        val menuId = b!!.getString("id").toString()
        val url = URL(Method.baseUrl + "/api/menu/$menuId")
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
                            val jsonObject = JSONObject(result)

                            description.text = jsonObject.getString("description")


                        }
                    }
                })
            }
        })
    }

    fun setData(){
        if(b != null) {
            title.text =  b!!.getString("name").toString()
            price.text =  b!!.getString("price").toString()
            displayImageFromBase64(b!!.getString("image").toString(), image)
        }
    }

    fun init(view: View){
        title = view.findViewById(R.id.titleDetail)
        price = view.findViewById(R.id.priceDetail)
        description = view.findViewById(R.id.description)
        image = view.findViewById(R.id.itemImageDetail)
        btnPlus = view.findViewById(R.id.btnPlus)
        btnMinus = view.findViewById(R.id.btnMinus)
        count = view.findViewById(R.id.count)
        addToCart = view.findViewById(R.id.addToCart)
    }
}
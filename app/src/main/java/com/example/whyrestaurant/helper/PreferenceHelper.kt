package com.example.whyrestaurant.helper

import android.content.Context
import android.content.SharedPreferences
import com.example.whyrestaurant.model.CartModel
import org.json.JSONArray
import org.json.JSONObject

class PreferenceHelper(context: Context) {
    private val PREF_NAME = "sharedCart"
    private val sharedPref : SharedPreferences
    val editor : SharedPreferences.Editor

    init{
        sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPref.edit()
    }

    fun addToCart(item: CartModel) {
        val cartItems = getCartItems().toMutableList()
        val existingItem = cartItems.find { it.getId() == item.getId() }
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + 1)
        } else {
            cartItems.add(item)
        }
        saveCartItems(cartItems)
    }

    fun getCartItems(): List<CartModel> {
        val cartJson = sharedPref.getString(Constant.CART, null)
        return if (cartJson != null) {
            val jsonArray = JSONArray(cartJson)
            val cartItems = mutableListOf<CartModel>()

            for (i in 0 until jsonArray.length()) {
                val cartObject = jsonArray.getJSONObject(i)

                val itemId = cartObject.getString("itemId")
                val itemName = cartObject.getString("itemName")
                val img = cartObject.getString("image")
                val itemPrice = cartObject.getString("itemPrice")
                val quantity = cartObject.getInt("quantity")

                val cartItem = CartModel(itemId, itemName, img, itemPrice, quantity)
                cartItems.add(cartItem)
            }

            cartItems.toList()
        } else {
            emptyList()
        }
    }

    private fun saveCartItems(cartItems: List<CartModel>) {
        val jsonArray = JSONArray()

        for (cartItem in cartItems) {
            val cartObject = JSONObject()
            cartObject.put("itemId", cartItem.getId())
            cartObject.put("itemName", cartItem.getName())
            cartObject.put("itemPrice", cartItem.getPrice())
            cartObject.put("image", cartItem.getImage())
            cartObject.put("quantity", cartItem.getQuantity())
            jsonArray.put(cartObject)
        }
        val cartJson = jsonArray.toString()
        editor.putString(Constant.CART, cartJson)
        editor.apply()
    }

    fun updateQuantityById(itemId: String, quantity: Int) {
        val cartItems = getCartItems().toMutableList()
        val existingItem = cartItems.find { it.getId() == itemId }
        existingItem?.setQuantity(quantity)
        saveCartItems(cartItems)
    }

    fun removeItemFromCart(itemId: String) {
        val cartItems = getCartItems().toMutableList()
        cartItems.removeAll { it.getId() == itemId }
        saveCartItems(cartItems)
    }

    fun removeDataFromSharedPreference(context: Context, key: String) {
        editor.remove(key)
        editor.apply()
    }

    fun put(key: String, value: String){
        editor.putString(key, value).apply()
    }

    fun putArray(key: String, value: String){
        editor.putString(key, value).apply()
    }

    fun addValueToSet(key: String, newValue: String) {
        val existingSet = sharedPref.getStringSet(key, emptySet())
        val newSet = existingSet!!.toMutableSet()
        newSet.add(newValue)

        editor.putStringSet(key, newSet)
        editor.apply()
    }

    fun getString(key: String) : String? {
        return sharedPref.getString(key, null)
    }
    fun put(key: String, value: Boolean){
        editor.putBoolean(key, value).apply()
    }
    fun getBoolean(key: String) : Boolean{
        return sharedPref.getBoolean(key, false)
    }
    fun clear(){
        editor.clear().apply()
    }
}
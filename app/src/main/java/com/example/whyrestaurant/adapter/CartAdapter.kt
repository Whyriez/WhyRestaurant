package com.example.whyrestaurant.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whyrestaurant.R
import com.example.whyrestaurant.helper.PreferenceHelper
import com.example.whyrestaurant.model.CartModel

class CartAdapter(val context: Context, val listener: CartAdapter.ButtonClickListener): RecyclerView.Adapter<CartAdapter.CartItemViewHolder>() {
    private val cart : MutableList<CartModel> = mutableListOf()
    private var sharedPref : PreferenceHelper = PreferenceHelper(context)
    var itemid = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        return CartItemViewHolder(LayoutInflater.from(context).inflate(R.layout.list_cart, parent, false))
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        holder.bindmodel(cart[position])
    }

    override fun getItemCount(): Int {
        return cart.size
    }

    fun setCart(data: List<CartModel>){
        cart.clear()
        cart.addAll(data)
        notifyDataSetChanged()
    }

    inner class CartItemViewHolder(item: View): RecyclerView.ViewHolder(item){

        val btnHapus:Button = item.findViewById(R.id.removeItem)
        val btnPLus:Button = item.findViewById(R.id.btnPlusCart)
        val btnMinus:Button = item.findViewById(R.id.btnMinusCart)

        val itemCartName: TextView = item.findViewById(R.id.itemCartName)
        val itemImageCart: ImageView = item.findViewById(R.id.itemImageCart)
        val itemCartPrice: TextView = item.findViewById(R.id.itemCartPrice)
        val quantity: TextView = item.findViewById(R.id.quantity)


        fun bindmodel(m: CartModel){
            itemCartName.text = m.getName()
            itemCartPrice.text = m.getPrice()
            quantity.text = m.getQuantity().toString()
            displayImageFromBase64(m.getImage(), itemImageCart)
            itemid = m.getId()

            var countity : Int = m.getQuantity()

            btnHapus.setOnClickListener {
               sharedPref.removeItemFromCart(m.getId())
                updateCartItems(sharedPref.getCartItems())
                val position = adapterPosition
                listener.onClick(position, "hapus")
            }

            btnMinus.setOnClickListener {
                if (countity <= 1){
                    countity = 1
                }else{
                    countity = countity - 1
                }
                quantity.text = countity.toString()
                sharedPref.updateQuantityById(m.getId(), countity)
                val position = adapterPosition
                listener.onClick(position, "kurang")
            }
            btnPLus.setOnClickListener {
                countity = countity + 1
                quantity.text = countity.toString()
                sharedPref.updateQuantityById(m.getId(), countity)
                val position = adapterPosition
                listener.onClick(position, "tambah")

            }


        }
    }


    fun displayImageFromBase64(base64String: String, imageView: ImageView) {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        imageView.setImageBitmap(bitmap)
    }

    fun updateCartItems(updatedCartItems: List<CartModel>) {
        cart.clear()
        cart.addAll(updatedCartItems)
        notifyDataSetChanged()
    }

    interface ButtonClickListener{
        fun onClick(position: Int, status:String)

    }
}
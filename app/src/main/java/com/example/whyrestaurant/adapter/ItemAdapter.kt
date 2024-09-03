package com.example.whyrestaurant.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.whyrestaurant.R
import com.example.whyrestaurant.fragment.FragmentItemDetail
import com.example.whyrestaurant.model.ItemModel

class ItemAdapter(val context: Context): RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    private val menu : MutableList<ItemModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindmodel(menu[position])
    }

    override fun getItemCount(): Int {
        return menu.size
    }

    fun setItem(data: List<ItemModel>){
        menu.clear()
        menu.addAll(data)
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(item: View): RecyclerView.ViewHolder(item){
        val itemName: TextView = item.findViewById(R.id.itemName)
        val itemPrice: TextView = item.findViewById(R.id.itemPrice)
        val itemImage: ImageView = item.findViewById(R.id.itemImage)

        val viewItem : CardView = item.findViewById(R.id.viewItem)

        val activty = context

        fun bindmodel(m: ItemModel){
            itemName.text = m.getName()
            itemPrice.text = m.getPrice()

            displayImageFromBase64(m.getImage(), itemImage)
//            itemImage.setImageBitmap(m.getImage())

            viewItem.setOnClickListener(object :View.OnClickListener{
                override fun onClick(v: View){
                    val activty = v.context as AppCompatActivity

                    val myFragment = FragmentItemDetail()
                    val fragmentManager = activty.supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    val arg = Bundle()
                    arg.putString("id", m.getId())
                    arg.putString("name", m.getName())
                    arg.putString("price", m.getPrice())
                    arg.putString("image", m.getImage())
                    myFragment.arguments = arg
                    fragmentTransaction.replace(R.id.container, myFragment)
                    fragmentTransaction.commit()

                }
            })

//            viewItem.setOnClickListener {
//                Toast.makeText(context, "clicked" + m.getId(), Toast.LENGTH_SHORT).show()
//
//                val img = m.getImage()
//                val i = Intent(context, FragmentItemDetail::class.java)
//                i.putExtra("id", m.getId())
//                i.putExtra("name", m.getName())
//                i.putExtra("price", m.getPrice())
////                i.putExtra("image", )
//                context.startActivity(i)
//            }

        }
    }

    fun displayImageFromBase64(base64String: String, imageView: ImageView) {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        imageView.setImageBitmap(bitmap)
    }


}
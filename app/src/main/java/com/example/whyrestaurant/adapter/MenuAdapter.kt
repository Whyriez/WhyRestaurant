package com.example.whyrestaurant.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.whyrestaurant.R
import com.example.whyrestaurant.model.MenuModel

class MenuAdapter(val context: Context, val listener:MenuClickListener) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private val menu : MutableList<MenuModel> = mutableListOf()
    private var single_Selected = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        return MenuViewHolder(LayoutInflater.from(context).inflate(R.layout.list_menu, parent, false))
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bindmodel(menu[position])
        if(single_Selected == position){
            val color = Color.parseColor("#F3F1F5")
            holder.menuTitle.setTextColor(color)
            holder.menuActive.isVisible = true
        }else{
            val color = Color.parseColor("#000000")
            holder.menuTitle.setTextColor(color)
            holder.menuActive.isVisible = false
        }

    }

    override fun getItemCount(): Int {
        return menu.size
    }

    fun setMenu(data: List<MenuModel>){
        menu.clear()
        menu.addAll(data)
        notifyDataSetChanged()
    }

    inner class MenuViewHolder(item: View): RecyclerView.ViewHolder(item){
        val menuTitle: TextView = item.findViewById(R.id.menuTitle)
        val lyTitle: LinearLayout = item.findViewById(R.id.lyTitle)
        val menuActive: View = item.findViewById(R.id.lineActive)

        fun bindmodel(m: MenuModel){
            menuTitle.text = m.getTitle()

            lyTitle.setOnClickListener {
                val position = adapterPosition
                setSingleSelection(position)
                listener.onClick(position, m.getTitle())
            }
        }
    }

    fun setSingleSelection(position: Int){
        if(position == RecyclerView.NO_POSITION) return
        notifyItemChanged(single_Selected)
        single_Selected  = position
        notifyItemChanged(single_Selected)
    }

    interface MenuClickListener{
        fun onClick(position: Int, name:String)

    }


}
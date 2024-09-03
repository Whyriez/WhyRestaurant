package com.example.whyrestaurant.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.whyrestaurant.R
import com.example.whyrestaurant.mod.Order
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class OrderAdminAdapter(val context: Context, private val ordersTest: List<Order>, private val listener: OrderAdminAdapter.OrderClickListener): RecyclerView.Adapter<OrderAdminAdapter.OrderItemViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderAdminAdapter.OrderItemViewHolder {
        return OrderItemViewHolder(LayoutInflater.from(context).inflate(R.layout.list_orders, parent, false))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: OrderAdminAdapter.OrderItemViewHolder, position: Int) {
        holder.bindmodel(ordersTest[position])
    }

    override fun getItemCount(): Int {
        return ordersTest.size
    }

    inner class OrderItemViewHolder(item: View): RecyclerView.ViewHolder(item){
        val orderNumber: TextView = item.findViewById(R.id.orderNumber)
        val date: TextView = item.findViewById(R.id.dateOrders)
        val status: TextView = item.findViewById(R.id.statusOrder)
        val nameOrder: TextView = item.findViewById(R.id.itemNameOrders)
        val quantityOrder: TextView = item.findViewById(R.id.quantityItemOrder)
        val subTot: TextView = item.findViewById(R.id.subTotalItemOrder)

        val viewOrders: CardView = item.findViewById(R.id.viewOrders)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bindmodel(m: Order){
            val position = adapterPosition

            orderNumber.text = "Order " + (position + 1).toString() + " -"
            date.text = convertDate(m.createdAt)
            status.text = m.status

            val quantityOrderText  = StringBuilder()
            val nameOrderText  = StringBuilder()
            val subTotText  = StringBuilder()

            for ((index, orderDetail) in m.orderDetails.withIndex()) {
                quantityOrderText.append(orderDetail.quantity.toString())
                nameOrderText.append(orderDetail.menu.name)
                subTotText.append(convertToRupiah(orderDetail.subTotal.toString()))

                if (index < m.orderDetails.size - 1) {
                    quantityOrderText.append("\n")
                    nameOrderText.append("\n")
                    subTotText.append("\n")
                }
            }
            quantityOrder.text = quantityOrderText.toString()
            nameOrder.text = nameOrderText.toString()
            subTot.text = subTotText.toString()

            viewOrders.setOnClickListener {
                listener.onClick(position, m.status, m.orderId, true)
            }
        }
    }

    interface OrderClickListener{
        fun onClick(position: Int, status: String, orderId: String, action: Boolean)
    }

    fun convertToRupiah(amount: String): String {
        val amountValue = amount.toDoubleOrNull()

        if (amountValue != null) {
            val formatRupiah = DecimalFormat.getCurrencyInstance() as DecimalFormat
            val symbols = formatRupiah.decimalFormatSymbols
            symbols.currencySymbol = "Rp. "
            formatRupiah.decimalFormatSymbols = symbols

            return formatRupiah.format(amountValue)
        }

        return ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertDate(isoDateTime: String): String {
        try {
            val isoFormatter = DateTimeFormatter.ISO_DATE_TIME

            val dateTime = LocalDateTime.parse(isoDateTime, isoFormatter)

            // Format yang diinginkan (21 Dec 17 19:54:02)
            val customFormatter = DateTimeFormatter.ofPattern("yy MMM dd HH:mm:ss", Locale.ENGLISH)

            // Konversi ke format yang diinginkan
            return dateTime.format(customFormatter)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Jika terjadi kesalahan, kembalikan string kosong
        return ""
    }

}
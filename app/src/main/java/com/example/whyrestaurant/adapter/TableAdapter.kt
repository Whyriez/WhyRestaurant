package com.example.whyrestaurant.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.whyrestaurant.R
import com.example.whyrestaurant.fragment.FragmentOrdersAdmin
import com.example.whyrestaurant.mod.TableModel
import java.text.DecimalFormat

class TableAdapter(val context: Context): RecyclerView.Adapter<TableAdapter.TableViewHolder>() {
    private val table: MutableList<TableModel> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TableAdapter.TableViewHolder {
        return TableViewHolder(LayoutInflater.from(context).inflate(R.layout.list_table, parent, false))
    }

    override fun onBindViewHolder(holder: TableAdapter.TableViewHolder, position: Int) {
        holder.bindmodel(table[position])
    }

    override fun getItemCount(): Int {
        return table.size
    }

    fun setTable(data: List<TableModel>){
        table.clear()
        table.addAll(data)
        notifyDataSetChanged()
    }

    inner class TableViewHolder(item: View): RecyclerView.ViewHolder(item){

        val tableNumber: TextView = item.findViewById(R.id.tableNumber)
        val tableCode: TextView = item.findViewById(R.id.tableCode)
        val tablePrice: TextView = item.findViewById(R.id.tablePrice)

        val viewTable: CardView = item.findViewById(R.id.viewTable)

        fun bindmodel(m: TableModel){
            tableNumber.text = "Table " + m.number
            tableCode.text = m.code
            tablePrice.text = convertToRupiah(m.total)

            viewTable.setOnClickListener(object :View.OnClickListener{
                override fun onClick(v: View){
                    val activty = v.context as AppCompatActivity

                    val myFragment = FragmentOrdersAdmin()
                    val fragmentManager = activty.supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    val arg = Bundle()
                    arg.putString("idTable", m.id)
                    arg.putString("number", m.number)
                    arg.putString("code", m.code)
                    myFragment.arguments = arg
                    fragmentTransaction.replace(R.id.containerHome, myFragment)
                    fragmentTransaction.commit()
                }
            })

        }
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

}
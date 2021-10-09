package vn.udn.vku.hvloan.onlineshopapp.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import vn.udn.vku.hvloan.onlineshopapp.Models.HistoryModel
import vn.udn.vku.hvloan.onlineshopapp.R
import java.text.DecimalFormat

class HistoryAdapter(val context: Context?, private val paymentList: ArrayList<HistoryModel>): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val currentDate: TextView = view.findViewById(R.id.current_date)
        val currentTime: TextView = view.findViewById(R.id.current_time)
        val productName: TextView = view.findViewById(R.id.product_name)
        val productPrice: TextView = view.findViewById(R.id.product_price)
        val totalPrice: TextView = view.findViewById(R.id.total_price)
        val productNumber: TextView = view.findViewById(R.id.total_quantity)
        val statusCart: TextView = view.findViewById(R.id.status_cart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_history, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val decimalFormat = DecimalFormat("###,###,###")
            holder.currentDate.text = paymentList[position].listOrder[0].currentDate
            holder.currentTime.text = paymentList[position].listOrder[0].currentTime
            holder.productName.text = paymentList[position].listOrder[0].productName
            holder.productNumber.text = paymentList[position].listOrder[0].productNumber.toString()
            holder.productPrice.text = decimalFormat.format(paymentList[position].listOrder[0].productPrice).toString().plus(" VNĐ")
            holder.totalPrice.text = decimalFormat.format(paymentList[position].listOrder[0].totalPrice).toString().plus(" VNĐ")
            holder.statusCart.text = "Waiting for confirmation"



    }

    override fun getItemCount(): Int {
        return paymentList.size
    }
}
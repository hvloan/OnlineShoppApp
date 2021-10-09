package vn.udn.vku.hvloan.onlineshopapp.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import vn.udn.vku.hvloan.onlineshopapp.Models.CartModel
import vn.udn.vku.hvloan.onlineshopapp.R
import java.text.DecimalFormat

class CartAdapter(val context: Context?, val cartList: ArrayList<CartModel>): RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    var totalAmount = 0

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val currentDate: TextView = view.findViewById(R.id.current_date)
        val currentTime: TextView = view.findViewById(R.id.current_time)
        val productName: TextView = view.findViewById(R.id.product_name)
        val productPrice: TextView = view.findViewById(R.id.product_price)
        val totalPrice: TextView = view.findViewById(R.id.total_price)
        val productNumber: TextView = view.findViewById(R.id.total_quantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val decimalFormat = DecimalFormat("###,###,###")
        holder.currentDate.text = cartList[position].currentDate
        holder.currentTime.text = cartList[position].currentTime
        holder.productName.text = cartList[position].productName
        holder.productNumber.text = cartList[position].productNumber.toString()
        holder.productPrice.text = decimalFormat.format(cartList[position].productPrice).toString().plus(" VNĐ")
        holder.totalPrice.text = decimalFormat.format(cartList[position].totalPrice).toString().plus(" VNĐ")

        totalAmount += cartList[position].totalPrice
        val intent = Intent("TotalAmount")
        intent.putExtra("totalAmount", totalAmount)

        LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)

    }

    override fun getItemCount(): Int {
        return cartList.size
    }
}
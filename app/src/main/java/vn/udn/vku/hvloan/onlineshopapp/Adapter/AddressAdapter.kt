package vn.udn.vku.hvloan.onlineshopapp.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vn.udn.vku.hvloan.onlineshopapp.Models.AddressModel
import vn.udn.vku.hvloan.onlineshopapp.R

class AddressAdapter(val context: Context?, val addressList: ArrayList<AddressModel>, val selectedAddress: SelectedAddress): RecyclerView.Adapter<AddressAdapter.ViewHolder>() {

    var selectedRadioButton: RadioButton? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val addressUser: TextView = view.findViewById(R.id.txt_address)
        val checkBoxAddress: RadioButton = view.findViewById(R.id.btn_tick_address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_address, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.addressUser.text = addressList[position].address

        holder.checkBoxAddress.setOnClickListener {
            for (address:AddressModel in addressList) {
                address.isSelected = false
            }
            addressList[position].isSelected = true
            if (selectedRadioButton != null) {
                selectedRadioButton!!.isChecked = false
            }
            selectedRadioButton = it as RadioButton?
            selectedRadioButton?.isChecked = true
            selectedAddress.setAddress(addressList[position].address)
        }
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    interface SelectedAddress {
        fun setAddress(address: String)
    }
}
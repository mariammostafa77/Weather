package com.example.weather.alerts.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.model.CustomAlert

class AlarmsAdapter(private val listener: AlertClickListener) : RecyclerView.Adapter<AlarmsAdapter.ViewHolder>(){
    var alertList:List<CustomAlert> = ArrayList<CustomAlert>()

    fun setUpdatedData(alertList:List<CustomAlert>){
        this.alertList=alertList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val tvAlertAdd: TextView = itemView.findViewById(R.id.tvAlertAdd)
        private val tvAlertDates: TextView = itemView.findViewById(R.id.tvAlertDates)
        private val tvAlertItemTime: TextView = itemView.findViewById(R.id.tvAlertItemTime)
        private val deleteAlertIcon: Button = itemView.findViewById(R.id.deleteAlertIcon)


        fun bind() {
            tvAlertAdd.text=alertList[position].address
            tvAlertDates.text=alertList[position].dates
            tvAlertItemTime.text=alertList[position].time

            deleteAlertIcon.setOnClickListener{
                listener.onDeleteClick(alertList[position])
                notifyDataSetChanged()
            }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alert_item,parent,false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return alertList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        alertList[position].let { holder.bind() }
    }
}
package com.example.weather.currentWeather.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build.VERSION_CODES
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.model.Hourly
import kotlin.collections.ArrayList

class HourlyAdapter : RecyclerView.Adapter<HourlyAdapter.ViewHolder>(){
    var hourlyWeather:List<Hourly> = ArrayList<Hourly>()
    lateinit var context: Context
    lateinit var tempUnit : String

    fun setUpdatedData(hourlyWeather:List<Hourly>,context: Context, tempUnit : String){
        this.hourlyWeather=hourlyWeather
        this.context=context
        this.tempUnit=tempUnit
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val imgDesc: ImageView = itemView.findViewById(R.id.imgDesc)
        private val tvHour: TextView = itemView.findViewById(R.id.tvHour)
        private val tvHourlyDesc: TextView = itemView.findViewById(R.id.tvHourlyDesc)
        private val tvHourlyTemp: TextView = itemView.findViewById(R.id.tvHourlyTemp)
        @SuppressLint("SetTextI18n")
        @RequiresApi(VERSION_CODES.O)
        fun bind(data: Hourly){
            val dateTime=java.time.format.DateTimeFormatter.ISO_INSTANT
                .format(java.time.Instant.ofEpochSecond(data.dt))
            val delim = "T"
            val dateTimeList = dateTime.split(delim)
            val delim2 = "Z"
            val timeList = dateTimeList[1].split(delim2)
            tvHour.text=timeList[0]
            tvHourlyDesc.text= data.weather[0].description
            val imgUrl= data.weather[0].icon
            val myIcon = "https://openweathermap.org/img/w/${imgUrl}.png"
            Glide.with(context).load(myIcon).into(imgDesc)

            when (tempUnit){
                "Fahrenheit (F°)"->tvHourlyTemp.text=((1.8* ((data.temp).minus(273)).plus(32).toInt()).toInt().toString())+"°F"
                "Celsius (C°)"->tvHourlyTemp.text=(((data.temp).minus(273.15).toInt())).toString()+"°C"
                "Kelvin (K°)"->tvHourlyTemp.text=((data.temp).toInt()).toString()+"°K"
                else->tvHourlyTemp.text=(((data.temp).minus(273.15).toInt())).toString()+"°C"
            }




        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hourly_item,parent,false)
        return ViewHolder(view)
    }

    @RequiresApi(VERSION_CODES.O)
    override fun onBindViewHolder(holder: HourlyAdapter.ViewHolder, position: Int) {
        hourlyWeather[position].let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return hourlyWeather.size
    }
}
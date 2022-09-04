package com.example.weather.currentWeather.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.model.Daily

class DaysAdapter : RecyclerView.Adapter<DaysAdapter.ViewHolder>(){
    var dailyWeather:List<Daily> = ArrayList<Daily>()
    lateinit var context: Context
    lateinit var tempUnit : String
    lateinit var windUnit : String

    fun setUpdatedData(daysList:List<Daily>, context: Context, tempUnit : String, windUnit : String){
        this.dailyWeather=daysList
        this.context=context
        this.tempUnit=tempUnit
        this.windUnit=windUnit
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val imgDayDesc: ImageView = itemView.findViewById(R.id.imgDayDesc)
        private val tvDayName: TextView = itemView.findViewById(R.id.tvDayName)
        private val tvDayTemp: TextView = itemView.findViewById(R.id.tvDayTemp)
        private val tvDayHumidity: TextView = itemView.findViewById(R.id.tvDayHumidity)
        private val tvDayCloud: TextView = itemView.findViewById(R.id.tvDayCloud)
        private val tvDayWindSpeed: TextView = itemView.findViewById(R.id.tvDayWindSpeed)
        private val tvDayPressure: TextView = itemView.findViewById(R.id.tvDayPressure)
        private val tvDayDesc: TextView = itemView.findViewById(R.id.tvDayDesc)
        @SuppressLint("SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(data: Daily){

            val dateTime=java.time.format.DateTimeFormatter.ISO_INSTANT
                .format(java.time.Instant.ofEpochSecond(data.dt))
            val delim = "T"
            val dateTimeList = dateTime.split(delim)
            tvDayName.text=dateTimeList[0]
            tvDayHumidity.text=data.humidity.toString()
            tvDayCloud.text=data.clouds.toString()
            tvDayPressure.text=data.pressure.toString()
            val imgUrl=data.weather[0].icon
            val myIcon = "https://openweathermap.org/img/w/${imgUrl}.png"
            Glide.with(context).load(myIcon).into(imgDayDesc)
            tvDayDesc.text=data.weather[0].description



            when (tempUnit){
                "Fahrenheit (F°)"->tvDayTemp.text=((1.8* ((data.temp.day).minus(273)).plus(32).toInt()).toInt().toString())+"°F"
                "Celsius (C°)"->tvDayTemp.text=(((data.temp.day).minus(273.15).toInt())).toString()+"°C"
                "Kelvin (K°)"->tvDayTemp.text=((data.temp.day).toInt()).toString()+"°K"
                else->tvDayTemp.text=(((data.temp.day).minus(273.15).toInt())).toString()+"°C"
            }
            when (windUnit){
                "miles/hour"->tvDayWindSpeed.text=((2.23694*(data.wind_speed).toInt()).toInt()).toString()
                else->tvDayWindSpeed.text=(data.wind_speed.toInt()).toString()
            }













        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dayly_item,parent,false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return dailyWeather.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        dailyWeather.get(position).let { holder.bind(it) }
    }

}
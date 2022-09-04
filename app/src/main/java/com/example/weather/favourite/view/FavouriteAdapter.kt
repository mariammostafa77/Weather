package com.example.weather.favourite.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.model.FavModel


class FavouriteAdapter(private val listeners: FavClickListeners) : RecyclerView.Adapter<FavouriteAdapter.ViewHolder>(){
    var favList:List<FavModel> = ArrayList<FavModel>()

    fun setUpdatedData(favList:List<FavModel>){
        this.favList=favList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val tvFavAddr: TextView = itemView.findViewById(R.id.tvFavAddr)
        private val deleteFavIcon: Button = itemView.findViewById(R.id.deleteFavIcon)
        private val favCardView : CardView = itemView.findViewById(R.id.favCardView)


        fun bind(){
            tvFavAddr.text=favList[position].Favaddress
            deleteFavIcon.setOnClickListener {
                listeners.onDeleteClick(favList[position])
                notifyDataSetChanged()
            }
            favCardView.setOnClickListener {
                listeners.onFavClick(favList[position].latLon)
            }

        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fav_location_item,parent,false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return favList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        favList.get(position).let { holder.bind() }
    }
}
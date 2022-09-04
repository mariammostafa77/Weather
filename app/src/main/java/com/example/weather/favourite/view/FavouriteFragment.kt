package com.example.weather.favourite.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.db.ConcreteLocalSource
import com.example.weather.CheckInternetConnectionFirstTime
import com.example.weather.R
import com.example.weather.favourite.viewModel.FavouriteViewModel
import com.example.weather.favourite.viewModel.FavouriteViewModelFactory
import com.example.weather.map.view.MapsActivity
import com.example.weather.model.FavModel
import com.example.weather.model.Repository
import com.example.weather.network.WeatherClient
import com.google.android.material.snackbar.Snackbar

class FavouriteFragment : Fragment(), FavClickListeners {

    private lateinit var btnAddFavLoc: Button
    private lateinit var favRecycle: RecyclerView
    private lateinit var favouriteViewModel: FavouriteViewModel
    private lateinit var favouriteViewModelFactory: FavouriteViewModelFactory
    private lateinit var favAdapter: FavouriteAdapter

    /*lateinit var favFactory: FavViewModelFactory
    lateinit var viewModel: FavViewModel
    lateinit var favAdapter: FavAdapter*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)
        favRecycle=view.findViewById(R.id.favRecycle)
        btnAddFavLoc=view.findViewById(R.id.btnAddFavLoc)
        favAdapter= FavouriteAdapter(this)
        favRecycle.adapter = favAdapter

        btnAddFavLoc.setOnClickListener {
            if(CheckInternetConnectionFirstTime.checkForInternet(requireContext())){
                val intent = Intent(requireActivity(), MapsActivity::class.java)
                val bundle = Bundle()
                bundle.putInt("key", 2)
                intent.putExtras(bundle)
                startActivity(intent)
                requireActivity().finish()
            }else{
                val snake = Snackbar.make(
                    view,
                    "You are in offline mode, check your network ",
                    Snackbar.LENGTH_LONG
                )
                snake.show()
            }

        }
        favouriteViewModelFactory = FavouriteViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(requireContext()),requireContext()))
        favouriteViewModel = ViewModelProvider(this, favouriteViewModelFactory).get(FavouriteViewModel::class.java)

        favouriteViewModel.localFavInfo().observe(viewLifecycleOwner) { favInfo ->
            if (favInfo != null)
                favAdapter.setUpdatedData(favInfo)
                favAdapter.notifyDataSetChanged()
        }
        return view
    }



    override fun onDeleteClick(favModel: FavModel) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(R.string.delete_confirm)
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                // Delete selected note from database
                favouriteViewModel.deleteFavWeatherInfo(favModel)
            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    override fun onFavClick(latLon:String) {
        FavInfoDialog(latLon).show(requireActivity().supportFragmentManager,"MyDialog")
    }


}
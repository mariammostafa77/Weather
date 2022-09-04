package com.example.weather.map.view

import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.location.LocationServices
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.weather.db.ConcreteLocalSource
import com.example.weather.HomeActivity
import com.example.weather.R
import com.example.weather.alerts.view.AddNewAlertDialog
import com.example.weather.currentWeather.viewModel.CurrentViewModel
import com.example.weather.currentWeather.viewModel.CurrentViewModelFactory
import com.example.weather.favourite.viewModel.FavouriteViewModel
import com.example.weather.favourite.viewModel.FavouriteViewModelFactory
import com.example.weather.model.FavModel
import com.example.weather.model.Repository
import com.example.weather.network.WeatherClient
import com.google.android.gms.location.FusedLocationProviderClient
import java.io.IOException
import java.util.*


class MapsActivity() : FragmentActivity(), OnMapReadyCallback{
/*key = 1 add this location in settings
* key = 2 add in fav
* key = 3 add in alert*/



    private lateinit var mMap: GoogleMap
    var currentMarker:Marker?=null
    var fusedLocationProviderClient: FusedLocationProviderClient?=null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var currentViewModel: CurrentViewModel
    private lateinit var currentViewModelFactory: CurrentViewModelFactory
    private lateinit var favouriteViewModel: FavouriteViewModel
    private lateinit var favouriteViewModelFactory: FavouriteViewModelFactory


    var key : Int = 0
    var latitude:Double=0.0
    var longtude:Double=0.0
    var selectedAddress=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val bundle= intent.extras
        if (bundle != null) {
            key=bundle.getInt("key")
        }

        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)
        sharedPreferences = this
            .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        editor =  sharedPreferences.edit()
        Log.i("TAG", sharedPreferences.getString("currentLatitude", "")!!)
        Log.i("TAG", sharedPreferences.getString("currentLongitude", "")!!)

        latitude = sharedPreferences.getString("currentLatitude", "")!!.toDouble()
        longtude = sharedPreferences.getString("currentLongitude", "")!!.toDouble()
        selectedAddress= sharedPreferences.getString("currentAddress", "")!!
        fetchLocation()
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation(){

            if(latitude != 0.0 && longtude != 0.0){
                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1000->if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                fetchLocation()
            }
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        /*val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/

        val latLon=LatLng(latitude,longtude)
        drawMarker(latLon)
        mMap.setOnMapClickListener {
            currentMarker?.remove()
            drawMarker(it)
        }

        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener{
            override fun onMarkerDrag(p0: Marker) {
            }

            override fun onMarkerDragEnd(p0: Marker) {
                if(currentMarker!=null){
                    currentMarker?.remove()
                }
                val newLatLon=LatLng(p0.position.latitude,p0.position.longitude)
                drawMarker(newLatLon)
                Log.i("TAG","lat= ${p0.position.latitude} \n lon= ${p0.position.longitude}")
            }

            override fun onMarkerDragStart(p0: Marker) {

            }
        })
    }

    fun drawMarker(latLon:LatLng){
        val markerOption=MarkerOptions().position(latLon).title("i need this location")
            .snippet(getAddress(latLon.latitude,latLon.longitude)).draggable(true)
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLon))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLon,15f))
        currentMarker=mMap.addMarker(markerOption)
        currentMarker?.showInfoWindow()
        latitude=latLon.latitude
        longtude=latLon.longitude
        selectedAddress=getAddress(latitude,longtude)
    }

    private fun getAddress(lat:Double, lon:Double): String {
        val geocoder= Geocoder(this, Locale.getDefault())
        val addresses=geocoder.getFromLocation(lat,lon,1)
        return addresses[0].getAddressLine(0).toString()
    }




    fun searchLocation(view: View) {
        val locationSearch = findViewById<EditText>(R.id.editText)
        val location = locationSearch.text.toString()
        var addressList: List<Address>? = null

        if (location.isEmpty()) {
            Toast.makeText(applicationContext,"provide location",Toast.LENGTH_SHORT).show()
        }
        else{

            val geoCoder = Geocoder(this)
            try {
                addressList = geoCoder.getFromLocationName(location, 1)
                if(!addressList.isNullOrEmpty()){
                    val address = addressList[0]
                    val latLng = LatLng(address.latitude, address.longitude)
                    drawMarker(latLng)
                }else{
                    Toast.makeText(applicationContext,"not found",Toast.LENGTH_SHORT).show()
                }

            } catch (e: IOException) {
                e.printStackTrace()

            }

        }
    }
    fun setLocation(view: View) {
        /*if(key == 1){
            editor.putString("currentLatitude",latitude.toString())
            editor.putString("currentLongitude",longtude.toString())
            editor.putString("currentAddress", selectedAddress)
            editor.apply()
            finish()
        }*/
        when (key) {
            1 -> {
                editor.putString("currentLatitude", latitude.toString())
                editor.putString("currentLongitude", longtude.toString())
                editor.putString("currentAddress", selectedAddress)
                editor.apply()
                finish()
            }
            2 -> {
                val lang = sharedPreferences.getString("lang", "")
                currentViewModelFactory = CurrentViewModelFactory(
                    Repository.getInstance(
                        WeatherClient.getInstance(),
                        ConcreteLocalSource(this), this
                    )
                )
                currentViewModel = ViewModelProvider(
                    this,
                    currentViewModelFactory
                ).get(CurrentViewModel::class.java)
                favouriteViewModelFactory = FavouriteViewModelFactory(
                    Repository.getInstance(
                        WeatherClient.getInstance(),
                        ConcreteLocalSource(this), this
                    )
                )
                favouriteViewModel = ViewModelProvider(
                    this,
                    favouriteViewModelFactory
                ).get(FavouriteViewModel::class.java)
                currentViewModel.getCurrentWeather(latitude.toString(),
                    longtude.toString(),
                    lang.toString(),
                    "68dea5913ee5edc56461d63440681c6c")
                currentViewModel.onlineWeather.observe(this) { newWeather ->
                    val myModel = FavModel(
                        (latitude.toString()+longtude.toString()),
                        latitude,
                        longtude,
                        getAddress(latitude, longtude),
                        newWeather.current.clouds,
                        newWeather.current.humidity,
                        newWeather.current.pressure,
                        newWeather.current.temp,
                        newWeather.current.dt,
                        newWeather.current.visibility,
                        newWeather.current.wind_speed,
                        newWeather.current.weather[0].icon,
                        newWeather.current.weather[0].description,
                        newWeather.current.wind_deg
                    )
                    favouriteViewModel.insertFav(myModel)
                    val intent = Intent(this, HomeActivity::class.java)
                    val bundle = Bundle()
                    bundle.putInt("key", 1)
                    intent.putExtras(bundle)
                    startActivity(intent)
                    finish()

                }
            }
            3 -> {
                AddNewAlertDialog.selectedAddress=selectedAddress
                AddNewAlertDialog.lon=longtude
                AddNewAlertDialog.lat=latitude
                finish()
            }

        }



    }
}
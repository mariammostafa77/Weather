package com.example.weather.alerts.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.example.weather.db.ConcreteLocalSource
import com.example.weather.Communicator
import com.example.weather.HomeActivity
import com.example.weather.R
import com.example.weather.alerts.viewModel.AlertViewModel
import com.example.weather.alerts.viewModel.AlertViewModelFactory
import com.example.weather.map.view.MapsActivity
import com.example.weather.model.Repository
import com.example.weather.network.WeatherClient
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AddNewAlertDialog : DialogFragment() {

    private lateinit var tvSelectedAlertLoc:TextView
    private lateinit var tvAlertStartDate:TextView
    private lateinit var tvAlertEndDate:TextView
    private lateinit var tvAlertTime:TextView
    private lateinit var setAlertLocBtn:Button
    private lateinit var alertSubmitBtn:Button
    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alertViewModelFactory: AlertViewModelFactory
    private lateinit var communicator: Communicator
    private lateinit var alertCancelBtn:Button
    private var startYear : Int = 0
    private var startMonth : Int = 0
    private var startDay : Int = 0
    private var endYear : Int = 0
    private var endMonth : Int = 0
    private var endDay : Int = 0
    private var hour : Int = 0
    private var minutes : Int = 0
    private lateinit var startDate : Date
    private lateinit var endDate : Date
    private var diffInDays : Long = 0

    companion object{
        var selectedAddress : String =""
        var lat = 0.0
        var lon = 0.0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = requireActivity().layoutInflater.inflate(R.layout.selected_alert_dialog_info,null)
        initComponent(view)
        communicator = activity as Communicator

        setAlertLocBtn.setOnClickListener {
            chooseLocation()
        }
        tvAlertStartDate.setOnClickListener {
            selectStartDate(tvAlertStartDate)
        }
        tvAlertEndDate.setOnClickListener {
            selectEndDate(tvAlertEndDate)
        }
        tvAlertTime.setOnClickListener {
            selectTime(tvAlertTime)
        }
        tvSelectedAlertLoc.setOnClickListener {
            chooseLocation()
        }

        alertViewModelFactory = AlertViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(requireContext()),requireContext()))
        alertViewModel = ViewModelProvider(this, alertViewModelFactory).get(
            AlertViewModel::class.java)

        alertCancelBtn.setOnClickListener {
            dismiss()
        }


        alertSubmitBtn.setOnClickListener {

            val c = Calendar.getInstance()
            c.set(startYear,startMonth,startDay,hour, minutes)
            val today = Calendar.getInstance()
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm")
            startDate = simpleDateFormat.parse("$startDay/$startMonth/$startYear $hour:$minutes") as Date
            endDate = simpleDateFormat.parse("$endDay/$endMonth/$endYear $hour:$minutes") as Date

            var diff = printDifference(startDate,endDate)
            diffInDays = printDifference(startDate,endDate)/86400
            HomeActivity.oneTimeRequestsArray.add(createOneTimeRequest(printDifference(today.time,startDate)))
            showNotification()
            for(i in 0 until diffInDays){
                if (diff == 86400L ){
                    HomeActivity.oneTimeRequestsArray.add(createOneTimeRequest(diff))
                }
                if (diff > 86400 ){
                    diff -= 86400
                    HomeActivity.oneTimeRequestsArray.add(createOneTimeRequest(diff))

                }
            }
            alertViewModel.insertAlert("$lat$lon$startDate$hour$minutes", selectedAddress,lon.toString(), lat.toString(),
            "${tvAlertStartDate.text} - ${tvAlertEndDate.text}","$hour:$minutes")


            dismiss()
            selectedAddress=""
            communicator.refreshFragment(AlertsFragment())
        }

        return view
    }

    private fun chooseLocation() {
        val intent = Intent(requireActivity(), MapsActivity::class.java)
        val bundle = Bundle()
        bundle.putInt("key", 3)
        intent.putExtras(bundle)
        startActivity(intent)
    }
    override fun onStart() {
        super.onStart()
        if(selectedAddress.isNotEmpty()){
            tvSelectedAlertLoc.text= selectedAddress
        }
    }
    private fun initComponent(view: View){
        tvSelectedAlertLoc = view.findViewById(R.id.tvSelectedAlertLoc)
        tvAlertStartDate = view.findViewById(R.id.tvAlertStartDate)
        tvAlertEndDate = view.findViewById(R.id.tvAlertEndDate)
        tvAlertTime = view.findViewById(R.id.tvAlertTime)
        setAlertLocBtn = view.findViewById(R.id.setAlertLocBtn)
        alertSubmitBtn = view.findViewById(R.id.alertSubmitBtn)
        alertCancelBtn=view.findViewById(R.id.alertCancelBtn)

    }

    fun selectStartDate(textView: TextView){
        val calendar = Calendar.getInstance()
        startYear = calendar[Calendar.YEAR]
        startMonth = calendar[Calendar.MONTH]
        startDay = calendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            activity!!,
            { datePicker, year, month, day ->
                var month = month
                month += 1
                val date = "$day/$month/$year"
                textView.text = date
                startDay=day
                startMonth=month
                startYear=year
            }, startYear, startMonth, startDay
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }
    private fun selectEndDate(textView: TextView){
        val calendar = Calendar.getInstance()
        endYear = calendar[Calendar.YEAR]
        endMonth = calendar[Calendar.MONTH]
        endDay = calendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            activity!!,
            { datePicker, year, month, day ->
                var month = month
                month += 1
                val date = "$day/$month/$year"
                textView.text = date
                endDay=day
                endMonth=month
                endYear=year
            }, endYear, endMonth, endDay
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }
    private fun selectTime(textView: TextView){
        val time = Calendar.getInstance()
        hour = time.get(Calendar.HOUR_OF_DAY)
        minutes = time.get(Calendar.MINUTE)

        val mTimePicker = TimePickerDialog(requireContext(),
            { view, hourOfDay, minute ->
                textView.text= String.format("%d : %d", hourOfDay, minute)
                hour=hourOfDay
                minutes=minute
            }, hour, minutes, false)

        mTimePicker.show()
    }

    private fun createOneTimeRequest(diff: Long):OneTimeWorkRequest{
        val constraint= Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()
        val data = Data.Builder()
        data.putString("lat", lat.toString())
        data.putString("lon", lon.toString())
        data.putString("address", selectedAddress)
        /*val myWorkRequest= OneTimeWorkRequest.Builder(MyWorker::class.java)
    .setConstraints(constraint).addTag("id").build()*/
        val myWorkRequest= OneTimeWorkRequestBuilder<MyWorker>()
            .setConstraints(constraint).addTag("id")
            .setInputData(data.build())
            .setInitialDelay(diff,TimeUnit.SECONDS).build()
        return myWorkRequest

    }

    private fun showNotification(){
        val name = "$lat$lon"
        WorkManager.getInstance().enqueueUniqueWork(name, ExistingWorkPolicy.REPLACE, HomeActivity.oneTimeRequestsArray)
    }


    private fun printDifference(startDate: Date, endDate: Date):Long {
        //milliseconds
        val different = endDate.time - startDate.time
        println("startDate : $startDate")
        println("endDate : $endDate")
        println("different : $different")
        val secondsInMilli: Long = 1000
        /*val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24
        val elapsedDays = different / daysInMilli
        different %= daysInMilli
        val elapsedHours = different / hoursInMilli
        different %= hoursInMilli
        val elapsedMinutes = different / minutesInMilli
        different %= minutesInMilli*/
        val elapsedSeconds = different / secondsInMilli
        return elapsedSeconds
    }
















}
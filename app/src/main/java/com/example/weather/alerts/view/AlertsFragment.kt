package com.example.weather.alerts.view

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
import com.example.weather.alerts.viewModel.AlertViewModel
import com.example.weather.alerts.viewModel.AlertViewModelFactory
import com.example.weather.model.CustomAlert
import com.example.weather.model.Repository
import com.example.weather.network.WeatherClient
import com.google.android.material.snackbar.Snackbar


class AlertsFragment : Fragment(),AlertClickListener {

    private lateinit var addNewAlert:Button
    private lateinit var alertsRecycle:RecyclerView
    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alertViewModelFactory: AlertViewModelFactory
    private lateinit var alertAdapter: AlarmsAdapter

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
        val view = inflater.inflate(R.layout.fragment_alerts, container, false)
        addNewAlert=view.findViewById(R.id.btnAddNewAlert)
        alertsRecycle=view.findViewById(R.id.alertsRecycle)
        alertAdapter= AlarmsAdapter(this)
        alertsRecycle.adapter = alertAdapter

        addNewAlert.setOnClickListener{
            if(CheckInternetConnectionFirstTime.checkForInternet(requireContext())){
                AddNewAlertDialog().show(requireActivity().supportFragmentManager,"MyAlertDialog")

            }else{
                val snake = Snackbar.make(view,
                    "You are in offline mode, check your network ", Snackbar.LENGTH_LONG)
                snake.show()
            }
        }

        alertViewModelFactory = AlertViewModelFactory(
            Repository.getInstance(
                WeatherClient.getInstance(),
                ConcreteLocalSource(requireContext()),requireContext()))
        alertViewModel = ViewModelProvider(this, alertViewModelFactory).get(AlertViewModel::class.java)
        alertViewModel.localAlertInfo().observe(viewLifecycleOwner) { alertInfo ->
            if (alertInfo != null)
                alertAdapter.setUpdatedData(alertInfo)
                alertAdapter.notifyDataSetChanged()
        }





        return view
    }

    override fun onDeleteClick(customAlert: CustomAlert) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(R.string.delete_confirm_alert)
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                alertViewModel.deleteAlert(customAlert)
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }


}
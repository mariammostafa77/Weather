package com.example.weather.alerts.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.CustomAlert
import com.example.weather.model.RepositoryInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AlertViewModel(
    private val _irepo: RepositoryInterface
) : ViewModel() {

    fun insertAlert(id:String, address:String, lon:String, lan:String, dates:String,
                    time:String) {
        CoroutineScope(Dispatchers.IO).launch {
            _irepo.insertAlertWeather(id,address,lon,lan,dates,time)
        }
    }

    fun deleteAlert(customAlert: CustomAlert) {
        viewModelScope.launch(Dispatchers.IO) {
            _irepo.deleteAlertWeather(customAlert)
        }
    }

    fun localAlertInfo(): LiveData<List<CustomAlert>> {
        return _irepo.storedAlertsWeather
    }


}


package com.amorgens.trade.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class OrderViewModel @Inject constructor(private val apiService: APIService) :ViewModel(){

    fun sayHello(){
        viewModelScope.launch {
            val resp = apiService.sayHello();
        }
    }

}
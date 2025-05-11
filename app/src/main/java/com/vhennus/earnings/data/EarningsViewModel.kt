package com.vhennus.earnings.data

import android.app.Application
import androidx.lifecycle.ViewModel
import com.vhennus.general.data.APIService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class EarningsViewModel @Inject constructor(
    private val apiService: APIService,
    private val application: Application,
) : ViewModel() {

}

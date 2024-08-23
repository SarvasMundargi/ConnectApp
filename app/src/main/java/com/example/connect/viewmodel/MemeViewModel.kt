package com.example.connect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connect.MemeModel
import com.example.connect.NetworkResponse
import com.example.connect.RetrofitInstance
import kotlinx.coroutines.launch

class MemeViewModel: ViewModel() {
    private val memeApi = RetrofitInstance.memeApi
    private val _results = MutableLiveData<NetworkResponse<MemeModel>>()
    val results: LiveData<NetworkResponse<MemeModel>> = _results

    fun getUrl() {
        _results.value = NetworkResponse.Loading

        viewModelScope.launch {
            val response = memeApi.getMeme()

            try {
                if (response.isSuccessful) {
                    response.body()?.let {
                        _results.value = NetworkResponse.Success(it)
                    }
                } else {
                    _results.value = NetworkResponse.Error("Failed to load data")
                }
            } catch (e: Exception) {
                _results.value = NetworkResponse.Error("Failed to load data")
            }
        }
    }
}

package com.example.carpull.presentation

import androidx.lifecycle.ViewModel
import com.example.carpull.data.remote.ApiServices
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

interface ICarMakeViewModel {
}

@HiltViewModel
class CarMakeViewModel (): ICarMakeViewModel, ViewModel() {

}
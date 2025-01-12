package com.example.pam_firebase.ui.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.pam_firebase.MahasiswaApplications


object PenyediaViewModel {
    val Factory = viewModelFactory {
        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MahasiswaApplications
            HomeViewModel(app.container.mahasiswaRepository)
        }
        initializer {
            val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MahasiswaApplications
            InsertViewModel(app.container.mahasiswaRepository)
        }
    }
}
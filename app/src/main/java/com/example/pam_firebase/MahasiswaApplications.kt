package com.example.pam_firebase

import android.app.Application
import com.example.pam_firebase.di.AppContainer
import com.example.pam_firebase.di.MahasiswaContainer

class MahasiswaApplications : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = MahasiswaContainer()
    }
}
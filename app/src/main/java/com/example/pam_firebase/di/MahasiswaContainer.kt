package com.example.pam_firebase.di

import com.example.pam_firebase.repository.MahasiswaRepository
import com.example.pam_firebase.repository.NetworkMahasiswaRepository
import com.google.firebase.firestore.FirebaseFirestore

interface AppContainer{
    val mahasiswaRepository : MahasiswaRepository
}

class MahasiswaContainer : AppContainer{
    private  val firebase : FirebaseFirestore = FirebaseFirestore.getInstance() // sejajar dengan URL

    override val mahasiswaRepository: MahasiswaRepository by lazy {
        NetworkMahasiswaRepository(firebase)
    }
}
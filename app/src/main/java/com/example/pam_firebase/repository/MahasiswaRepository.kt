package com.example.pam_firebase.repository


import com.example.pam_firebase.model.Mahasiswa
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

interface MahasiswaRepository {
    val firestore: FirebaseFirestore // Deklarasikan propertinya di sini

    suspend fun getMahasiswa(): Flow<List<Mahasiswa>>

    suspend fun insertMahasiswa(mahasiswa: Mahasiswa)

    suspend fun updateMahasiswa(nim: String, mahasiswa: Mahasiswa)

    suspend fun deleteMahasiswa(mahasiswa: Mahasiswa)

    suspend fun getMahasiswaByNim(nim: String): Flow<Mahasiswa>
}
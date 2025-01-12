package com.example.pam_firebase.repository

import com.example.pam_firebase.model.Mahasiswa
import kotlinx.coroutines.flow.Flow

interface MahasiswaRepository {
    suspend fun getMahasiswa(): Flow<List<Mahasiswa>>

    suspend fun insertMahasiswa(mahasiswa: Mahasiswa)

    suspend fun updateMahasiswa(nim: String, mahasiswa: Mahasiswa)

    suspend fun deleteMahasiswa(nim: String)

    suspend fun getMahasiswaByNim(nim: String): Flow<Mahasiswa>
}
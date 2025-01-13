package com.example.pam_firebase.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pam_firebase.model.Mahasiswa
import com.example.pam_firebase.repository.MahasiswaRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

sealed class HomeUiState {
    data class Success(val mahasiswa: List<Mahasiswa>) : HomeUiState()
    data class Error(val exception: Throwable) : HomeUiState()
    object Loading : HomeUiState()
}

class HomeViewModel(private val mhs: MahasiswaRepository) : ViewModel() {
    var mhsUiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    init {
        getMhs()
    }

    fun getMhs() {
        viewModelScope.launch {
            mhs.getMahasiswa()
                .onStart {
                    mhsUiState = HomeUiState.Loading
                }
                .catch { e ->
                    mhsUiState = HomeUiState.Error(e)
                }
                .collect { mhsList ->
                    mhsUiState = if (mhsList.isEmpty()) {
                        HomeUiState.Error(Exception("Belum ada daftar mahasiswa"))
                    } else {
                        HomeUiState.Success(mhsList)
                    }
                }
        }
    }

    fun insertMahasiswa(mahasiswa: Mahasiswa) {
        viewModelScope.launch {
            try {
                mhs.insertMahasiswa(mahasiswa) // Panggil fungsi insert dari repository
                // Tampilkan notifikasi "DATA BERHASIL DITAMBAHKAN"
            } catch (e: Exception) {
                // Tampilkan notifikasi error
                mhsUiState = HomeUiState.Error(e)
                Log.e("HomeViewModel", "Error inserting data: ", e)
            }
        }
    }
}
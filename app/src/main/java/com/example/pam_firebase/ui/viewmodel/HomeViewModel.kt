package com.example.pam_firebase.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pam_firebase.model.Mahasiswa
import com.example.pam_firebase.repository.MahasiswaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class HomeUiState {
    data class Success(val mahasiswa: List<Mahasiswa>) : HomeUiState()
    data class Error(val exception: Throwable) : HomeUiState()
    object Loading : HomeUiState()
}

class HomeViewModel(
    private val repository: MahasiswaRepository
) : ViewModel() {

    // Variabel private untuk menyimpan state secara internal
    private val _mhsUiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)

    // Variabel public untuk mengekspos state ke UI
    val mhsUiState: StateFlow<HomeUiState> = _mhsUiState

    init {
        getMhs()
    }

    // Fungsi untuk mengambil data mahasiswa
    fun getMhs() {
        viewModelScope.launch {
            repository.getMahasiswa()
                .catch { e ->
                    _mhsUiState.value = HomeUiState.Error(e)
                }
                .collectLatest { mahasiswa ->
                    _mhsUiState.value = HomeUiState.Success(mahasiswa)
                }
        }
    }

    // Fungsi untuk menghapus data mahasiswa
    fun deleteMahasiswa(nim: String) {
        viewModelScope.launch {
            try {
                // Buat objek Mahasiswa dengan nilai default untuk parameter lainnya
                val mahasiswa = Mahasiswa(
                    nim = nim,
                    nama = "",
                    alamat = "",
                    jenisKelamin = "",
                    kelas = "",
                    angkatan = "",
                    judulSkripsi = "",
                    dosen1 = "",
                    dosen2 = "",
                )
                repository.deleteMahasiswa(mahasiswa) // Panggil metode delete dari repository
                getMhs() // Refresh data setelah menghapus
            } catch (e: Exception) {
                // Handle error
                _mhsUiState.value = HomeUiState.Error(e)
                Log.e("HomeViewModel", "Error deleting data: ", e)
            }
        }
    }

    // Fungsi untuk menambahkan data mahasiswa
    fun insertMahasiswa(mahasiswa: Mahasiswa) {
        viewModelScope.launch {
            try {
                repository.insertMahasiswa(mahasiswa) // Panggil fungsi insert dari repository
                // Tampilkan notifikasi "DATA BERHASIL DITAMBAHKAN"
            } catch (e: Exception) {
                // Tampilkan notifikasi error
                _mhsUiState.value = HomeUiState.Error(e)
                Log.e("HomeViewModel", "Error inserting data: ", e)
            }
        }
    }
}
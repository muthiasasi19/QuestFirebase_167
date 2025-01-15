package com.example.pam_firebase.ui.theme


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pam_firebase.model.Mahasiswa
import com.example.pam_firebase.repository.MahasiswaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UpdateViewModel(
    private val repository: MahasiswaRepository
) : ViewModel() {

    private val _mahasiswaState = MutableStateFlow<Mahasiswa?>(null)
    val mahasiswaState: StateFlow<Mahasiswa?> = _mahasiswaState

    private val _updateUiState = MutableStateFlow<UpdateUiState>(UpdateUiState.Idle)
    val updateUiState: StateFlow<UpdateUiState> = _updateUiState

    fun getMhs(nim: String) {
        viewModelScope.launch {
            println("Fetching data for nim: $nim")
            repository.getMahasiswaByNim(nim)
                .catch { e ->
                    println("Error fetching data: ${e.message}")
                    _mahasiswaState.value = null
                }
                .collectLatest { mahasiswa ->
                    println("Data fetched successfully: $mahasiswa")
                    _mahasiswaState.value = mahasiswa
                }
        }
    }

    fun updateMahasiswa(nim: String, mahasiswa: Mahasiswa) {
        viewModelScope.launch {
            _updateUiState.value = UpdateUiState.Loading
            try {
                repository.updateMahasiswa(nim, mahasiswa) // Pastikan parameter yang benar
                _updateUiState.value = UpdateUiState.Success
            } catch (e: Exception) {
                _updateUiState.value = UpdateUiState.Error(e.message ?: "Gagal mengupdate data")
            }
        }
    }
}

sealed class UpdateUiState {
    object Idle : UpdateUiState()
    object Loading : UpdateUiState()
    object Success : UpdateUiState()
    data class Error(val message: String) : UpdateUiState()
}
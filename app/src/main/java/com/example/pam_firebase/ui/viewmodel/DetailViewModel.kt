package com.example.pam_firebase.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pam_firebase.model.Mahasiswa
import com.example.pam_firebase.repository.MahasiswaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: MahasiswaRepository
) : ViewModel() {

    private val nim: String = checkNotNull(savedStateHandle["nim"])

    private val _detailUiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val detailUiState: StateFlow<DetailUiState> = _detailUiState

    init {
        getMhsbyNim(nim)
    }

    fun getMhsbyNim(nim: String) {
        viewModelScope.launch {
            _detailUiState.value = DetailUiState.Loading

            repository.getMahasiswaByNim(this@DetailViewModel.nim)
                .onStart {
                }
                .catch {
                    _detailUiState.value = DetailUiState.Error
                }
                .collect { mahasiswa ->
                    _detailUiState.value = DetailUiState.Success(mahasiswa)
                }
        }
    }
}
sealed class DetailUiState {
    data class Success(val mahasiswa: Mahasiswa) : DetailUiState()
    object Error : DetailUiState()
    object Loading : DetailUiState()
}
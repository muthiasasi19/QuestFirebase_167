package com.example.pam_firebase.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pam_firebase.repository.MahasiswaRepository
import kotlinx.coroutines.launch

class InsertViewModel (
    private val mhs: MahasiswaRepository
) : ViewModel() {
    var uiEvent: InsertUiState by mutableStateOf(InsertUiState())
        private set
    var uiState: FormState by mutableStateOf(FormState.Idle)
        private set

    // Memperbarui state by input pengguna
    fun updateState(mahasiswaEvent: MahasiswaEvent) {
        uiEvent =uiEvent.copy(
            insertUiEvent = mahasiswaEvent,
        )
    }// Validasi data input pengguna
    fun validateFields() : Boolean {
        val event = uiEvent.insertUiEvent
        val errorState = FormErrorState(
            nim = if (event.nim?.isNotEmpty() == true) null else "NIM tidak boleh kosong",
            nama = if (event.nama?.isNotEmpty() == true) null else "Nama tidak boleh kosong",
            jenis_kelamin = if (event.jenis_kelamin?.isNotEmpty() == true) null else "Jenis Kelamin tidak boleh kosong",
            alamat = if (event.alamat?.isNotEmpty() == true) null else "Alamat tidak boleh kosong",
            kelas = if (event.kelas?.isNotEmpty() == true) null else "Kelas tidak boleh kosong",
            angkatan = if (event.angkatan?.isNotEmpty() == true) null else "angkatan tidak boleh kosong"
        )
        uiEvent = uiEvent.copy(isEntryValid = errorState)
        return errorState.isValid()
    }// Fungsi insert view model
    fun insertMhs() {
        if (validateFields()) { // logika validasi insert
            viewModelScope.launch {
                uiState = FormState.Loading
                try {
                    mhs.insertMahasiswa(uiEvent.insertUiEvent.toMhsModel())
                    uiState = FormState.Success("Data berhasil disimpan")
                } catch (e: Exception) {
                    uiState = FormState.Error("Data gagal disimpan")
                }
            }
        } else {
            uiState = FormState.Error("Data tidak valid")
        }
    }
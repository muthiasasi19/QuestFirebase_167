package com.example.pam_firebase.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pam_firebase.model.Mahasiswa
import com.example.pam_firebase.ui.view.FormMahasiswa
import com.example.pam_firebase.ui.viewmodel.FormErrorState
import com.example.pam_firebase.ui.viewmodel.MahasiswaEvent
import com.example.pam_firebase.ui.viewmodel.PenyediaViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun UpdateView(
    mahasiswa: Mahasiswa,
    viewModel: UpdateViewModel = viewModel(factory = PenyediaViewModel.Factory),
    onUpdateSuccess: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    val updateUiState by viewModel.updateUiState.collectAsState()
    val mahasiswaEvent = remember {
        mutableStateOf(
            MahasiswaEvent(
                nama = mahasiswa.nama,
                nim = mahasiswa.nim,
                jenisKelamin = mahasiswa.jenisKelamin,
                alamat = mahasiswa.alamat,
                kelas = mahasiswa.kelas,
                angkatan = mahasiswa.angkatan
            )
        )
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(updateUiState) {
        when (updateUiState) {
            is UpdateUiState.Success -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Data berhasil diperbarui")
                }
                delay(700)
                onUpdateSuccess()
            }
            is UpdateUiState.Error -> {
                val errorMessage = (updateUiState as UpdateUiState.Error).message
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(errorMessage)
                }
            }
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            FormMahasiswa(
                mahasiswaEvent = mahasiswaEvent.value,
                onValueChange = { updatedEvent ->
                    mahasiswaEvent.value = updatedEvent
                },
                errorState = FormErrorState(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.updateMahasiswa(
                        nim = mahasiswa.nim, // Berikan NIM sebagai parameter pertama
                        mahasiswa = Mahasiswa(
                            nim = mahasiswaEvent.value.nim ?: "",
                            nama = mahasiswaEvent.value.nama ?: "",
                            jenisKelamin = mahasiswaEvent.value.jenisKelamin ?: "",
                            alamat = mahasiswaEvent.value.alamat ?: "",
                            kelas = mahasiswaEvent.value.kelas ?: "",
                            angkatan = mahasiswaEvent.value.angkatan ?: "",
                            judulSkripsi = mahasiswaEvent.value.judulSkripsi ?: "",
                            dosen1 = mahasiswaEvent.value.dosen1 ?: "",
                            dosen2 = mahasiswaEvent.value.dosen2 ?: "",




                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (updateUiState is UpdateUiState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Updating...")
                } else {
                    Text("Update")
                }
            }
        }
    }
}
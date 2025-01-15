package com.example.pam_firebase.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pam_firebase.model.Mahasiswa
import com.example.pam_firebase.ui.viewmodel.DetailUiState
import com.example.pam_firebase.ui.viewmodel.DetailViewModel
import com.example.pam_firebase.ui.viewmodel.PenyediaViewModel

@Composable
fun DetailView(
    nim: String,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = viewModel(factory = PenyediaViewModel.Factory),
    onBack: () -> Unit = { },
    onUpdate: (String) -> Unit = { }
) {
    LaunchedEffect(nim) {
        viewModel.getMhsbyNim(nim) // Gunakan parameter nim untuk mengambil data
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 18.dp),

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    try {
                        val detailState = viewModel.detailUiState.value
                        if (detailState is DetailUiState.Success) {
                            println("Navigating to UpdateView with nim: ${detailState.mahasiswa.nim}")
                            onUpdate(detailState.mahasiswa.nim) // Panggil onUpdate dengan nim
                        } else {
                            println("Detail state is not Success: $detailState")
                        }
                    } catch (e: Exception) {
                        println("Error in FloatingActionButton onClick: ${e.message}")
                        e.printStackTrace() // Cetak stack trace untuk debugging
                    }
                },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(16.dp)
            ) {
               /* Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Mahasiswa",
                )*/
                Button(onClick = onBack) {
                    Text("Back")
                }

            }
        }
    ) { innerPadding ->
        val detailUiState by viewModel.detailUiState.collectAsState()

        BodyDetail(
            modifier = Modifier.padding(innerPadding),
            detailUiState = detailUiState,
        )
    }
}

@Composable
fun BodyDetail(
    modifier: Modifier = Modifier,
    detailUiState: DetailUiState,
) {
    when (detailUiState) {
        is DetailUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is DetailUiState.Success -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                ItemDetail(
                    mahasiswa = detailUiState.mahasiswa,
                    modifier = Modifier
                )
            }
        }

        is DetailUiState.Error -> {
            Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Data tidak ditemukan",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun ItemDetail(
    modifier: Modifier = Modifier,
    mahasiswa: Mahasiswa
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            ComponentDetail(judul = "NIM", isinya = mahasiswa.nim)
            Spacer(modifier = Modifier.padding(4.dp))
            ComponentDetail(judul = "Nama", isinya = mahasiswa.nama)
            Spacer(modifier = Modifier.padding(4.dp))
            ComponentDetail(judul = "Alamat", isinya = mahasiswa.alamat)
            Spacer(modifier = Modifier.padding(4.dp))
            ComponentDetail(judul = "Jenis Kelamin", isinya = mahasiswa.jenisKelamin)
            Spacer(modifier = Modifier.padding(4.dp))
            ComponentDetail(judul = "Kelas", isinya = mahasiswa.kelas)
            Spacer(modifier = Modifier.padding(4.dp))
            ComponentDetail(judul = "Angkatan", isinya = mahasiswa.angkatan)
            ComponentDetail(judul = "Judul Skripsi", isinya = mahasiswa.judulSkripsi)
            Spacer(modifier = Modifier.padding(4.dp))
            ComponentDetail(judul = "Dosen 1", isinya = mahasiswa.dosen1)
            Spacer(modifier = Modifier.padding(4.dp))
            ComponentDetail(judul = "Dosen 2", isinya = mahasiswa.dosen2)
            Spacer(modifier = Modifier.padding(4.dp))
        }
    }
}

@Composable
fun ComponentDetail(
    modifier: Modifier = Modifier,
    judul: String,
    isinya: String
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "$judul : ",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Text(
            text = isinya, fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
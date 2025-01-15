package com.example.pam_firebase.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pam_firebase.model.Mahasiswa
import com.example.pam_firebase.ui.viewmodel.HomeUiState
import com.example.pam_firebase.ui.viewmodel.HomeViewModel
import com.example.pam_firebase.ui.viewmodel.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToItemEntry: () -> Unit,
    modifier: Modifier = Modifier,
    onDetailClick: (String) -> Unit = {},
    viewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var mahasiswaToDelete by remember { mutableStateOf<Mahasiswa?>(null) }

    // Mengumpulkan nilai dari StateFlow menggunakan collectAsState
    val homeUiState by viewModel.mhsUiState.collectAsState()

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                mahasiswaToDelete = null
            },
            title = { Text("Hapus Data") },
            text = { Text("Apakah Anda yakin ingin menghapus data ini?") },
            confirmButton = {
                Button(
                    onClick = {
                        mahasiswaToDelete?.let { mahasiswa ->
                            viewModel.deleteMahasiswa(mahasiswa.nim)
                        }
                        showDeleteDialog = false
                        mahasiswaToDelete = null
                    }
                ) {
                    Text("Ya")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        mahasiswaToDelete = null
                    }
                ) {
                    Text("Tidak")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Data Mahasiswa") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToItemEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(18.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Kontak")
            }
        }
    ) { innerPadding ->
        HomeStatus(
            homeUiState = homeUiState, // Gunakan homeUiState yang sudah dikumpulkan
            retryAction = { viewModel.getMhs() },
            modifier = Modifier.padding(innerPadding),
            onDetailClick = onDetailClick,
            onDeleteClick = { nim ->
                // Cari data yang akan dihapus dari state yang sudah dikumpulkan
                mahasiswaToDelete = (homeUiState as? HomeUiState.Success)?.mahasiswa?.find { it.nim == nim }
                showDeleteDialog = true
            }
        )
    }
}

@Composable
fun HomeStatus(
    homeUiState: HomeUiState,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    onDeleteClick: (String) -> Unit = {},
    onDetailClick: (String) -> Unit
) {
    when (homeUiState) {
        is HomeUiState.Loading -> OnLoading(modifier = modifier.fillMaxSize())

        is HomeUiState.Success ->
            MhsLayout(
                mahasiswa = homeUiState.mahasiswa,
                modifier = modifier.fillMaxWidth(),
                onDetailClick = { mahasiswa ->
                    onDetailClick(mahasiswa.nim)
                },
                onDeleteClick = { nim ->
                    onDeleteClick(nim) // Teruskan onDeleteClick ke MhsLayout
                }
            )

        is HomeUiState.Error -> OnError(
            retryAction,
            modifier = modifier.fillMaxSize(),
            message = homeUiState.exception.message ?: "ERROR"
        )
    }
}

@Composable
fun OnLoading(modifier: Modifier = Modifier){
    Column (
        modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        CircularProgressIndicator()
    }
}

@Composable
fun OnError(retryAction:()->Unit, modifier: Modifier = Modifier, message: String){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text("retry")
        }
    }
}

@Composable
fun MhsLayout(
    mahasiswa: List<Mahasiswa>,
    modifier: Modifier = Modifier,
    onDetailClick: (Mahasiswa) -> Unit,
    onDeleteClick: (String) -> Unit = {} // Tambahkan parameter onDeleteClick
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(mahasiswa) { mahasiswa ->
            MhsCard(
                mahasiswa = mahasiswa,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDetailClick(mahasiswa) },
                onDeleteClick = { nim ->
                    onDeleteClick(nim) // Panggil onDeleteClick dengan NIM
                }
            )
        }
    }
}

@Composable
fun MhsCard(
    mahasiswa: Mahasiswa,
    modifier: Modifier = Modifier,
    onDeleteClick: (String) -> Unit = {}
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = mahasiswa.nama,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = mahasiswa.nim,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                IconButton(
                    onClick = { onDeleteClick(mahasiswa.nim) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            }
            Text(
                text = mahasiswa.alamat,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = mahasiswa.judulSkripsi,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

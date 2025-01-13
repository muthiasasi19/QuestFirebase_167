package com.example.pam_firebase.repository

import android.util.Log
import com.example.pam_firebase.model.Mahasiswa
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NetworkMahasiswaRepository (
    override val firestore: FirebaseFirestore
) : MahasiswaRepository {
    override suspend fun getMahasiswa(): Flow<List<Mahasiswa>> = callbackFlow {
        val mhsCollection = firestore.collection("Mahasiswa")
            .orderBy("nim", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    close(error) // Tutup aliran dengan error
                    return@addSnapshotListener
                }

                if (value != null) {
                    val mhsList = value.documents.mapNotNull {
                        it.toObject(Mahasiswa::class.java)
                    }
                    trySend(mhsList)
                }
            }

        awaitClose {
            mhsCollection.remove()
        }
    }


    override suspend fun insertMahasiswa(mahasiswa: Mahasiswa) {
        try {
            firestore.collection("Mahasiswa")
                .document(mahasiswa.nim) // Gunakan NIM sebagai ID dokumen
                .set(mahasiswa)
                .await()
        } catch (e: Exception) {
            Log.e("Firestore", "Error adding document: ", e)
            throw e // Lempar exception ke pemanggil
        }
    }

    override suspend fun updateMahasiswa(nim: String, mahasiswa: Mahasiswa) {

    }

    override suspend fun deleteMahasiswa(nim: String) {

    }

    override suspend fun getMahasiswaByNim(nim: String): Flow<Mahasiswa> {
        TODO()
    }
}



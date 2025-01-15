package com.example.pam_firebase.repository

import android.util.Log
import com.example.pam_firebase.model.Mahasiswa
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NetworkMahasiswaRepository(
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
                .document(mahasiswa.nim) // Pake NIM untuk ID nya
                .set(mahasiswa)
                .await()
        } catch (e: Exception) {
            Log.e("Firestore", "Error adding document: ", e)
            throw e
        }
    }

    override suspend fun updateMahasiswa(nim: String, mahasiswa: Mahasiswa) {
        try {
            val querySnapshot = firestore.collection("Mahasiswa")
                .whereEqualTo("nim", mahasiswa.nim)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val documentId = querySnapshot.documents[0].id
                firestore.collection("Mahasiswa")
                    .document(documentId)
                    .set(mahasiswa)
                    .await()
            } else {
                throw Exception("Mahasiswa dengan NIM ${mahasiswa.nim} tidak ditemukan.")
            }
        } catch (e: Exception) {
            throw Exception("Gagal mengupdate data mahasiswa: ${e.message}")
        }
    }

    override suspend fun deleteMahasiswa(mahasiswa: Mahasiswa) {
        try {
            val querySnapshot = firestore.collection("Mahasiswa")
                .whereEqualTo("nim", mahasiswa.nim)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val documentId = querySnapshot.documents[0].id
                firestore.collection("Mahasiswa")
                    .document(documentId)
                    .delete()
                    .await()
            } else {
                throw Exception("Mahasiswa dengan NIM ${mahasiswa.nim} tidak ditemukan.")
            }
        } catch (e: Exception) {
            throw Exception("Error deleting Mahasiswa: ${e.message}")
        }
    }

    override suspend fun getMahasiswaByNim(nim: String): Flow<Mahasiswa> = callbackFlow {
        val mhsCollection = firestore.collection("Mahasiswa")
            .whereEqualTo("nim", nim)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    close(error)
                } else {
                    if (value != null && value.documents.isNotEmpty()) {
                        val mahasiswa = value.documents.firstOrNull()?.toObject(Mahasiswa::class.java)
                        mahasiswa?.let {
                            trySend(it)
                        } ?: close(Exception("Mahasiswa tidak ditemukan"))
                    } else {
                        close(Exception("Mahasiswa tidak ditemukan"))
                    }
                }
            }

        awaitClose { mhsCollection.remove() }
    }
}
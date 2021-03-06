package com.itaeducativa.android.redita.data.repositorios

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.itaeducativa.android.redita.data.firebase.FirebaseSource
import com.itaeducativa.android.redita.data.modelos.Archivo

private const val ARCHIVOS = "archivos"

class RepositorioArchivo(
    private val firebase: FirebaseSource
) {
    private val firestoreDB by lazy {
        firebase.firestoreDB
    }

    fun guardarArchivoFirestore(archivo: Archivo): Task<Void> =
        firestoreDB.collection(ARCHIVOS).document(archivo.id).set(archivo)

    fun guardarUrlArchivoFirestore(archivoId: String, url: String): Task<Void> =
        firestoreDB.collection(ARCHIVOS).document(archivoId).update("url", url)

    fun getArchivosByActividadId(actividadId: String, limit: Long): Query =
        firestoreDB.collection(ARCHIVOS).whereEqualTo("actividadId", actividadId).limit(limit)

    fun eliminarArchivo(archivo: Archivo) =
        firestoreDB.collection(ARCHIVOS).document(archivo.id).delete()


}
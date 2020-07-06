package com.itaeducativa.android.redita.ui.comentario.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.itaeducativa.android.redita.data.modelos.Comentario
import com.itaeducativa.android.redita.data.modelos.Historial
import com.itaeducativa.android.redita.data.repositorios.*
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.comentario.adapters.ListaComentariosAdapter

class ListaComentariosViewModel(
    private val repositorioComentario: RepositorioComentario,
    private val repositorioUsuario: RepositorioUsuario,
    private val repositorioPublicacion: RepositorioPublicacion,
    private val repositorioHistorial: RepositorioHistorial
) :
    ViewModel() {

    private val listaComentarios = MutableLiveData<List<Comentario>>()

    val listaComentariosAdapter: ListaComentariosAdapter =
        ListaComentariosAdapter()

    var requestListener: RequestListener? = null

    fun agregarComentariosEnFirestorePorPublicacion(comentario: Comentario) {
        requestListener?.onStartRequest()
        repositorioComentario.agregarComentarioEnFirestorePorPublicacion(comentario)
            .addOnFailureListener {
                requestListener?.onFailureRequest(it.message!!)
            }.addOnSuccessListener {
                if (comentario.tipoPublicacion == "actividades") {
                    val historial = Historial(
                        usuarioUid = comentario.usuarioUid,
                        actividadId = comentario.publicacionId,
                        accion = "Comentó",
                        timestampAccion = comentario.fecha
                    )

                    repositorioHistorial.guardarHistorialFirestore(historial)
                }
                repositorioPublicacion.aumentarInteraccion(
                    comentario.tipoPublicacion,
                    comentario.publicacionId,
                    "comentarios"
                ).addOnSuccessListener {
                    requestListener?.onSuccessRequest()
                }.addOnFailureListener {
                    requestListener?.onFailureRequest(it.message!!)
                }
                repositorioUsuario.sumarInteraccion("comentarios", comentario.usuarioUid)
                    .addOnSuccessListener {
                        requestListener?.onSuccessRequest()
                    }.addOnFailureListener {
                        requestListener?.onFailureRequest(it.message!!)
                    }
            }
    }

    fun getComentariosEnFirestorePorPublicacion(publicacionId: String) {
        requestListener?.onStartRequest()
        repositorioComentario.getComentariosEnFirestorePorActividad(publicacionId)
            .addSnapshotListener(EventListener { value, e ->
                if (e != null) {
                    listaComentarios.value = null
                    requestListener?.onFailureRequest(e.message!!)
                    return@EventListener
                }

                val comentarios: MutableList<Comentario> = mutableListOf()
                for (doc in value!!) {
                    val comentario = doc.toObject(Comentario::class.java)
                    comentario.usuarioReference =
                        repositorioUsuario.getUsuarioByUid(comentario.usuarioUid)
                    Log.d("Comentario", comentario.toString())
                    comentarios.add(comentario)
                }
                listaComentarios.value = comentarios
                requestListener?.onSuccessRequest()
                listaComentariosAdapter.actualizarComentarios(listaComentarios.value as MutableList<Comentario>)
            })
    }
}
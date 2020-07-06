package com.itaeducativa.android.redita.ui.actividad.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itaeducativa.android.redita.data.repositorios.*

@Suppress("UNCHECKED_CAST")
class ListaActividadesViewModelFactory(
    private val repositorioActividad: RepositorioActividad,
    private val repositorioUsuario: RepositorioUsuario,
    private val repositorioReaccion: RepositorioReaccion,
    private val repositorioAutenticacion: RepositorioAutenticacion,
    private val repositorioHistorial: RepositorioHistorial
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListaActividadesViewModel(
            repositorioActividad,
            repositorioUsuario,
            repositorioReaccion,
            repositorioAutenticacion,
            repositorioHistorial
        ) as T
    }
}
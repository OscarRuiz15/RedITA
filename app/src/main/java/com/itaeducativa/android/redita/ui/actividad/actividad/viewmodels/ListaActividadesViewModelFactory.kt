package com.itaeducativa.android.redita.ui.actividad.actividad.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itaeducativa.android.redita.data.repositorios.RepositorioActividad
import com.itaeducativa.android.redita.data.repositorios.RepositorioUsuario

@Suppress("UNCHECKED_CAST")
class ListaActividadesViewModelFactory(
    private val repositorioActividad: RepositorioActividad,
    private val repositorioUsuario: RepositorioUsuario
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListaActividadesViewModel(
            repositorioActividad,
            repositorioUsuario
        ) as T
    }
}
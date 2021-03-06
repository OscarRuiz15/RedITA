package com.itaeducativa.android.redita.ui.usuario

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders

import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.data.modelos.Usuario
import com.itaeducativa.android.redita.databinding.FragmentPerfilBinding
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.historial.ListaHistorialViewModel
import com.itaeducativa.android.redita.ui.historial.ListaHistorialViewModelFactory
import com.itaeducativa.android.redita.util.fileChooser
import com.itaeducativa.android.redita.util.hideKeyboard
import kotlinx.android.synthetic.main.fragment_perfil.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

private const val USUARIO = "usuario"

private const val ACTION_RESULT_GET_IMAGES = 0

/**
 * A simple [Fragment] subclass.
 * Use the [PerfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PerfilFragment : Fragment(), KodeinAware, RequestListener {
    override val kodein: Kodein by kodein()
    private val listaUsuarioFactory: ListaUsuarioViewModelFactory by instance()
    private val historialFactory: ListaHistorialViewModelFactory by instance()

    private lateinit var listaUsuarioViewModel: ListaUsuarioViewModel
    private lateinit var listaHistoriaViewModel: ListaHistorialViewModel

    private lateinit var usuario: Usuario

    private lateinit var  textInputTelefono: EditText

    private var uriImagen: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            usuario = it.getSerializable(USUARIO) as Usuario
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentPerfilBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_perfil, container, false)
        listaUsuarioViewModel =
            ViewModelProviders.of(this, listaUsuarioFactory).get(ListaUsuarioViewModel::class.java)
        listaHistoriaViewModel =
            ViewModelProviders.of(this, historialFactory).get(ListaHistorialViewModel::class.java)

        binding.usuarioViewModel = listaUsuarioViewModel
        listaUsuarioViewModel.bindUsuario(usuario)

        binding.historialViewModel = listaHistoriaViewModel

        listaHistoriaViewModel.getHistorialByUsuarioUid(usuario.uid)

        binding.textFieldTelefono.setEndIconOnClickListener {
            listaUsuarioViewModel.modificarTelefono()
            context!!.hideKeyboard(activity!!)
        }

        binding.buttonCambiarImagenPerfil.setOnClickListener {
            context!!.fileChooser(this)
        }
        listaUsuarioViewModel.requestListener = this

        textInputTelefono = binding.inputTelefonoPerfil

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        listaUsuarioViewModel.getUsuarioByUid(usuario.uid)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTION_RESULT_GET_IMAGES && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null
        ) {
            uriImagen = data.data!!
            imagenPerfil.setImageURI(uriImagen)
            listaUsuarioViewModel.cambiarImagenPerfil(uriImagen!!, context!!)
        }
    }

    override fun onStop() {
        super.onStop()
        listaUsuarioViewModel.requestListener = null
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param usuario Parameter 1.
         * @return A new instance of fragment PerfilFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(usuario: Usuario) =
            PerfilFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(USUARIO, usuario)
                }
            }
    }

    override fun onStartRequest() {

    }

    override fun onSuccessRequest(response: Any?) {
        when (response) {
            is Usuario -> {
                usuario = response
                listaUsuarioViewModel.bindUsuario(usuario)
                textViewCantidadMeGustaUsuario.text = response.meGusta.toString()
                textViewCantidadNoMeGustaUsuario.text = response.noMeGusta.toString()
                textViewCantidadComentariosUsuario.text = response.comentarios.toString()
                textInputTelefono.setText(response.telefono)
            }
        }
    }

    override fun onFailureRequest(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

package com.itaeducativa.android.redita.ui.login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.itaeducativa.android.redita.R
import com.itaeducativa.android.redita.databinding.ActivitySingUpBinding
import com.itaeducativa.android.redita.network.AutenticacionListener
import com.itaeducativa.android.redita.network.RequestListener
import com.itaeducativa.android.redita.ui.ImageUploadListener
import com.itaeducativa.android.redita.ui.usuario.UsuarioViewModel
import com.itaeducativa.android.redita.ui.usuario.UsuarioViewModelFactory
import com.itaeducativa.android.redita.util.*
import kotlinx.android.synthetic.main.activity_sing_up.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

private const val ACTION_RESULT_GET_IMAGES = 0

class SingUpActivity : AppCompatActivity(), AutenticacionListener, RequestListener,
    ImageUploadListener, KodeinAware {
    override val kodein: Kodein by kodein()
    private val autenticacionFactory: AutenticacionViewModelFactory by instance()
    private val usuarioFactory: UsuarioViewModelFactory by instance()

    private lateinit var usuarioViewModel: UsuarioViewModel
    private lateinit var autenticacionViewModel: AutenticacionViewModel

    var uriImagen: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivitySingUpBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_sing_up)
        autenticacionViewModel = ViewModelProviders.of(this, autenticacionFactory)
            .get(AutenticacionViewModel::class.java)
        binding.autenticacionViewModel = autenticacionViewModel
        usuarioViewModel =
            ViewModelProviders.of(this, usuarioFactory).get(UsuarioViewModel::class.java)
        binding.usuarioViewModel = usuarioViewModel

        autenticacionViewModel.autenticacionListener = this
        usuarioViewModel.requestListener = this
        usuarioViewModel.imageUploadListener = this

        inputNombreCompletoSignUp.editText?.addTextChangedListener(
            TextWatcherValidacionVacio(
                inputNombreCompletoSignUp
            )
        )
        inputEmailSignup.editText?.addTextChangedListener(
            TextWatcherValidacionVacio(
                inputEmailSignup
            )
        )
        inputTelefono.editText?.addTextChangedListener(TextWatcherValidacionVacio(inputTelefono))
        inputPasswordSignup.editText?.addTextChangedListener(
            TextWatcherValidacionVacio(
                inputPasswordSignup
            )
        )
        inputConfirmarPasswordSignup.editText?.addTextChangedListener(
            TextWatcherValidacionVacio(
                inputConfirmarPasswordSignup
            )
        )

        autenticacionViewModel.singUp()
    }

    fun signup(view: View) {
        val textoNombreCompleto = inputNombreCompletoSignUp.editText?.text.toString()
        val textoEmail = inputEmailSignup.editText?.text.toString()
        val textoTelefono = inputTelefono.editText?.text.toString()
        val textoPassword = inputPasswordSignup.editText?.text.toString()
        val textoConfirmarPassword = inputConfirmarPasswordSignup.editText?.text.toString()
        if (textoNombreCompleto.isEmpty()) {
            inputNombreCompletoSignUp.error = getString(R.string.este_campo_no_puede_estar_vacio)
            return
        }
        if (textoEmail.isEmpty()) {
            inputEmailSignup.error = getString(R.string.este_campo_no_puede_estar_vacio)
            return
        }
        if (textoTelefono.isEmpty()) {
            inputTelefono.error = getString(R.string.este_campo_no_puede_estar_vacio)
            return
        }
        if (textoPassword.isEmpty()) {
            inputPasswordSignup.error = getString(R.string.este_campo_no_puede_estar_vacio)
            return
        }
        if (textoPassword.length < 6) {
            inputPasswordSignup.error = getString(R.string.contraseña_corta)
            return
        }
        if (textoConfirmarPassword.isEmpty()) {
            inputConfirmarPasswordSignup.error = getString(R.string.este_campo_no_puede_estar_vacio)
            return
        }
        if (textoConfirmarPassword != textoPassword) {
            inputConfirmarPasswordSignup.error = getString(R.string.no_coinciden)
            return
        }
        autenticacionViewModel.singUp()
    }

    override fun onStarted() {
        buttonRegistarse.isEnabled = false
        progressBarSingUp.visibility = View.VISIBLE
        layoutSignUp.visibility = View.GONE

    }

    override fun onSuccess() {
        progressBarSingUp.visibility = View.GONE
        if (uriImagen != null) {
            usuarioViewModel.uploadProfileImage(uriImagen!!, this)
        } else {
            val uid = autenticacionViewModel.usuario!!.uid
            usuarioViewModel.guardarUsuario(autenticacionViewModel.email!!, uid, null)
        }

    }

    override fun onFailure(mensaje: String) {
        progressBarSingUp.visibility = View.GONE
        buttonRegistarse.isEnabled = true
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        layoutSignUp.visibility = View.VISIBLE
    }

    override fun onStartRequest() {
        progressBarSingUp.visibility = View.VISIBLE
    }

    override fun onSuccessRequest() {
        this.startMainActivity(usuarioViewModel.usuario.value!!)
    }

    override fun onFailureRequest(message: String) {
        buttonRegistarse.isEnabled = true
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        layoutSignUp.visibility = View.VISIBLE
    }

    fun goToLoginActivity(view: View) {
        this.startLoginActivity()
    }

    fun elegirImagen(view: View) {
        this.fileChooser(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTION_RESULT_GET_IMAGES && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null
        ) {
            uriImagen = data.data!!
            imageViewFotoPerfilSignUp.setImageURI(uriImagen)
            imageViewFotoPerfilSignUp.visibility = View.VISIBLE
        }
    }

    override fun onStartUploadImage() {
        progressBarSingUp.visibility = View.VISIBLE
    }

    override fun onSuccessUploadImage(rutaImagen: String) {
        Log.d("ruta", rutaImagen)
        val uid = autenticacionViewModel.usuario!!.uid
        usuarioViewModel.guardarUsuario(autenticacionViewModel.email!!, uid, rutaImagen)
    }

    override fun onFailureUploadImage(message: String) {
        showSnackbar(message, coordinatorSignUp)
        layoutSignUp.visibility = View.GONE
    }

}

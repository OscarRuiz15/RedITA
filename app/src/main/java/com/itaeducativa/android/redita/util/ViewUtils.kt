package com.itaeducativa.android.redita.util

import android.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.android.material.snackbar.Snackbar
import com.itaeducativa.android.redita.MainActivity
import com.itaeducativa.android.redita.data.modelos.Actividad
import com.itaeducativa.android.redita.data.modelos.Reaccion
import com.itaeducativa.android.redita.data.modelos.Usuario
import com.itaeducativa.android.redita.ui.actividad.actividad.ui.ActividadActivity
import com.itaeducativa.android.redita.ui.actividad.actividad.ui.CrearActividadActivity
import com.itaeducativa.android.redita.ui.login.LoginActivity
import com.itaeducativa.android.redita.ui.login.SingUpActivity
import com.itaeducativa.android.redita.ui.vista.VistasActivity

private const val ACTION_RESULT_GET_IMAGES = 0
private const val ACTION_RESULT_GET_VIDEO = 1

fun Context.startMainActivity(usuario: Usuario) =
    Intent(this, MainActivity::class.java).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val bundle = Bundle()
        bundle.putSerializable("usuario", usuario)
        it.putExtras(bundle)
        startActivity(it)
    }

fun Context.startActividadActivity(actividad: Actividad, reaccion: Reaccion?) =
    Intent(this, ActividadActivity::class.java).also {
        val bundle = Bundle()
        bundle.putSerializable("actividad", actividad)
        bundle.putSerializable("reaccion", reaccion)
        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        it.putExtras(bundle)
        startActivity(it)
    }

fun Context.startVistasActivity(actividad: Actividad) =
    Intent(this, VistasActivity::class.java).also {
        val bundle = Bundle()
        bundle.putSerializable("actividad", actividad)
        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        it.putExtras(bundle)
        startActivity(it)
    }

fun Context.startLoginActivity() = Intent(this, LoginActivity::class.java).also {
    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(it)
}

fun Context.startSingUpActivity() = Intent(this, SingUpActivity::class.java).also {
    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    startActivity(it)
}

fun Context.startFormularioActividadActivity(actividad: Actividad?) =
    Intent(this, CrearActividadActivity::class.java).also {

        if (actividad != null) {
            val bundle = Bundle()
            bundle.putSerializable("actividad", actividad)
            it.putExtras(bundle)
        }
        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        startActivity(it)
    }

fun Context.hideKeyboard(activity: Activity) {
    val inputMethodManager: InputMethodManager =
        activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val view: View = activity.currentFocus ?: View(activity)
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.fileChooser(activity: Activity) = Intent().also {
    it.setType("image/*");
    it.setAction(Intent.ACTION_GET_CONTENT)
    activity.startActivityForResult(it, ACTION_RESULT_GET_IMAGES)
}

fun Context.videoChooser(activity: Activity) = Intent().also {
    it.setType("video/*");
    it.setAction(Intent.ACTION_GET_CONTENT)
    activity.startActivityForResult(it, ACTION_RESULT_GET_VIDEO)
}

fun Context.multipleFileChooser(activity: Activity) = Intent().also {
    it.setType("image/*");
    it.setAction(Intent.ACTION_GET_CONTENT)
    it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
    activity.startActivityForResult(it, ACTION_RESULT_GET_IMAGES)
}


fun Context.showSnackbar(mensaje: String, view: View) =
    Snackbar.make(view, mensaje, Snackbar.LENGTH_SHORT).show()

package com.itaeducativa.android.redita

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.itaeducativa.android.redita.data.modelos.Usuario
import com.itaeducativa.android.redita.ui.actividad.actividad.ui.ListaActividadesFragment
import com.itaeducativa.android.redita.ui.actividad.actividad.ui.MisActividadesFragment
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModel
import com.itaeducativa.android.redita.ui.login.AutenticacionViewModelFactory
import com.itaeducativa.android.redita.ui.usuario.PerfilFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MainActivity : AppCompatActivity(), KodeinAware {
    override val kodein: Kodein by kodein()
    private val factory: AutenticacionViewModelFactory by instance()
    private lateinit var autenticacionViewModel: AutenticacionViewModel
    private lateinit var usuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usuario = intent.extras!!.getSerializable("usuario") as Usuario
        Log.d("USuario", usuario.toString())

        autenticacionViewModel =
            ViewModelProviders.of(this, factory).get(AutenticacionViewModel::class.java)

        //bottomBarMenuPrincipal.menu().isVisible = false
        bottomBarMenuPrincipal.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menuInicio -> {
                    openFragment(ListaActividadesFragment.newInstance())
                    true
                }
                R.id.menuPerfil -> {
                    openFragment(PerfilFragment.newInstance("", ""))
                    true

                }
                R.id.logout -> {
                    autenticacionViewModel.logout(this)
                    true

                }
                R.id.menuMisActividades -> {
                    openFragment(MisActividadesFragment.newInstance(usuario))
                    true
                }
                else -> false
            }
        }
        openFragment(ListaActividadesFragment())
    }

    private fun openFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }
}

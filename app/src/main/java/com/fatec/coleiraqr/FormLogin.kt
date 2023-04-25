package com.fatec.coleiraqr

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FormLogin : AppCompatActivity() {
    private var bt_cadastrar: TextView? = null
    private var edit_email: EditText? = null
    private var edit_senha: EditText? = null
    private var bt_login: Button? = null
    private var ProgressBar: ProgressBar? = null
    var mensagens =
        arrayOf("Todos os campos devem ser preenchidos.", "Login realizado com sucesso.")
    private val auth: FirebaseAuth? = null
    private val user: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_login)
        supportActionBar!!.hide()
        IniciarComponentes()
        bt_cadastrar!!.setOnClickListener {
            val intent = Intent(this@FormLogin, FormCadastro::class.java)
            startActivity(intent)
        }
        bt_login!!.setOnClickListener { v ->
            val email = edit_email!!.text.toString()
            val senha = edit_senha!!.text.toString()
            if (email.isEmpty() || senha.isEmpty()) {
                val snackbar = Snackbar.make(v, mensagens[0], Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.WHITE)
                snackbar.setTextColor(Color.BLACK)
                snackbar.show()
            } else {
                AutenticarUsuario(v)
            }
        }
    }

    private fun AutenticarUsuario(view: View) {
        val email = edit_email!!.text.toString()
        val senha = edit_senha!!.text.toString()
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    ProgressBar!!.visibility = View.VISIBLE
                    Handler().postDelayed({ TelaUser() }, 2000)
                } else {
                    val erro: String
                    erro = try {
                        throw task.exception!!
                    } catch (e: Exception) {
                        "Erro ao logar usuário"
                    }
                    val snackbar = Snackbar.make(view, erro, Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.WHITE)
                    snackbar.setTextColor(Color.BLACK)
                    snackbar.show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        val usuarioAtual = FirebaseAuth.getInstance().currentUser
        if (usuarioAtual != null) {
            TelaUser()
        }
    }

    private fun TelaUser() {
        val intent = Intent(this@FormLogin, FormTelaUser::class.java)
        startActivity(intent)
        finish()
    }

    private fun IniciarComponentes() {
        bt_cadastrar = findViewById(R.id.bt_cadastrar)
        edit_email = findViewById(R.id.edit_email)
        edit_senha = findViewById(R.id.edit_senha)
        bt_login = findViewById(R.id.bt_login)
        ProgressBar = findViewById(R.id.progressbar)
    }
}
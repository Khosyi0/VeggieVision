package com.capstone.veggievision.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.capstone.veggievision.databinding.ActivityLoginBinding
import com.capstone.veggievision.ui.main.MainActivity
import com.capstone.veggievision.ui.register.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        showLoading(false)
        firebaseAuth = FirebaseAuth.getInstance()

        setupAction()
    }

    private fun setupAction() {
        binding.apply {
            regButton.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }

            logButton.setOnClickListener {
                showLoading(true)
                val email = fillEmail.text.toString()
                val pw = fillPassword.text.toString()

                if (email.isNotEmpty() && pw.isNotEmpty()) {
                    firebaseAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener {
                        if (it.isSuccessful) {
                            showLoading(false)
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            showLoading(false)
                            val errorMessage = when (it.exception) {
                                is FirebaseAuthInvalidCredentialsException ->
                                    "Email atau password salah."
                                is FirebaseAuthInvalidUserException ->
                                    "Pengguna tidak ditemukan."
                                else -> "Login gagal: ${it.exception?.message}"
                            }
                            showToast(errorMessage)
                        }
                    }
                } else {
                    showToast("Laman masih kosong.")
                }
            }

            forgotButton.setOnClickListener {
                val email = fillEmail.text.toString()
                if (email.isNotEmpty()) {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                           showToast("Email untuk reset password telah dikirim.")
                        } else {
                            showToast("Gagal mengirim email reset password: ${task.exception?.message}")
                        }
                    }
                } else {
                    showToast("Masukkan email untuk reset password.")
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        if(firebaseAuth.currentUser!=null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
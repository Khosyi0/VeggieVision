package com.capstone.veggievision.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.capstone.veggievision.databinding.ActivityRegisterBinding
import com.capstone.veggievision.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        showLoading(false)

        setupAction()
    }

    private fun setupAction() {
        firebaseAuth = FirebaseAuth.getInstance()

        binding.apply {
            regButton.setOnClickListener {
                showLoading(true)
                val name = fillName.text.toString()
                val email = fillEmail.text.toString()
                val pw = fillPassword.text.toString()

                if (name.isNotEmpty() && email.isNotEmpty() && pw.isNotEmpty()){
                    firebaseAuth.createUserWithEmailAndPassword(email,pw).addOnCompleteListener{
                        if(it.isSuccessful){
                            showLoading(false)

                            val user = firebaseAuth.currentUser
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build()

                            user?.updateProfile(profileUpdates)?.addOnCompleteListener { profileUpdateTask ->
                                if (profileUpdateTask.isSuccessful) {
                                    showToast("Akun berhasil dibuat.")
                                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    showToast("Gagal mengupdate profil: ${profileUpdateTask.exception?.message}")
                                }
                            }
                        }
                        else{
                            val errorMessage = when (it.exception) {
                                is FirebaseAuthUserCollisionException -> "Email sudah terdaftar."
                                else -> "Gagal membuat akun: ${it.exception?.message}"
                            }
                            showToast(errorMessage)
                        }
                    }
                }
                else{
                    showToast("Laman masih kosong.")
                }
            }

            logButton.setOnClickListener {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
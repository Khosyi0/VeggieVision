package com.capstone.veggievision.ui.main.user

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capstone.veggievision.databinding.FragmentUserBinding
import com.capstone.veggievision.ui.begin.BeginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = Firebase.auth

        val user = auth.currentUser

        binding.apply {
            user?.let {
                userName.text = it.displayName
                userEmail.text = it.email

                it.photoUrl?.let { url ->
                    Glide.with(this@UserFragment)
                        .load(url)
                        .apply(RequestOptions.circleCropTransform())
                        .into(userPhoto)
                }
            }

            logoutButton.setOnClickListener {
                lifecycleScope.launch {
                    val credentialManager = CredentialManager.create(requireActivity())
                    auth.signOut()
                    credentialManager.clearCredentialState(ClearCredentialStateRequest())
                    startActivity(Intent(requireActivity(), BeginActivity::class.java))
                    requireActivity().finish()
                }
            }

            changePass.setOnClickListener {
                showChangePassDialog()
            }
        }

        return root
    }

    private fun showChangePassDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Change Password")

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 0, 50, 0)

        val newPw = EditText(requireContext())
        newPw.hint = "Enter new password"
        newPw.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        layout.addView(newPw)

        val newPwConfirm = EditText(requireContext())
        newPwConfirm.hint = "Confirm new password"
        newPwConfirm.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        layout.addView(newPwConfirm)

        builder.setView(layout)
        builder.setPositiveButton("OK") { dialog, _ ->
            val newPass = newPw.text.toString().trim()
            val confirmPass = newPwConfirm.text.toString().trim()
            if (newPass.isNotEmpty() && newPass == confirmPass) {
                updatePass(newPass)
                dialog.dismiss()
            }
            else {
                newPw.error = "Passwords do not match"
                showToast(newPw.error.toString())
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun updatePass(newPass: String) {
        val user = auth.currentUser
        user?.updatePassword(newPass)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
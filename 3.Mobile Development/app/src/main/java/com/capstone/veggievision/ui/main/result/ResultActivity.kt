package com.capstone.veggievision.ui.main.result

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.capstone.veggievision.R
import com.capstone.veggievision.data.remote.response.ItemHistory
import com.capstone.veggievision.databinding.ActivityResultBinding
import com.capstone.veggievision.databinding.CustomTitleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

@Suppress("DEPRECATION")
class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val customTitle = CustomTitleBinding.inflate(layoutInflater)
        supportActionBar?.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            setDisplayShowCustomEnabled(true)
            customView = customTitle.root
            setDisplayHomeAsUpEnabled(true)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            titleColor = getColor(R.color.greenApple_700)
        }

        customTitle.actionBarTitle.text = getString(R.string.result_title)

        val label = intent.getStringExtra(EXTRA_LABEL) ?: "Unknown"
        val lastWord = label.split("_").last()
        val lastWordTitle = lastWord.replaceFirstChar { it.uppercase() }

        val percentage = intent.getStringExtra(EXTRA_PERCENTAGE)?.toFloatOrNull() ?: 0f
        val image = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))


        val (freshness, color) = when {
            percentage <= 35 -> "Your $lastWord are stale. Please do not eat it." to Color.RED
            percentage <= 70 -> "Your $lastWord is about to go stale, consume it immediately." to Color.YELLOW
            else -> "Your $lastWord are safe to consume." to Color.GREEN
        }

        binding.apply {
            progressBar.progress = percentage.toInt()
            progressText.text = "$percentage%"

            resultDesc.text = freshness
            resultDesc.setTextColor(color)

            itemName.text = lastWordTitle

            image?.let {
                preview.setImageURI(it)
            }
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser!!.displayName

        val database = FirebaseDatabase.getInstance()
        val historyRef = database.getReference("users").child(userId.toString()).child("history")
        val timestamp = System.currentTimeMillis()

        if (image != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val fileReference = storageRef.child("history/image/${UUID.randomUUID()}.jpg")

            fileReference.putFile(image).addOnSuccessListener {
                fileReference.downloadUrl.addOnSuccessListener { uri ->
                    val historyData = ItemHistory(
                        lastWordTitle,
                        uri.toString(),
                        percentage.toString(),
                        timestamp,
                        freshness
                    )
                    historyRef.child(timestamp.toString()).setValue(historyData)
                }
            }.addOnFailureListener { e ->
                e.printStackTrace()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_LABEL = "extra_label"
        const val EXTRA_PERCENTAGE = "extra_percentage"
    }
}
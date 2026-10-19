package com.greatark.archery.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.greatark.archery.R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        
        // Cek jika sudah login sebelumnya
        if (auth.currentUser != null) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }

        val etEmail = findViewById<EditText>(com.greatark.archery.R.id.etEmail)
        val etPassword = findViewById<EditText>(com.greatark.archery.R.id.etPassword)
        val btnLogin = findViewById<Button>(com.greatark.archery.R.id.btnLogin)
        val progress = findViewById<ProgressBar>(com.greatark.archery.R.id.progressBar)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass = etPassword.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Isi email dan password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progress.visibility = View.VISIBLE
            btnLogin.isEnabled = false

            auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid ?: ""
                        
                        // CEK APAKAH INI ADMIN PERTAMA
                        val db = FirebaseDatabase.getInstance().reference
                        db.child("users").child(uid).get().addOnSuccessListener { snapshot ->
                            if (!snapshot.exists()) {
                                // Jika belum ada data, buat otomatis sebagai admin
                                val userMap = mapOf(
                                    "email" to email,
                                    "name" to "Admin Utama",
                                    "role" to "admin"
                                )
                                db.child("users").child(uid).setValue(userMap).addOnCompleteListener {
                                    progress.visibility = View.GONE
                                    startActivity(Intent(this, DashboardActivity::class.java))
                                    finish()
                                }
                            } else {
                                // Jika sudah ada, langsung masuk
                                progress.visibility = View.GONE
                                startActivity(Intent(this, DashboardActivity::class.java))
                                finish()
                            }
                        }
                    } else {
                        progress.visibility = View.GONE
                        btnLogin.isEnabled = true
                        Toast.makeText(this, "Login Gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}

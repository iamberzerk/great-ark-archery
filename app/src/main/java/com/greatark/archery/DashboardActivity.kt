package com.greatark.archery.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.greatark.archery.R.layout.activity_dashboard)

        val tvWelcome = findViewById<TextView>(com.greatark.archery.R.id.tvWelcome)
        val btnLogout = findViewById<Button>(com.greatark.archery.R.id.btnLogout)
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid

        if (uid != null) {
            FirebaseDatabase.getInstance().reference.child("users").child(uid).get()
                .addOnSuccessListener { snapshot ->
                    val name = snapshot.child("name").getValue(String::class.java) ?: "User"
                    val role = snapshot.child("role").getValue(String::class.java) ?: "atlet"
                    tvWelcome.text = "Halo, $name\n(Role: $role)"
                }
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

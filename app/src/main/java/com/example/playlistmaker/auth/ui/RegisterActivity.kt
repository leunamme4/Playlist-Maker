package com.example.playlistmaker.auth.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivityRegisterBinding
import com.example.playlistmaker.root.RootActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private var binding: ActivityRegisterBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding!!.getRoot())

        binding!!.registerButton.setOnClickListener {
            if (binding!!.emailEt.text.toString().isEmpty() || binding!!.passwordEt.text.toString().isEmpty()
                || binding!!.usernameEt.text.toString().isEmpty()
            ) {
                Toast.makeText(applicationContext, "Поля не могут быть пустые", Toast.LENGTH_SHORT).show()
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding!!.emailEt.text.toString(),
                    binding!!.passwordEt.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userInfo = HashMap<String, String>()
                        userInfo["email"] = binding!!.emailEt.text.toString()
                        userInfo["username"] = binding!!.usernameEt.text.toString()
                        userInfo["description"] = ""

                        FirebaseDatabase.getInstance().reference.child("Users")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .setValue(userInfo)

                        startActivity(Intent(this@RegisterActivity, RootActivity::class.java))
                    }
                }
            }
        }
    }
}
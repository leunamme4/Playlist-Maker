package com.example.playlistmaker.profile.ui

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentProfileBinding
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val name = FirebaseDatabase.getInstance()
            .reference
            .child("Users")
            .child(uid)
            .child("username") // <-- указываешь нужное поле

        name.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.getValue(String::class.java)
                binding.textFullName.text = username.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase_READ", "Failed to read value", error.toException())
            }
        })

        val description = FirebaseDatabase.getInstance()
            .reference
            .child("Users")
            .child(uid)
            .child("description") // <-- указываешь нужное поле
        binding.description.text = description.toString()

        description.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.getValue(String::class.java)
                binding.description.text = username.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase_READ", "Failed to read value", error.toException())
            }
        })
    }
}
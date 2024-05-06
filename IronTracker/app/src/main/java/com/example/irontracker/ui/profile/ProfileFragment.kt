// ProfileFragment.kt
package com.example.irontracker.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.irontracker.R
import com.example.irontracker.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = Firebase.auth

        val textViewProfileName: TextView = binding.profileName
        val textViewEmail: TextView = binding.textEmail
        val buttonLogout: Button = binding.buttonLogout

        val currentUser = auth.currentUser
        textViewProfileName.text = currentUser?.displayName ?: "User"
        textViewEmail.text = currentUser?.email

        textViewProfileName.setOnClickListener {
            showEditNicknameDialog(currentUser)
        }

        buttonLogout.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.action_navigation_profile_to_signInFragment)
        }

        return root
    }

    private fun showEditNicknameDialog(currentUser: FirebaseUser?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_nickname, null)
        val editTextNickname: EditText = dialogView.findViewById(R.id.edit_nickname)
        editTextNickname.setText(currentUser?.displayName)

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Nickname")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                val nickname = editTextNickname.text.toString().trim()
                if (nickname.isNotEmpty()) {
                    val profileUpdates = userProfileChangeRequest {
                        displayName = nickname
                    }
                    currentUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                binding.profileName.text = nickname
                                Toast.makeText(requireContext(), "Nickname updated", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireContext(), "Failed to update nickname", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
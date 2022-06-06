package com.example.sportjournal.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.sportjournal.R
import com.example.sportjournal.models.User
import com.example.sportjournal.utilits.*
import com.google.android.material.textfield.TextInputEditText

class LoginFragment : BaseFragment(R.layout.fragment_login) {

    private lateinit var emailView: TextInputEditText
    private lateinit var passwordView: TextInputEditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFirebase()

        setProgressBar(view.findViewById(R.id.progressBar))
        emailView = view.findViewById(R.id.enterEmail)
        passwordView = view.findViewById(R.id.enterPassword)

        view.findViewById<Button>(R.id.login_btn).setOnClickListener {
            val email = emailView.text.toString().lowercase()
            val password = passwordView.text.toString()
            signIn(email, password)
        }

        view.findViewById<Button>(R.id.reg_btn).setOnClickListener {
            val email = emailView.text.toString().lowercase()
            val password = passwordView.text.toString()
            createAccount(email, password)
        }
    }

    private fun createAccount(email: String, password: String) {
        if (!validateForm(emailView) || !validateForm(passwordView)) {
            return
        }
        showProgressBar()
        AUTH.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val uid = AUTH.currentUser?.uid.toString()

                    val user = User(uid, email, email)

                    REF_DATABASE_ROOT.child(NODE_USERS).child(uid).setValue(user)
                        .addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                baseNavigate(R.id.action_loginFragment_to_registrationSecondFragment)
                            } else showToast(task2.exception?.message.toString())
                        }
                } else {
                    showToast(resources.getString(R.string.AuthFail))
                }
                hideProgressBar()
            }
    }

    private fun signIn(email: String, password: String) {
        if (!validateForm(emailView) || !validateForm(passwordView)) {
            return
        }
        showProgressBar()
        AUTH.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    showToast(resources.getString(R.string.welcome))
                    baseNavigate(R.id.action_loginFragment_to_mainFragment)
                } else showToast(resources.getString(R.string.AuthFail))
                hideProgressBar()
            }
    }
}
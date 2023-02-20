package com.example.sportjournal.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.sportjournal.R
import com.example.sportjournal.utilits.AUTH
import com.example.sportjournal.utilits.baseNavigate


class SplashFragment : Fragment(R.layout.fragment_splash) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.postDelayed({
            if (AUTH.currentUser != null) {
                baseNavigate(R.id.action_splashFragment_to_mainFragment)
            } else {
                baseNavigate(R.id.action_splashFragment_to_loginFragment)
            }
        }, 2000)
    }
}
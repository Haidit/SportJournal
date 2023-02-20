package com.example.sportjournal.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.sportjournal.R
import com.example.sportjournal.databinding.FragmentMainBinding
import com.example.sportjournal.utilits.initFirebase
import com.example.sportjournal.utilits.initUser


class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var binding: FragmentMainBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMainBinding.bind(requireView())

        initFirebase()
        initUser()

        val bottomNavigationView = binding.mainBottomNavigationView
        val navController =
            (childFragmentManager.findFragmentById(R.id.mainContainerView) as NavHostFragment).navController
        bottomNavigationView.setupWithNavController(navController)
    }
}
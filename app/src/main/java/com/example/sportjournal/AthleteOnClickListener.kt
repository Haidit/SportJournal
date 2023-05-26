package com.example.sportjournal

import com.example.sportjournal.models.Athlete

interface AthleteOnClickListener {

    fun onClicked(athlete: Athlete) {

    }

    fun onAcceptButtonClicked(request: Athlete) {

    }

    fun onRejectButtonClicked(request: Athlete) {

    }
}
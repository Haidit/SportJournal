package com.example.sportjournal.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.example.sportjournal.R
import com.example.sportjournal.databinding.FragmentRegistrtationSecondBinding
import com.example.sportjournal.utilits.*
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import java.util.*

class RegistrationSecondFragment : Fragment(R.layout.fragment_registrtation_second) {

    private lateinit var binding: FragmentRegistrtationSecondBinding
    private lateinit var usernameView: TextInputEditText
    private lateinit var sportsView: TextInputEditText
    private lateinit var birthdayView: TextInputEditText
    private lateinit var gender: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRegistrtationSecondBinding.bind(requireView())
        initFirebase()
        val user = REF_DATABASE_ROOT.child(NODE_USERS).child(UID)

        usernameView = binding.enterUsername
        sportsView = binding.enterSport
        birthdayView = binding.enterDOB

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        gender = "Не указан"

        binding.dateBtn.setOnClickListener {
            val dpd = DatePickerDialog(this.requireContext(), {_,mYear,mMonth,mDay->
                val mmMonth = mMonth + 1
                birthdayView.setText("$mDay/$mmMonth/$mYear")
            }, year, month, day)
            dpd.show()
        }
        binding.genderPickGroup.setOnCheckedChangeListener { _, checkedId ->
            view.findViewById<RadioButton>(checkedId)?.apply{
                gender = text.toString()
            }
        }
        binding.doneBtn.setOnClickListener {
            editUser(user)
        }
    }

    private fun editUser(user: DatabaseReference) {
        val username = usernameView.text.toString()
        var sports = sportsView.text.toString()
        if (sports == "") sports = "None"

        if (validateForm(usernameView)){
            user.child(USERNAME).setValue(username)
            user.child(USER_SPORTS).setValue(sports)
            user.child(USER_GENDER).setValue(gender)
            baseNavigate(R.id.action_registrationSecondFragment_to_mainFragment)
            showToast(resources.getString(R.string.welcome))
        }
    }
}
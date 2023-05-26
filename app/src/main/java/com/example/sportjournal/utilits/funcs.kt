package com.example.sportjournal.utilits

import android.text.TextUtils
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.sportjournal.R
import com.google.android.material.textfield.TextInputEditText

fun Fragment.showToast(message: String){
    Toast.makeText(this.context,message,Toast.LENGTH_SHORT).show()
}

fun Fragment.baseNavigate(action: Int){
    findNavController().navigate(
        action,
        bundleOf(),
        navOptions {
            anim {
                enter = R.anim.nav_default_enter_anim
                popEnter = R.anim.nav_default_pop_enter_anim
                popExit = R.anim.nav_default_pop_exit_anim
                exit = R.anim.nav_default_exit_anim
            }
            launchSingleTop = true
            popUpTo = R.id.nav_graph
        }
    )
}

fun Fragment.validateForm(inputField: TextInputEditText): Boolean {
    var valid = true
    if (TextUtils.isEmpty(inputField.text)) {
        inputField.error = resources.getString(R.string.required)
        valid = false
    } else {
        inputField.error = null
    }
    return valid
}

fun Fragment.validateForm(inputField: AutoCompleteTextView): Boolean {
    var valid = true
    if (TextUtils.isEmpty(inputField.text)) {
        inputField.error = resources.getString(R.string.required)
        valid = false
    } else {
        inputField.error = null
    }
    return valid
}



package com.example.sportjournal.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportjournal.*
import com.example.sportjournal.databinding.FragmentSocialBinding
import com.example.sportjournal.databinding.SendRequestDialogBinding
import com.example.sportjournal.models.Athlete
import com.example.sportjournal.models.User
import com.example.sportjournal.utilits.*
import com.google.firebase.database.DatabaseReference

class SocialFragment : Fragment(R.layout.fragment_social) {

    private lateinit var binding: FragmentSocialBinding
    private lateinit var requestsAdapter: RequestsAdapter
    private lateinit var athletesAdapter: AthletesAdapter
    private lateinit var requestsPath: DatabaseReference
    private lateinit var athletesPath: DatabaseReference
    private lateinit var requestsListener: AppValueEventListener
    private lateinit var athletesListener: AppValueEventListener
    private val viewModel: SocialViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSocialBinding.bind(requireView())

        binding.fab.setOnClickListener {
            showSendRequestDialog()
            requestsAdapter.notifyDataSetChanged()
        }

        requestsPath = REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_REQUESTS)
        athletesPath = REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_ATHLETES)

        requestsAdapter = RequestsAdapter(viewModel.requests, object : AthleteOnClickListener {
            override fun onAcceptButtonClicked(request: Athlete) {
                with(athletesPath.child(request.id)) {
                    child(USERNAME).setValue(request.username)
                    child("id").setValue(request.id)
                    child(USER_EMAIL).setValue(request.email)
                }
                requestsPath.child(request.id).removeValue()
            }

            override fun onRejectButtonClicked(request: Athlete) {
                requestsPath.child(request.id).removeValue()
            }
        })

        val requestsRV = binding.requestsRV
        requestsRV.layoutManager = LinearLayoutManager(context)
        requestsRV.adapter = requestsAdapter

        athletesAdapter = AthletesAdapter(viewModel.athletes, object : AthleteOnClickListener {
            override fun onClicked(athlete: Athlete) {
                showToast(athlete.id)
            }
        })

        val athletesRV = binding.athletesRV
        athletesRV.layoutManager = LinearLayoutManager(context)
        athletesRV.adapter = athletesAdapter

        requestsListener = AppValueEventListener { ds1 ->
            viewModel.requests.clear()
            ds1.children.forEach {
                val request = it.getValue(Athlete::class.java) ?: Athlete()
                viewModel.requests.add(request)
            }
            requestsAdapter.notifyDataSetChanged()
        }
        athletesListener = AppValueEventListener { ds1 ->
            viewModel.athletes.clear()
            ds1.children.forEach {
                val athlete = it.getValue(Athlete::class.java) ?: Athlete()
                viewModel.athletes.add(athlete)
            }
            athletesAdapter.notifyDataSetChanged()
        }

        requestsPath.addValueEventListener(requestsListener)
        athletesPath.addValueEventListener(athletesListener)
    }

    override fun onPause() {
        super.onPause()
        requestsPath.removeEventListener(requestsListener)
        athletesPath.removeEventListener(athletesListener)
    }

    override fun onStop() {
        super.onStop()
        viewModel.requests.clear()
        viewModel.athletes.clear()
    }

    private fun showSendRequestDialog() {
        val dialogBinding = SendRequestDialogBinding.inflate(layoutInflater)

        val dialogBuilder =
            androidx.appcompat.app.AlertDialog.Builder(requireContext()).setView(dialogBinding.root)
        val dialog = dialogBuilder.create()
        dialog.show()

        dialogBinding.sendButton.setOnClickListener {
            val email = dialogBinding.email.text.toString().lowercase()

            REF_DATABASE_ROOT.child(NODE_USERS)
                .addListenerForSingleValueEvent(AppValueEventListener { ds1 ->
                    var coachID = ""
                    ds1.children.forEach {
                        if (it.child(USER_EMAIL).value.toString() == email) {
                            coachID = it.child("id").value.toString()
                            val dataMap = mutableMapOf<String, Any>()
                            dataMap["id"] = UID
                            dataMap[USERNAME] = USER.username
                            dataMap[USER_EMAIL] = USER.email
                            REF_DATABASE_ROOT.child(NODE_USERS).child(coachID).child(NODE_REQUESTS)
                                .child(UID).updateChildren(dataMap)

                        }
                    }
                    if (coachID == "") showToast(getString(R.string.no_email_exception))
                })
            dialog.dismiss()
        }
        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
    }
}

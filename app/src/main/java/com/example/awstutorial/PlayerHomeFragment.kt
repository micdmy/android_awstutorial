package com.example.awstutorial

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_player_home.*


class PlayerHomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        UserData.player.observe(viewLifecycleOwner, Observer<UserData.Player> { player ->
            experienceField.text = player.experience.toString()
            userField.text = player.owner
        })

        UserData.isSignedIn.observe(viewLifecycleOwner, Observer<Boolean> { isSignedUp ->
            if (isSignedUp) {
                loginLayout.visibility = GONE
                playersLayout.visibility = VISIBLE
            } else {
                loginLayout.visibility = VISIBLE
                playersLayout.visibility = GONE
            }
        })

        loginButton.setOnClickListener {
            if (UserData.isSignedIn.value!!) {
                Log.e(TAG, "Trying to sign out, but already signed out!")
            } else {
                activity?.let { Backend.signIn(it) }
            }
        }
        logoutButton.setOnClickListener {
            if (UserData.isSignedIn.value!!) {
                Backend.signOut()
            } else {
                Log.e(TAG, "Trying to sign in, but already signed in!")
            }
        }
    }

    companion object {
        private const val TAG = "PlayerHomeFragment"
    }

}
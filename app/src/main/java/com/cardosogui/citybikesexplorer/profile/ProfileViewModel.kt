package com.cardosogui.citybikesexplorer.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    // Static mock profile; there is no backend for user data in this demo.
    val profile: Profile = Profile(
        firstName = "Guilherme",
        lastName = "Cardoso",
        paymentMethod = "Visa •••• 4242",
        supportUrl = "https://support.citybikesexplorer.example",
    )

    data class Profile(
        val firstName: String,
        val lastName: String,
        val paymentMethod: String,
        val supportUrl: String,
    )
}

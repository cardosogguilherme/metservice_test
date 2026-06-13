package com.cardosogui.citybikesexplorer

import com.cardosogui.citybikesexplorer.profile.ProfileViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileViewModelTest {

    @Test
    fun `exposes a complete mock profile`() {
        val profile = ProfileViewModel().profile

        assertEquals("Guilherme", profile.firstName)
        assertEquals("Cardoso", profile.lastName)
        assertTrue(profile.paymentMethod.isNotBlank())
        assertTrue(profile.supportUrl.startsWith("http"))
    }
}

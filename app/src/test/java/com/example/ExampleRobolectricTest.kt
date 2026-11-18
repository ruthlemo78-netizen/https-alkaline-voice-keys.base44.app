package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AlkalineCityWorld
import com.example.model.ProtagonistId
import com.example.viewmodel.AlkalineViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `app name matches Alkaline City`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Alkaline City", appName)
    }

    @Test
    fun `viewmodel switches characters and launches mission`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = AlkalineViewModel(app)

        vm.dismissSplash()
        assertFalse(vm.uiState.value.isSplashVisible)

        vm.switchCharacter(ProtagonistId.KAI)
        assertEquals(ProtagonistId.KAI, vm.uiState.value.activeCharacter)

        val mission = AlkalineCityWorld.missions.first()
        vm.launchMission(mission)
        assertEquals(mission.id, vm.uiState.value.activeMission?.id)
        assertNotNull(vm.uiState.value.waypoint)
    }
}

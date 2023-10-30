package com.facilitation.phone

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.SharedPreferences
import org.assertj.core.api.Assertions;
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.facilitation.phone.utility.SocketHandler
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Before

@RunWith(AndroidJUnit4::class)
class SocketHandlerIntegrationTests {

    private lateinit var socketHandler: SocketHandler
    private lateinit var socket: BluetoothSocket
    private lateinit var appContext:Context
    private lateinit var sharedPreferencesSpotify:SharedPreferences
    private var btAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    @Before
    fun setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        sharedPreferencesSpotify = appContext.getSharedPreferences("SPOTIFY", 0)
        socketHandler = SocketHandler(appContext)
    }

    @Test
    fun checkWhetherTheTokenStoredInSharedPreferencesIsNotNull() {
        Assertions.assertThat(sharedPreferencesSpotify.getString("token", null)).isNotNull
    }
    @Test
    fun ensureThatTheCorrectPackageIsBeingUsed() {
        // Context of the app under test.
        Assertions.assertThat("com.facilitation.phone").isEqualTo(appContext.packageName)
    }
}
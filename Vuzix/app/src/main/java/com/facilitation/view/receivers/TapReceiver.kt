package com.facilitation.view.receivers

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.util.Log
import com.facilitation.view.utility.ITapInput
import com.facilitation.view.utility.MyActivityLifecycleCallbacks
import com.facilitation.view.utility.enums.TapToCommandEnum
import com.tapwithus.sdk.TapListener
import com.tapwithus.sdk.TapSdk
import com.tapwithus.sdk.airmouse.AirMousePacket
import com.tapwithus.sdk.bluetooth.BluetoothManager
import com.tapwithus.sdk.bluetooth.TapBluetoothManager
import com.tapwithus.sdk.mode.RawSensorData
import com.tapwithus.sdk.mouse.MousePacket


class TapReceiver(context: Context, private var activityLifecycleCallbacks: MyActivityLifecycleCallbacks) : TapListener {
  private var bluetoothManager: BluetoothManager = BluetoothManager(context.applicationContext, BluetoothAdapter.getDefaultAdapter())
  private var tapBluetoothManager = TapBluetoothManager(bluetoothManager)
  private var tapSDK = TapSdk(tapBluetoothManager)
  private lateinit var tapID : String
  private var listener : ITapInput? = null

  init {
    tapSDK.registerTapListener(this)
    updateListener()
  }

  private fun booleanToEnum(booleanArray : BooleanArray) : TapToCommandEnum? {
    var xo = ""
    booleanArray.forEach { bool ->
      xo +=
        if (bool) {
        "X"
        } else {
        "O"
      }
    }

    val result = TapToCommandEnum.values().find { it.name == xo }
    Log.d("Tap INFO", "Input = $xo \nParsed into: $result\n")
    return result
  }

  override fun onBluetoothTurnedOn() {
  }

  override fun onBluetoothTurnedOff() {
  }

  override fun onTapStartConnecting(tapIdentifier: String) {
  }

  override fun onTapConnected(tapIdentifier: String) {
    tapID = tapIdentifier
    Log.d("Tap INFO", "Connected to Tap with ID: $tapIdentifier")
  }

  override fun onTapDisconnected(tapIdentifier: String) {
    Log.d("Tap INFO", "Disconnected from device with ID: $tapIdentifier")
    tapID = ""
  }

  override fun onTapResumed(tapIdentifier: String) {
    tapID = tapIdentifier
  }

  override fun onTapChanged(tapIdentifier: String) {
  }

  override fun onTapInputReceived(tapIdentifier: String, data: Int, repeatData: Int) {
    val command = booleanToEnum(TapSdk.toFingers(data))
    if (command != null)
    {
      updateListener()
      listener!!.onInputReceived(command)
    }
  }

  override fun onTapShiftSwitchReceived(tapIdentifier: String, data: Int) {
  }

  override fun onMouseInputReceived(tapIdentifier: String, data: MousePacket) {
  }

  override fun onAirMouseInputReceived(tapIdentifier: String, data: AirMousePacket) {
  }

  override fun onRawSensorInputReceived(tapIdentifier: String, rsData: RawSensorData) {
  }

  override fun onTapChangedState(tapIdentifier: String, state: Int) {
  }

  override fun onError(tapIdentifier: String, code: Int, description: String) {
    Log.e("Tap ERROR", "Error from device with ID: $tapIdentifier\n\tError code: $code\n\tDescription: $description")
  }

  private fun updateListener() {
    val activity = activityLifecycleCallbacks.currentActivity
    if (activity is ITapInput)
      listener = activity
  }
}
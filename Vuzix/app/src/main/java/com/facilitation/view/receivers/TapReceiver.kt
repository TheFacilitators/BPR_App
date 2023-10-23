package com.facilitation.view.receivers

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.util.Log
import com.facilitation.view.utility.ITapInput
import com.facilitation.view.utility.enums.TapToCommandEnum
import com.tapwithus.sdk.TapListener
import com.tapwithus.sdk.TapSdk
import com.tapwithus.sdk.airmouse.AirMousePacket
import com.tapwithus.sdk.bluetooth.BluetoothManager
import com.tapwithus.sdk.bluetooth.TapBluetoothManager
import com.tapwithus.sdk.mode.RawSensorData
import com.tapwithus.sdk.mouse.MousePacket


class TapReceiver(context: Context) : TapListener {

  // Official documentation: https://tapwithus.atlassian.net/wiki/spaces/TD/pages/792002574/Tap+Strap+Raw+Sensors+Mode

  private var bluetoothManager: BluetoothManager = BluetoothManager(context.applicationContext, BluetoothAdapter.getDefaultAdapter())
  private var tapBluetoothManager = TapBluetoothManager(bluetoothManager)
  private var tapSDK = TapSdk(tapBluetoothManager)
  private lateinit var tapID : String
  private var listener : ITapInput? = null

  init {
    tapSDK.registerTapListener(this)
  }

  fun registerListener(listener: ITapInput) {
    this.listener = listener
  }

  fun unregisterListener() {
    this.listener = null
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

    Log.d("Tap INFO", "Input = $xo")
    val result = TapToCommandEnum.values().find { it.name == xo }
    Log.d("Tap INFO", "Input parsed into: $result")
    return result
  }

  override fun onBluetoothTurnedOn() {
    Log.d("Tap INFO", "Bluetooth on")
  }

  override fun onBluetoothTurnedOff() {
    Log.d("Tap INFO", "Bluetooth off")
  }

  override fun onTapStartConnecting(tapIdentifier: String) {
    Log.d("Tap INFO", "Connecting to Tap...")
  }

  override fun onTapConnected(tapIdentifier: String) {
    tapID = tapIdentifier
    Log.d("Tap INFO", "Connected to Tap with ID: $tapIdentifier")
  }

  override fun onTapDisconnected(tapIdentifier: String) {
    Log.d("Tap INFO", "Disconnected from device with ID: $tapIdentifier")
    tapID = ""
    tapSDK.close()
  }

  override fun onTapResumed(tapIdentifier: String) {

  }

  override fun onTapChanged(tapIdentifier: String) {

  }

  override fun onTapInputReceived(tapIdentifier: String, data: Int, repeatData: Int) {
    if (tapIdentifier != tapID) return
    val command = booleanToEnum(TapSdk.toFingers(data))
    if (command != null && listener != null)
      listener!!.onInputReceived(command)
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
}
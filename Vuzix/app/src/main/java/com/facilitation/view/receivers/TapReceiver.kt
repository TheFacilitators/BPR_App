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

/** A class to handle the logic of receiving, translating and relaying input from Tap devices.
 * @constructor
 * @param context the context in which this class should operate.
 * @param activityLifecycleCallbacks the activity lifecycle monitor.*/
class TapReceiver(context: Context, private var activityLifecycleCallbacks: MyActivityLifecycleCallbacks) : TapListener {
  private var bluetoothManager: BluetoothManager = BluetoothManager(context.applicationContext, BluetoothAdapter.getDefaultAdapter())
  private var tapBluetoothManager = TapBluetoothManager(bluetoothManager)
  private var tapSDK = TapSdk(tapBluetoothManager)
  private lateinit var tapID : String
  private var listener : ITapInput? = null

  /** Registers this as a listener with the Tap SDK and calls updateListener().*/
  init {
    tapSDK.registerTapListener(this)
    updateListener()
  }

  /** Converting an array of booleans to a string of 'X's for 'true' and 'O's for 'false'.
   * That string is used to try and find a TapToCommandEnum.
   * @param booleanArray a boolean representation of whether a digit was tapped with each variable corresponding to finger, starting from the thumb.
   * @return If a TapToCommandEnum was found corresponding to the argument array that is returned, otherwise a null.*/
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

  /** Storing the ID of the connected Tap device to filter the input received to only this device.
   * For future development.
   * @param tapIdentifier string received from the Tap device with it's unique ID.*/
  override fun onTapConnected(tapIdentifier: String) {
    tapID = tapIdentifier
    Log.d("Tap INFO", "Connected to Tap with ID: $tapIdentifier")
  }

  /** Clearing the stored tapID.
   * In future development the ID will only be cleared if it matches tapID, however this was
   * deemed unnecessary for now.
   * @param tapIdentifier string received from the Tap device with it's unique ID.*/
  override fun onTapDisconnected(tapIdentifier: String) {
    Log.d("Tap INFO", "Disconnected from device with ID: $tapIdentifier")
    tapID = ""
  }

  /** Storing the ID of the connected Tap device when it resumes.
   * In future development the ID will only be set if it matches tapID, however this was
   * deemed unnecessary for now.
   * @param tapIdentifier string received from the Tap device with it's unique ID.*/
  override fun onTapResumed(tapIdentifier: String) {
    tapID = tapIdentifier
  }

  override fun onTapChanged(tapIdentifier: String) {
  }

  /** Initializes a local variable as the result from calling booleanToEnum().
   * If the result is not null updateListener() and onInputReceived() is called on it.
   * In future development the input will only be handled if the ID matches tapID, however this was
   * deemed unnecessary for now.
   * @param tapIdentifier string received from the Tap device with it's unique ID.
   * @param data integer of the input received.
   * @param repeatData integer of how often the received data was repeated.*/
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

  /** Local error logging for the Tap device.
   * @param tapIdentifier string received from the Tap device with it's unique ID.
   * @param code the integer code of the error.
   * @param description a string describing the error.*/
  override fun onError(tapIdentifier: String, code: Int, description: String) {
    Log.e("Tap ERROR", "Error from device with ID: $tapIdentifier\n\tError code: $code\n\tDescription: $description")
  }

  /** Gets the currentActivity property of the activityLifecycleCallbacks.
   * If the activity implements ITapInput the 'listener' property is updated with it.*/
  private fun updateListener() {
    val activity = activityLifecycleCallbacks.currentActivity
    if (activity is ITapInput)
      listener = activity
  }
}
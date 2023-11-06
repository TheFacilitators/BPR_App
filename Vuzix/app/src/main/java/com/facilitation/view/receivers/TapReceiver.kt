package com.facilitation.view.receivers

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.util.Log
import com.facilitation.view.ViewApplication
import com.facilitation.view.utility.IGlobalListener
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
import java.io.Serializable
import java.lang.NullPointerException

class TapReceiver(context: Context, private var activityLifecycleCallbacks: MyActivityLifecycleCallbacks) : TapListener, Serializable {
  @Transient private var bluetoothManager: BluetoothManager = BluetoothManager(context.applicationContext, BluetoothAdapter.getDefaultAdapter())
  @Transient private var tapBluetoothManager = TapBluetoothManager(bluetoothManager)
  @Transient private var tapSDK = TapSdk(tapBluetoothManager)
  private lateinit var tapID: String
  private var listener: ITapInput? = null
  private val globalListener: IGlobalListener

  init {
    tapSDK.registerTapListener(this)
    globalListener = context as IGlobalListener
    updateListener()
  }

  private fun booleanToEnum(booleanArray : BooleanArray) : TapToCommandEnum {
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
    if (result == null) {
      throw TypeNotPresentException("Tap enum not found: $xo", result)
    }
    return result
  }

  private fun isGlobalCommand(command: TapToCommandEnum): Boolean {
    return command == TapToCommandEnum.XOXXO || command == TapToCommandEnum.XXXOO
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
  }

  override fun onTapResumed(tapIdentifier: String) {
    tapID = tapIdentifier
  }

  override fun onTapChanged(tapIdentifier: String) {

  }

  override fun onTapInputReceived(tapIdentifier: String, data: Int, repeatData: Int) {
    try {
      val command = booleanToEnum(TapSdk.toFingers(data))
      if (isGlobalCommand(command)) {
        globalListener.onGlobalInputReceived(command)
        return
      }
      updateListener()
      listener!!.onInputReceived(command)
    } catch (e: TypeNotPresentException) {
      Log.e("Tap receiver ERROR", e.typeName())
    }
  }

  override fun onTapShiftSwitchReceived(tapIdentifier: String, data: Int) {
  }

  override fun onMouseInputReceived(tapIdentifier: String, data: MousePacket) {
  }

  override fun onAirMouseInputReceived(tapIdentifier: String, data: AirMousePacket) {
  }

  override fun onRawSensorInputReceived(tapIdentifier: String, rsData: RawSensorData) {
    Log.d("Tap INFO", "Tap is in raw sensor mode")
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
package com.facilitation.view.receivers

import android.util.Log
import com.facilitation.view.utility.TapInputHandler
import com.facilitation.view.utility.enums.TapToCommandEnum
import com.tapwithus.sdk.TapListener
import com.tapwithus.sdk.TapSdk
import com.tapwithus.sdk.airmouse.AirMousePacket
import com.tapwithus.sdk.mode.RawSensorData
import com.tapwithus.sdk.mouse.MousePacket

class TapReceiver(private val handler: TapInputHandler, tapSDK: TapSdk) : TapListener {

  // Official documentation: https://tapwithus.atlassian.net/wiki/spaces/TD/pages/792002574/Tap+Strap+Raw+Sensors+Mode

  private val tapSdk : TapSdk = tapSDK
  // Valid range is 1-5, default is 0
  private var thumbAccSensitivity : Byte = 0
  // Valid range is 1-4, default is 0
  private var gyroSensitivity : Byte = 0
  // Valid range is 1-4, default is 0
  private var accSensitivity : Byte = 0
  private lateinit var tapID : String


//    fun handleInput(event: KeyEvent, keyCode: Int?): Boolean {
//         = when (keyCode) {
//            KeyEvent.KEYCODE_DPAD_UP -> if (currentDirection != Snake.Direction.DOWN) Snake.Direction.UP else currentDirection
//            KeyEvent.KEYCODE_DPAD_DOWN -> if (currentDirection != Snake.Direction.UP) Snake.Direction.DOWN else currentDirection
//            KeyEvent.KEYCODE_DPAD_LEFT -> if (currentDirection != Snake.Direction.RIGHT) Snake.Direction.LEFT else currentDirection
//            KeyEvent.KEYCODE_DPAD_RIGHT -> if (currentDirection != Snake.Direction.LEFT) Snake.Direction.RIGHT else currentDirection
//            else -> currentDirection
//        }
//        return true
//    }

  init {
    tapSDK.registerTapListener(this)
  }

  private fun booleanToEnum(booleanArray : BooleanArray) : TapToCommandEnum? {
    var xo = ""
    for (i in booleanArray) {
      xo += if (i) "X"
      else "O"
    }
    var result : TapToCommandEnum? = null
    try
    {
      result = TapToCommandEnum.valueOf(xo)
    }
    catch (e : java.lang.IllegalArgumentException) {
      Log.e("Tap ERROR", "$xo is not a valid command")
    }
    return result
  }

  override fun onBluetoothTurnedOn() {
    Log.d("Tap INFO", "Bluetooth on")
  }

  override fun onBluetoothTurnedOff() {
    Log.d("Tap INFO", "Bluetooth off")
  }

  override fun onTapStartConnecting(tapIdentifier: String) {
    Log.d("Tap INFO", "Connecting to Tap with ID: $tapIdentifier")
  }

  override fun onTapConnected(tapIdentifier: String) {
    tapID = tapIdentifier
    Log.d("Tap INFO", "Connected to Tap with ID: $tapIdentifier")
    tapSdk.startRawSensorMode(tapID, accSensitivity, gyroSensitivity, thumbAccSensitivity)
    Log.d("Tap INFO", "Started raw sensor mode")
  }

  override fun onTapDisconnected(tapIdentifier: String) {
    Log.d("Tap INFO", "Disconnected from device with ID: $tapIdentifier")
    tapID = ""
  }

  override fun onTapResumed(tapIdentifier: String) {
    TODO("Not yet implemented")
  }

  override fun onTapChanged(tapIdentifier: String) {
    TODO("Not yet implemented")
  }

  override fun onTapInputReceived(tapIdentifier: String, data: Int, repeatData: Int) {
    if (tapIdentifier != tapID) return
    val fingers = BooleanArray(5)
    for (i in 0 until 5) {
      fingers[i] = (1 shl i and data) != 0
    }
    val command = booleanToEnum(fingers)
    if (command != null) handler.handleInput(command)
  }

  override fun onTapShiftSwitchReceived(tapIdentifier: String, data: Int) {
    TODO("Not yet implemented")
  }

  override fun onMouseInputReceived(tapIdentifier: String, data: MousePacket) {
    TODO("Not yet implemented")
  }

  override fun onAirMouseInputReceived(tapIdentifier: String, data: AirMousePacket) {
    TODO("Not yet implemented")
  }

  override fun onRawSensorInputReceived(tapIdentifier: String, rsData: RawSensorData) {
    TODO("Not yet implemented")
    // RawSensorData has timestamp, data type and array points
    // Each point in the array corresponds to the accelerometer value of a finger from thumb to pinky
//    if (rsData.dataType == RawSensorData.DataType.Device) {
//      val index : Point3 = rsData.getPoint(RawSensorData.iDEV_INDEX)
//
//      if (index != null) {
//        val x : Double = index.x
//        val y : Double = index.y
//        val z : Double = index.z
//      }
//    }
//// Fingers accelerometer.
//      // Each point in array represents the accelerometer value of a finger (thumb, index, middle, ring, pinky).
//      Point3 thumb = rsData.getPoint(RawSensorData.iDEV_INDEX);
//      if (thumb != null) {
//        double x = thumb.x;
//        double y = thumb.y;
//        double z = thumb.z;
//      }
//      // Etc... use indexes: RawSensorData.iDEV_THUMB, RawSensorData.iDEV_INDEX, RawSensorData.iDEV_MIDDLE, RawSensorData.iDEV_RING, RawSensorData.iDEV_PINKY
//    } else if (data.dataType == RawSensorData.DataType.IMU) {
//      // Refers to an additional accelerometer on the Thumb sensor and a Gyro (placed on the thumb unit as well).
//      Point3 gyro = rsData.getPoint(RawSensorData.iIMU_GYRO);
//      if (point3 != null) {
//        double x = gyro.x;
//        double y = gyro.y;
//        double z = gyro.z;
//      }
//      // Etc... use indexes: RawSensorData.iIMU_GYRO, RawSensorData.iIMU_ACCELEROMETER
//    }
  }

  override fun onTapChangedState(tapIdentifier: String, state: Int) {
    TODO("Not yet implemented")
  }

  override fun onError(tapIdentifier: String, code: Int, description: String) {
    Log.e("Tap ERROR", "Error from device with ID: $tapIdentifier\n\tError code: $code\n\tDescription: $description")
  }
}
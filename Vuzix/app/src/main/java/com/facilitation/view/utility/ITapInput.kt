package com.facilitation.view.utility

import com.facilitation.view.utility.enums.TapToCommandEnum

/** Interface to implement for methods to relay commands from Tap devices.*/
interface ITapInput {
	/** The method to override for receiving TapToCommandEnum.
	 * @param commandEnum the TapToCommandEnum to handle.*/
	fun onInputReceived(commandEnum: TapToCommandEnum)
}
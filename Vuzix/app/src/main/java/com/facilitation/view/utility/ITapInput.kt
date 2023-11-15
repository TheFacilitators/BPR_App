package com.facilitation.view.utility

import com.facilitation.view.utility.enums.TapToCommandEnum

interface ITapInput {
	fun onInputReceived(commandEnum: TapToCommandEnum)
}
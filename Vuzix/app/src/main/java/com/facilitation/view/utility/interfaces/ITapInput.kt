package com.facilitation.view.utility.interfaces

import com.facilitation.view.model.TrackDTO
import com.facilitation.view.utility.enums.TapToCommandEnum

interface ITapInput {
	fun onInputReceived(commandEnum: TapToCommandEnum)
}
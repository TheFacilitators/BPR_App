package com.facilitation.phone.utility.authorization

import android.app.Activity
import com.facilitation.phone.R
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class AuthorizationHandler: IAuthorization {
    override fun authorizationSpotify(activity: Activity) {
        val builder: AuthorizationRequest.Builder =
            AuthorizationRequest.Builder(
                activity.getString(R.string.client_id),
                AuthorizationResponse.Type.TOKEN,
                activity.getString(R.string.redirect_uri)
            )

        builder.setScopes(arrayOf("streaming"))
        val request: AuthorizationRequest = builder.build()
        AuthorizationClient.openLoginActivity(activity, 9485, request)
    }
}
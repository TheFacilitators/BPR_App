package com.facilitation.phone.viewModel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.facilitation.phone.R
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class LoginViewModel(private val appContext: Application) : AndroidViewModel(appContext) {
    private val requestCode = 9485
    var IsLoggedIn = false
    var EmailAddress:String = ""

     fun initSpotifyAuth(activity: Activity) {
        val builder = AuthorizationRequest.Builder(R.string.client_id.toString(),
            AuthorizationResponse.Type.TOKEN,
            R.string.redirect_uri.toString())

        builder.setScopes(arrayOf("streaming"))
        val request = builder.build()

        AuthorizationClient.openLoginActivity(activity, requestCode, request)
    }
}
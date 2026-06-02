package com.example.productsapp.ui.auth

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

fun showBiometricPrompt(
    activity: FragmentActivity,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val executor = ContextCompat.getMainExecutor(activity)

    val callback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            onSuccess()
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON
            ) {
                onError(errString.toString())
            }
        }

        override fun onAuthenticationFailed() {
            onError("Authentication failed")
        }
    }

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Biometric Login")
        .setSubtitle("Use your fingerprint or face to login")
        .setAllowedAuthenticators(
            BiometricManager.Authenticators.BIOMETRIC_WEAK or
                    DEVICE_CREDENTIAL
        )
        .build()

    BiometricPrompt(activity, executor, callback).authenticate(promptInfo)
}

fun isBiometricAvailable(activity: FragmentActivity): Boolean {
    val biometricManager = BiometricManager.from(activity)
    return biometricManager.canAuthenticate(
        BiometricManager.Authenticators.BIOMETRIC_WEAK or
                DEVICE_CREDENTIAL
    ) == BiometricManager.BIOMETRIC_SUCCESS
}
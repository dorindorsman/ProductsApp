package com.example.productsapp.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.material3.ripple
import androidx.compose.ui.Modifier

fun Modifier.clickableWithRipple(onClick: () -> Unit): Modifier =
    this.clickable(
        indication = ripple(),
        interactionSource = null,
        onClick = onClick
    )
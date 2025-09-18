package com.insurancereminder.android.ui.utils

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

enum class FormFactor {
    PHONE,
    TABLET,
    FOLDABLE
}

@Composable
fun getFormFactor(windowSizeClass: WindowSizeClass): FormFactor {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.smallestScreenWidthDp >= 600
    val isFoldable = configuration.screenWidthDp > configuration.screenHeightDp * 1.5

    return when {
        isFoldable -> FormFactor.FOLDABLE
        isTablet || windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded -> FormFactor.TABLET
        else -> FormFactor.PHONE
    }
}

@Composable
fun getColumnsForFormFactor(formFactor: FormFactor): Int {
    return when (formFactor) {
        FormFactor.PHONE -> 1
        FormFactor.TABLET -> 2
        FormFactor.FOLDABLE -> 3
    }
}
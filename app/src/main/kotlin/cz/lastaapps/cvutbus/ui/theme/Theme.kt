/*
 * Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
 *
 * This file is part of ČVUT Bus.
 *
 * ČVUT Bus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ČVUT Bus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ČVUT Bus.  If not, see <https://www.gnu.org/licenses/>.
 */

package cz.lastaapps.cvutbus.ui.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.lighthousegames.logging.logging

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    useCustomTheme: Boolean = false,
    updateSystemBars: Boolean = false,
    content: @Composable () -> Unit
) {
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !useCustomTheme
    val colorScheme = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> DarkThemeColors
        else -> LightThemeColors
    }.animated()

    if (updateSystemBars) {
        val systemUiController = rememberSystemUiController()

        SideEffect {
            logging("AppTheme").i { "Updating system bar colors" }
            systemUiController.setStatusBarColor(
                color = colorScheme.background,
                darkIcons = !darkTheme,
            )
            systemUiController.setNavigationBarColor(
                color = colorScheme.surfaceVariant,
                darkIcons = !darkTheme,
            )
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}

@Composable
private fun ColorScheme.animated(): ColorScheme {
    return ColorScheme(
        background = animateColorAsState(background).value,
        error = animateColorAsState(error).value,
        errorContainer = animateColorAsState(errorContainer).value,
        inverseOnSurface = animateColorAsState(inverseOnSurface).value,
        inversePrimary = animateColorAsState(inversePrimary).value,
        inverseSurface = animateColorAsState(inverseSurface).value,
        onBackground = animateColorAsState(onBackground).value,
        onError = animateColorAsState(onError).value,
        onErrorContainer = animateColorAsState(onErrorContainer).value,
        onPrimary = animateColorAsState(onPrimary).value,
        onPrimaryContainer = animateColorAsState(onPrimaryContainer).value,
        onSecondary = animateColorAsState(onSecondary).value,
        onSecondaryContainer = animateColorAsState(onSecondaryContainer).value,
        onSurface = animateColorAsState(onSurface).value,
        onSurfaceVariant = animateColorAsState(onSurfaceVariant).value,
        onTertiary = animateColorAsState(onTertiary).value,
        onTertiaryContainer = animateColorAsState(onTertiaryContainer).value,
        outline = animateColorAsState(outline).value,
        primary = animateColorAsState(primary).value,
        primaryContainer = animateColorAsState(primaryContainer).value,
        secondary = animateColorAsState(secondary).value,
        secondaryContainer = animateColorAsState(secondaryContainer).value,
        surface = animateColorAsState(surface).value,
        surfaceVariant = animateColorAsState(surfaceVariant).value,
        surfaceTint = animateColorAsState(targetValue = surfaceTint).value,
        tertiary = animateColorAsState(tertiary).value,
        tertiaryContainer = animateColorAsState(tertiaryContainer).value,
    )
}
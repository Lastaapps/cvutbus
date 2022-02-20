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

package cz.lastaapps.cvutbus.components.license.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.util.withContext
import cz.lastaapps.cvutbus.ui.providers.LocalWindowWidth
import cz.lastaapps.cvutbus.ui.providers.WindowSizeClass
import cz.lastaapps.cvutbus.ui.root.BackArrowAndHandler

@Composable
fun LicenseLayout(
    navController: NavController,
    modifier: Modifier
) {

    val context = LocalContext.current
    var libraries by remember {
        mutableStateOf<Libs?>(null)
    }
    LaunchedEffect(libraries) {
        libraries = Libs.Builder().withContext(context).build()
    }

    var selectedArtifactUniqueId by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedLibrary = remember(libraries, selectedArtifactUniqueId) {
        libraries?.libraries?.firstOrNull { it.uniqueId == selectedArtifactUniqueId }
    }
    val onLibrarySelected: (Library?) -> Unit = { selectedArtifactUniqueId = it?.uniqueId }

    if (libraries == null)
        return

    when (LocalWindowWidth.current) {
        WindowSizeClass.COMPACT -> LicenseLayoutCompact(
            libraries!!.libraries,
            selectedLibrary,
            onLibrarySelected,
            modifier
        )
        WindowSizeClass.MEDIUM -> LicenseLayoutExpanded(
            libraries = libraries!!.libraries,
            selectedLibrary = selectedLibrary,
            onLibrarySelected = onLibrarySelected,
        )
        WindowSizeClass.EXPANDED -> LicenseLayoutExpanded(
            libraries = libraries!!.libraries,
            selectedLibrary = selectedLibrary,
            onLibrarySelected = onLibrarySelected,
        )
    }

    BackArrowAndHandler { navController.navigateUp() }
}

@Composable
private fun LicenseLayoutCompact(
    libraries: List<Library>,
    selectedLibrary: Library?,
    onLibrarySelected: (Library?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LibraryList(libraries, onLibrarySelected = onLibrarySelected, modifier)

    if (selectedLibrary != null) {
        Dialog(onDismissRequest = { onLibrarySelected(null) }) {
            Surface(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                LibraryDetail(library = selectedLibrary, Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
private fun LicenseLayoutExpanded(
    libraries: List<Library>,
    selectedLibrary: Library?,
    onLibrarySelected: (Library?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        LibraryList(
            libraries, onLibrarySelected,
            Modifier
                .fillMaxHeight()
                .weight(1f)
        )
        LibraryDetailWrapper(
            selectedLibrary,
            Modifier
                .fillMaxHeight()
                .weight(1f)
        )
    }
}


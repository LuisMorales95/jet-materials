/*
 * Copyright (c) 2021 Kodeco Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
@file:OptIn(ExperimentalMaterialApi::class)

package com.yourcompany.android.jetnotes.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomDrawer
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.rememberBottomDrawerState
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourcompany.android.jetnotes.domain.model.ColorModel
import com.yourcompany.android.jetnotes.domain.model.NEW_NOTE_ID
import com.yourcompany.android.jetnotes.domain.model.NoteModel
import com.yourcompany.android.jetnotes.theme.JetNotesTheme
import com.yourcompany.android.jetnotes.ui.components.NoteColor
import com.yourcompany.android.jetnotes.util.fromHex
import com.yourcompany.android.jetnotes.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SaveNoteScreen(
	viewModel: MainViewModel,
	onNavigateBack: () -> Unit
) {
	
	val noteEntry: NoteModel by viewModel.noteEntry
		.observeAsState(NoteModel())
	val drawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)
	val colors: List<ColorModel> by viewModel.colors
		.observeAsState(initial = emptyList())
	val coroutineScope = rememberCoroutineScope()
	val moveNoteToTrashDialogShownState = rememberSaveable {
		mutableStateOf(false)
	}
	
	Scaffold(
		topBar = {
			val isEditingMode: Boolean = noteEntry.id != NEW_NOTE_ID
			SaveNoteTopAppBar(
				isEditingMode = isEditingMode,
				onBackClick = {
					onNavigateBack.invoke()
				},
				onSaveNoteClick = {
					viewModel.saveNote(noteEntry)
					onNavigateBack.invoke()
				},
				onColorPickerClick = {
					coroutineScope.launch {
						drawerState.open()
					}
				},
				onDeleteClick = {
					moveNoteToTrashDialogShownState.value = true
				}
			)
		},
		content = {
			BottomDrawer(
				modifier = Modifier.padding(it),
				drawerState = drawerState,
				drawerContent = {
					ColorPicker(colors = colors, onColorSelect = { colorModel ->
						viewModel.onNoteEntryChange(noteEntry.copy(color = colorModel))
					})
				},
				content = {
					SaveNoteContent(
						noteModel = noteEntry,
						onNoteChange = { noteModel ->
							viewModel.onNoteEntryChange(noteModel)
						}
					)
					if (moveNoteToTrashDialogShownState.value) {
						AlertDialog(
							onDismissRequest = {
								moveNoteToTrashDialogShownState.value = false
							},
							title = {
								Text(text = "Move note to trash?")
							},
							text = {
								Text(text = "Are you sure you wish to move this to trash?")
							},
							confirmButton = {
								TextButton(onClick = {
									viewModel.moveNoteToTrash(noteEntry)
									onNavigateBack.invoke()
								}) {
									Text(text = "Confirm")
								}
							},
							dismissButton = {
								TextButton(onClick = {
									moveNoteToTrashDialogShownState.value = false
								}) {
									Text(text = "Dismiss")
								}
							}
						)
					}
				}
			)
		}
	)
}

@Composable
fun SaveNoteTopAppBar(
	isEditingMode: Boolean,
	onBackClick: () -> Unit,
	onSaveNoteClick: () -> Unit,
	onColorPickerClick: () -> Unit,
	onDeleteClick: () -> Unit,
) {
	TopAppBar(
		title = {
			Text(
				text = "Save Note",
				color = MaterialTheme.colors.surface
			)
		},
		navigationIcon = {
			IconButton(onClick = onBackClick) {
				Icon(
					imageVector = Icons.Filled.ArrowBack,
					contentDescription = "Navigate Back",
					tint = MaterialTheme.colors.surface
				)
			}
		},
		contentColor = MaterialTheme.colors.surface,
		actions = {
			IconButton(onClick = onSaveNoteClick) {
				Icon(
					imageVector = Icons.Filled.Check,
					contentDescription = "Save Note Button",
					tint = MaterialTheme.colors.surface
				)
			}
			
			IconButton(onClick = onColorPickerClick) {
				Icon(
					imageVector = Icons.Filled.ColorLens,
					contentDescription = "Color Picker Button",
					tint = MaterialTheme.colors.surface
				)
			}
			if (isEditingMode) {
				IconButton(onClick = onDeleteClick) {
					Icon(
						imageVector = Icons.Filled.RestoreFromTrash,
						contentDescription = "Delete Note Button ",
						tint = MaterialTheme.colors.surface
					)
				}
			}
		}
	)
}

@Preview(showBackground = true)
@Composable
fun SaveNoteTopAppBarPreview() {
	JetNotesTheme {
		Column(
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			
			SaveNoteTopAppBar(
				isEditingMode = false,
				onBackClick = {},
				onSaveNoteClick = {},
				onColorPickerClick = {},
				onDeleteClick = {}
			)
			
			SaveNoteTopAppBar(
				isEditingMode = true,
				onBackClick = {},
				onSaveNoteClick = {},
				onColorPickerClick = {},
				onDeleteClick = {}
			)
		}
	}
}

@Composable
fun SaveNoteContent(
	noteModel: NoteModel,
	onNoteChange: (NoteModel) -> Unit
) {
	
	val canBeCheckedOff: Boolean = noteModel.isCheckedOff != null
	
	Column(
		modifier = Modifier.fillMaxSize()
	) {
		ContentTextField(
			label = "Title",
			text = noteModel.title,
			onTextChanged = { newTitle ->
				onNoteChange.invoke(noteModel.copy(title = newTitle))
			}
		)
		ContentTextField(
			label = "Body",
			text = noteModel.content,
			onTextChanged = { newContent ->
				onNoteChange.invoke(noteModel.copy(content = newContent))
			},
			modifier = Modifier
				.heightIn(max = 240.dp)
				.padding(top = 16.dp)
		)
		NoteCheckOption(
			isChecked = canBeCheckedOff,
			onCheckedChange = { canBeCheckedOffNewValue ->
				val isCheckedOff: Boolean? = if (canBeCheckedOffNewValue) false else null
				onNoteChange.invoke(noteModel.copy(isCheckedOff = isCheckedOff))
			}
		)
		PickedColor(color = noteModel.color)
	}
}

@Preview
@Composable
fun SaveNoteContentPreview() {
	JetNotesTheme {
		SaveNoteContent(
			noteModel = NoteModel(
				title = "title",
				content = "content"
			),
			onNoteChange = {}
		)
	}
}

@Composable
fun ContentTextField(
	modifier: Modifier = Modifier,
	label: String,
	text: String,
	onTextChanged: (String) -> Unit
) {
	TextField(
		value = text,
		onValueChange = onTextChanged,
		label = {
			Text(text = label)
		},
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = 8.dp),
		colors = TextFieldDefaults.textFieldColors(
			backgroundColor = MaterialTheme.colors.surface
		)
	)
}

@Preview
@Composable
fun ContentTextFieldPreview() {
	JetNotesTheme {
		Column {
			ContentTextField(
				modifier = Modifier,
				label = "Title",
				text = "",
				onTextChanged = {}
			)
		}
	}
}

@Composable
fun PickedColor(color: ColorModel) {
	Row(
		modifier = Modifier
			.padding(8.dp)
			.padding(top = 16.dp)
	) {
		Text(
			text = "Picked Color",
			modifier = Modifier
				.weight(1f)
				.align(Alignment.CenterVertically)
		)
		NoteColor(
			color = Color.fromHex(color.hex),
			size = 40.dp,
			border = 1.dp,
			modifier = Modifier.padding(4.dp)
		)
	}
}

@Preview(showBackground = true)
@Composable
fun PickedColorPreview() {
	JetNotesTheme {
		PickedColor(color = ColorModel.DEFAULT)
	}
}

@Composable
fun NoteCheckOption(
	isChecked: Boolean,
	onCheckedChange: (Boolean) -> Unit
) {
	Row(
		modifier = Modifier
			.padding(8.dp)
			.padding(top = 26.dp)
	) {
		Text(
			text = "Can note be checked off?",
			modifier = Modifier
				.weight(1f)
				.align(Alignment.CenterVertically)
		)
		Switch(
			checked = isChecked,
			onCheckedChange = onCheckedChange,
			modifier = Modifier.padding(start = 8.dp)
		)
	}
}

@Preview
@Composable
fun NoteCheckOptionPreview() {
	JetNotesTheme {
		NoteCheckOption(
			isChecked = false,
			onCheckedChange = {}
		)
	}
}

@Composable
private fun ColorPicker(
	colors: List<ColorModel>,
	onColorSelect: (ColorModel) -> Unit
) {
	Column(modifier = Modifier.fillMaxWidth()) {
		Text(
			text = "Color picker",
			fontSize = 18.sp,
			fontWeight = FontWeight.Bold,
			modifier = Modifier.padding(8.dp)
		)
		LazyColumn(modifier = Modifier.fillMaxWidth()) {
			items(colors.size) { itemIndex ->
				val color = colors[itemIndex]
				ColorItem(
					color = color,
					onColorSelect = onColorSelect
				)
			}
		}
	}
}

@Composable
fun ColorItem(
	color: ColorModel,
	onColorSelect: (ColorModel) -> Unit
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(
				onClick = {
					onColorSelect(color)
				}
			)
	) {
		NoteColor(
			modifier = Modifier.padding(10.dp),
			color = Color.fromHex(color.hex),
			size = 80.dp,
			border = 2.dp
		)
		Text(
			text = color.name,
			fontSize = 22.sp,
			modifier = Modifier
				.padding(horizontal = 16.dp)
				.align(Alignment.CenterVertically)
		)
	}
}

@Preview(showBackground = true)
@Composable
fun ColorItemPreview() {
	ColorItem(ColorModel.DEFAULT) {}
}

@Preview(showBackground = true)
@Composable
fun ColorPickerPreview() {
	ColorPicker(
		colors = listOf(
			ColorModel.DEFAULT,
			ColorModel.DEFAULT,
			ColorModel.DEFAULT
		)
	) { }
}
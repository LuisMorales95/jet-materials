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
package com.yourcompany.android.jetnotes.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yourcompany.android.jetnotes.domain.model.NoteModel
import com.yourcompany.android.jetnotes.theme.JetNotesTheme
import com.yourcompany.android.jetnotes.ui.components.Note
import com.yourcompany.android.jetnotes.viewmodel.MainViewModel

@Composable
fun NotesScreen(
	viewModel: MainViewModel,
	onOpenNavigationDrawer: () -> Unit,
	onNavigateToSaveNote: () -> Unit
) {

	val notes: List<NoteModel> by viewModel
		.notesNotInTrash
		.observeAsState(listOf())
	Scaffold(
		floatingActionButtonPosition = FabPosition.End,
		floatingActionButton = {
			AddNoteButton(
				onNavigateToSaveNote = {
					viewModel.onCreateNewNoteClick()
					onNavigateToSaveNote.invoke()
				}
			)
		},
		topBar = {
			TopAppBar(
				title = {
					Text(
						text = "JetNotes",
						color = MaterialTheme.colors.onPrimary
					)
				},
				navigationIcon = {
					IconButton(onClick = {
						onOpenNavigationDrawer.invoke()
					}) {
						Icon(
							imageVector = Icons.Filled.List,
							contentDescription = "Drawer Button"
						)
					}
				}
			)
		},
		content = {
			if (notes.isNotEmpty()) {
				NotesList(
					Modifier.padding(it),
					notes = notes,
					onNoteCheckedChange = { viewModel.onNoteCheckedChange(it) },
					onNoteClick = {
						viewModel.onNoteClick(it)
						onNavigateToSaveNote.invoke()
					}
				)
			}
		}
	)
}

@Composable
fun AddNoteButton(
	onNavigateToSaveNote: () -> Unit
) {
	FloatingActionButton(
		contentColor = MaterialTheme.colors.background,
		onClick = { onNavigateToSaveNote.invoke() },
		content = {
			Icon(
				imageVector = Icons.Filled.Add,
				contentDescription = "Add Save Note"
			)
		}
	)
}

@Preview
@Composable
fun AddNoteButtonPreview() {
	JetNotesTheme {
		AddNoteButton {
		}
	}
}

@Composable
private fun NotesList(
	modifier: Modifier = Modifier,
	notes: List<NoteModel>,
	onNoteCheckedChange: (NoteModel) -> Unit,
	onNoteClick: (NoteModel) -> Unit
) {
	LazyColumn(
		modifier = modifier
	) {
		items(count = notes.size) { noteIndex ->
			val note = notes[noteIndex]
			Note(
				note = note,
				onNoteClick = onNoteClick,
				onNoteCheckedChange = onNoteCheckedChange,
				isSelected = false
			)
		}
	}
}

@Preview
@Composable
private fun NotesListPreview() {
	NotesList(
		notes = listOf(
			NoteModel(1, "Note 1", "Content 1", null),
			NoteModel(2, "Note 2", "Content 2", false),
			NoteModel(3, "Note 3", "Content 3", true)
		),
		onNoteCheckedChange = {},
		onNoteClick = {}
	)
}
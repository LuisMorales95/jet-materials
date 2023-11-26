package com.yourcompany.android.jetnotes.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import com.yourcompany.android.jetnotes.domain.model.NoteModel
import com.yourcompany.android.jetnotes.theme.JetNotesTheme
import com.yourcompany.android.jetnotes.ui.components.Note
import com.yourcompany.android.jetnotes.ui.components.TopAppBar
import com.yourcompany.android.jetnotes.viewmodel.MainViewModel

@Composable
fun NotesScreen(
    viewModel: MainViewModel
) {
    val notes: List<NoteModel> by viewModel
        .notesNotInTrash
        .observeAsState(initial = listOf())
    Column {
        TopAppBar(
            title = "JetNotes",
            icon = Icons.Filled.List,
            onIconClick = {}
        )
        NotesList(
            notes = notes,
            onNoteClick = { viewModel.onNoteClick(it) },
            onNoteCheckChange = { viewModel.onNoteCheckedChange(it) }
        )
    }
}

@Composable
fun NotesList(
    notes: List<NoteModel>,
    onNoteClick: (NoteModel) -> Unit,
    onNoteCheckChange: (NoteModel) -> Unit
) {
    LazyColumn(content = {
        items(count = notes.size) { noteIndex ->
            val note = notes[noteIndex]
            Note(
                note = note,
                onNoteClick = onNoteClick,
                onNoteCheckedChange = onNoteCheckChange
            )
        }
    })
}

@Preview(showBackground = true)
@Composable
fun NotesListPreview() {
    JetNotesTheme {
        NotesList(
            notes = listOf(
                NoteModel(1, "Note 1", "Content 1", null),
                NoteModel(2, "Note 2", "Content 2", false),
                NoteModel(3, "Note 3", "Content 3", null)
            ),
            onNoteClick = {},
            onNoteCheckChange = {}
        )
    }
}
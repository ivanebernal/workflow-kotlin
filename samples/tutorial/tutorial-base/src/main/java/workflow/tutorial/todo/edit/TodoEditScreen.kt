package workflow.tutorial.todo.edit

data class TodoEditScreen(
  val title: String,
  val note: String,
  val onTitleChanged: (String) -> Unit,
  val onNoteChanged: (String) -> Unit,
  val discardChanges: () -> Unit,
  val saveChanges: () -> Unit
)

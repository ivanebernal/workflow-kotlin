package workflow.tutorial.todo.edit

import com.squareup.workflow1.Snapshot
import com.squareup.workflow1.StatefulWorkflow
import com.squareup.workflow1.action

import workflow.tutorial.todo.edit.TodoEditWorkflow.TodoEditProps
import workflow.tutorial.todo.edit.TodoEditWorkflow.TodoEditState
import workflow.tutorial.todo.edit.TodoEditWorkflow.TodoEditOutput
import workflow.tutorial.todo.edit.TodoEditWorkflow.TodoEditOutput.Discard
import workflow.tutorial.todo.edit.TodoEditWorkflow.TodoEditOutput.Save
import workflow.tutorial.todo.todolist.TodoListWorkflow.TodoModel

object TodoEditWorkflow : StatefulWorkflow<TodoEditProps, TodoEditState, TodoEditOutput, TodoEditScreen>() {

  data class TodoEditProps(
    val initialTodo: TodoModel
  )

  data class TodoEditState(
    val todo: TodoModel
  )

  sealed class TodoEditOutput {
    object Discard: TodoEditOutput()
    data class Save(val todo: TodoModel): TodoEditOutput()
  }

  override fun initialState(
    props: TodoEditProps,
    snapshot: Snapshot?
  ): TodoEditState = TodoEditState(props.initialTodo)

  override fun onPropsChanged(
    old: TodoEditProps,
    new: TodoEditProps,
    state: TodoEditState
  ): TodoEditState {
    if (old.initialTodo != new.initialTodo) {
      return state.copy(todo = new.initialTodo)
    }
    return state
  }

  private fun onTitleChanged(title: String) = action {
    state = state.withTitle(title)
  }

  private fun onNoteChanged(note: String) = action {
    state = state.withNote(note)
  }

  private fun onDiscard() = action {
    setOutput(Discard)
  }

  private fun onSave() = action {
    setOutput(Save(state.todo))
  }

  private fun TodoEditState.withTitle(title: String) = copy(todo = todo.copy(title = title))
  private fun TodoEditState.withNote(note: String) = copy(todo = todo.copy(note = note))

  override fun render(
    renderProps: TodoEditProps,
    renderState: TodoEditState,
    context: RenderContext
  ): TodoEditScreen = TodoEditScreen(
    title = renderState.todo.title,
    note = renderState.todo.note,
    onTitleChanged = { context.actionSink.send(onTitleChanged(it)) },
    onNoteChanged = { context.actionSink.send(onNoteChanged(it)) },
    discardChanges = { context.actionSink.send(onDiscard()) },
    saveChanges = { context.actionSink.send(onSave()) },
  )

  override fun snapshotState(state: TodoEditState): Snapshot? = null
}

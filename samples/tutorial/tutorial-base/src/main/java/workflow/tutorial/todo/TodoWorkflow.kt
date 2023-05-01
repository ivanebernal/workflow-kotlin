package workflow.tutorial.todo

import com.squareup.workflow1.Snapshot
import com.squareup.workflow1.StatefulWorkflow
import com.squareup.workflow1.action
import workflow.tutorial.todo.TodoWorkflow.Back
import workflow.tutorial.todo.TodoWorkflow.State
import workflow.tutorial.todo.TodoWorkflow.State.Step
import workflow.tutorial.todo.TodoWorkflow.State.Step.Edit
import workflow.tutorial.todo.TodoWorkflow.TodoProps
import workflow.tutorial.todo.edit.TodoEditWorkflow
import workflow.tutorial.todo.edit.TodoEditWorkflow.TodoEditOutput.Discard
import workflow.tutorial.todo.edit.TodoEditWorkflow.TodoEditOutput.Save
import workflow.tutorial.todo.edit.TodoEditWorkflow.TodoEditProps
import workflow.tutorial.todo.todolist.TodoListWorkflow
import workflow.tutorial.todo.todolist.TodoListWorkflow.ListProps
import workflow.tutorial.todo.todolist.TodoListWorkflow.Output
import workflow.tutorial.todo.todolist.TodoListWorkflow.TodoModel

object TodoWorkflow : StatefulWorkflow<TodoProps, State, Back, List<Any>>() {

  data class TodoProps(val username: String)

  data class State(
    val todos: List<TodoModel>,
    val step: Step,
  ) {
    sealed class Step {
      object List: Step()
      data class Edit(val index: Int): Step()
    }
  }

  object Back

  override fun initialState(
    props: TodoProps,
    snapshot: Snapshot?
  ): State = State(
    listOf(
      TodoModel(
        title = "Do german homework",
        note = "I really need to get better at german, that's why " +
          "I need to be more responsible with my homework"
      )
    ),
    Step.List
  )

  override fun render(
    renderProps: TodoProps,
    renderState: State,
    context: RenderContext
  ): List<Any> {
    val todoScreen = context.renderChild(
      TodoListWorkflow,
      props = ListProps(
        username = renderProps.username,
        todos = renderState.todos
      )
    ) { output ->
      when(output) {
        Output.Back -> onBack()
        is Output.SelectTodo -> editTodo(output.index)
      }
    }
    return when (val step = renderState.step) {
      Step.List -> listOf(todoScreen)
      is Edit -> {
        val editScreen = context.renderChild(
          TodoEditWorkflow, TodoEditProps(initialTodo = renderState.todos[step.index])
        ) { output ->
          when(output) {
            Discard -> discardChanges()
            is Save -> saveChanges(output.todo, step.index)
          }
        }
        listOf(todoScreen, editScreen)
      }
    }
  }

  private fun discardChanges() = action {
    state = state.copy(step = Step.List)
  }

  private fun saveChanges(
    todo: TodoModel,
    index: Int,
  ) = action {
    state = state.copy(
      todos = state.todos.toMutableList().also { it[index] = todo },
      step = Step.List
    )
  }

  private fun editTodo(index: Int) = action {
    state = state.copy(step = Step.Edit(index))
  }

  private fun onBack() = action {
    setOutput(Back)
  }

  override fun snapshotState(state: State): Snapshot? = Snapshot.write {
    TODO("Save state")
  }
}

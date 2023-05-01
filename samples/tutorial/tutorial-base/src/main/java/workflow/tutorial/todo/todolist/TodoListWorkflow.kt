package workflow.tutorial.todo.todolist

import com.squareup.workflow1.Snapshot
import com.squareup.workflow1.StatefulWorkflow
import com.squareup.workflow1.StatelessWorkflow
import com.squareup.workflow1.action
import workflow.tutorial.todo.todolist.TodoListWorkflow.ListProps
import workflow.tutorial.todo.todolist.TodoListWorkflow.Output
import workflow.tutorial.todo.todolist.TodoListWorkflow.Output.Back
import workflow.tutorial.todo.todolist.TodoListWorkflow.Output.SelectTodo

object TodoListWorkflow : StatelessWorkflow<ListProps, Output, TodoListScreen>() {


  data class TodoModel(
    val title: String,
    val note: String,
  )

  data class ListProps(
    val username: String,
    val todos: List<TodoModel>
  )

  sealed class Output {
    object Back: Output()
    data class SelectTodo(val index: Int): Output()
  }

  override fun render(
    renderProps: ListProps,
    context: RenderContext
  ): TodoListScreen {
    val titles = renderProps.todos.map { it.title }
    return TodoListScreen(
      username = renderProps.username,
      todoTiles = titles,
      onTodoSelected = { context.actionSink.send(selectTodo(it)) },
      onBack = { context.actionSink.send(onBack()) },
    )
  }

  private fun selectTodo(index: Int) = action {
    setOutput(SelectTodo(index))
  }

  private fun onBack() = action {
    setOutput(Back)
  }
}

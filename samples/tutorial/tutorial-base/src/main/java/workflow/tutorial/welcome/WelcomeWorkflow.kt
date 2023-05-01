package workflow.tutorial.welcome

import com.squareup.workflow1.Snapshot
import com.squareup.workflow1.StatefulWorkflow
import com.squareup.workflow1.action
import workflow.tutorial.welcome.WelcomeWorkflow.LoggedIn
import workflow.tutorial.welcome.WelcomeWorkflow.State

object WelcomeWorkflow : StatefulWorkflow<Unit, State, LoggedIn, WelcomeScreen>() {

  data class State(
    val username: String
  )

  data class LoggedIn(val username: String)

  override fun initialState(
    props: Unit,
    snapshot: Snapshot?
  ): State = State(username = "")

  override fun render(
    renderProps: Unit,
    renderState: State,
    context: RenderContext
  ): WelcomeScreen = WelcomeScreen(
    username = renderState.username,
    onUsernameChanged = { context.actionSink.send(onUsernameChanged(it)) },
    onLoginTapped = { context.actionSink.send(onLogin()) },
  )

  override fun snapshotState(state: State): Snapshot? = null

  private fun onUsernameChanged(username: String) = action {
    state = state.copy(username = username)
  }

  private fun onLogin() = action {
    setOutput(LoggedIn(state.username))
  }
}

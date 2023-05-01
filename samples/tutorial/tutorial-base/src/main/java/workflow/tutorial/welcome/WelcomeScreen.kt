package workflow.tutorial.welcome

data class WelcomeScreen(
  val username: String,
  val onUsernameChanged: (String) -> Unit,
  val onLoginTapped: () -> Unit
)

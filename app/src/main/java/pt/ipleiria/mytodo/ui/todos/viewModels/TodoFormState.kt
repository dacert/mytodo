package pt.ipleiria.mytodo.ui.todos.viewModels

/**
 * Data validation state of the login form.
 */
data class TodoFormState(
    val textError: Int? = null,
    val isDataValid: Boolean = false
)
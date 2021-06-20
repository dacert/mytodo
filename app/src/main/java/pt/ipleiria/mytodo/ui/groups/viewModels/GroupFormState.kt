package pt.ipleiria.mytodo.ui.groups.viewModels

/**
 * Data validation state of the login form.
 */
data class GroupFormState(
    val nameError: Int? = null,
    val membersError: Int? = null,
    val isDataValid: Boolean = false
)
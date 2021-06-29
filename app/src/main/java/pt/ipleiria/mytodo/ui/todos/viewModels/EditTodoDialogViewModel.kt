package pt.ipleiria.mytodo.ui.todos.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import pt.ipleiria.mytodo.R
import pt.ipleiria.mytodo.base.BaseViewModel
import pt.ipleiria.mytodo.shared.SharedUser
import pt.ipleiria.mytodo.dataLayer.repositories.TodosRepository.create
import pt.ipleiria.mytodo.dataLayer.repositories.TodosRepository.delete
import pt.ipleiria.mytodo.dataLayer.repositories.TodosRepository.update

class EditTodoDialogViewModel : BaseViewModel() {
    private val _form = MutableLiveData<TodoFormState>()
    val formState: LiveData<TodoFormState> = _form

    fun add(groupId: String, text: String, onComplete: (isSuccess: Boolean) -> Unit) {
        dataLoading.value = true
        val data = hashMapOf(
            "text" to text,
            "by" to SharedUser.email,
            "timestamp" to FieldValue.serverTimestamp()
        )

        create(groupId, data) { isSuccess: Boolean, error: String ->
            dataLoading.value = false
            toastMessage.value = if (!isSuccess) error else "Success"
            onComplete(isSuccess)
        }
    }

    fun edit(groupId: String, id: String, text: String, onComplete: (isSuccess: Boolean) -> Unit) {
        dataLoading.value = true
        val data = hashMapOf(
            "text" to text,
            "timestamp" to FieldValue.serverTimestamp()
        )

        update(groupId, id, data) { isSuccess: Boolean, error: String ->
            dataLoading.value = false
            toastMessage.value = if (!isSuccess) error else "Success"
            onComplete(isSuccess)
        }
    }

    fun remove(groupId: String, id: String, onComplete: (isSuccess: Boolean) -> Unit) {
        dataLoading.value = true
        delete(groupId, id) { isSuccess: Boolean, error: String ->
            dataLoading.value = false
            toastMessage.value = if (!isSuccess) error else "Success"
            onComplete(isSuccess)
        }
    }

    fun dataChanged(name: String) {
        if (name.isNullOrEmpty()) {
            _form.value = TodoFormState(textError = R.string.invalid_text)
        } else {
            _form.value = TodoFormState(isDataValid = true)
        }
    }
}
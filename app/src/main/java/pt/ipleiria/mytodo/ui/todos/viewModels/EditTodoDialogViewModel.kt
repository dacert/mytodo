package pt.ipleiria.mytodo.ui.todos.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import pt.ipleiria.mytodo.R
import pt.ipleiria.mytodo.base.BaseViewModel
import pt.ipleiria.mytodo.shared.SharedFireBase
import pt.ipleiria.mytodo.shared.SharedUser

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

    //helper create
    private fun create(groupId: String, data: HashMap<String, Any>, onComplete: (isSuccess: Boolean, error: String) -> Unit){
        val db = SharedFireBase.store
        db.collection("groups/${groupId}/todos").add(data).addOnCompleteListener { addTask  ->
            onComplete(addTask.isSuccessful, addTask.exception?.message ?: "")
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
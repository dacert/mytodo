package pt.ipleiria.mytodo.ui.todos.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import pt.ipleiria.mytodo.R
import pt.ipleiria.mytodo.base.BaseViewModel
import pt.ipleiria.mytodo.models.Group
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

    //helper update
    private fun update(groupId: String, id: String, data: HashMap<String, Any>, onComplete: (isSuccess: Boolean, error: String) -> Unit){
        val db = SharedFireBase.store
        db.collection("groups/${groupId}/todos").document(id).update(data).addOnCompleteListener { editTask  ->
            onComplete(editTask.isSuccessful, editTask.exception?.message ?: "")
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
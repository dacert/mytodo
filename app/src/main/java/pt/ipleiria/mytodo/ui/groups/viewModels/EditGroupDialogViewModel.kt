package pt.ipleiria.mytodo.ui.groups.viewModels

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pt.ipleiria.mytodo.R
import pt.ipleiria.mytodo.base.BaseViewModel
import pt.ipleiria.mytodo.models.Group
import pt.ipleiria.mytodo.shared.SharedFireBase
import pt.ipleiria.mytodo.shared.SharedUser

class EditGroupDialogViewModel : BaseViewModel() {
    private val _form = MutableLiveData<GroupFormState>()
    val formState: LiveData<GroupFormState> = _form

    fun add(name: String, members: String, onComplete: (isSuccess: Boolean) -> Unit) {
        dataLoading.value = true
        val data = hashMapOf(
            "name" to name,
            "members" to members.split(";").map { m -> m.trim() }.filter { m -> m.isNotEmpty() },
            "owner" to SharedUser.email
        )

        any(name) { isAny: Boolean, error: String ->
            if (!isAny && error.isEmpty()) {
                create(data) { isSuccess: Boolean, error: String ->
                    dataLoading.value = false
                    toastMessage.value = if (!isSuccess) error else "Success"
                    onComplete(isSuccess)
                }
            } else {
                dataLoading.value = false
                toastMessage.value = if (error.isNotEmpty()) error else "Name is duplicated"
                onComplete(false)
            }
        }
    }

    fun edit(oldValue: Group, newName: String, newMembers: String, onComplete: (isSuccess: Boolean) -> Unit) {
        dataLoading.value = true
        val data = hashMapOf(
            "name" to newName,
            "members" to newMembers.split(";").map { m -> m.trim() }.filter { m -> m.isNotEmpty() }
        )

        if(oldValue.name != newName) {
            any(newName) { isAny: Boolean, error: String ->
                if (!isAny && error.isEmpty()) {
                    update(oldValue.id, data) { isSuccess: Boolean, error: String ->
                        dataLoading.value = false
                        toastMessage.value = if (!isSuccess) error else "Success"
                        onComplete(isSuccess)
                    }
                } else {
                    dataLoading.value = false
                    toastMessage.value = if (error.isNotEmpty()) error else "Name is duplicated"
                    onComplete(false)
                }
            }
        } else {
            update(oldValue.id, data) { isSuccess: Boolean, error: String ->
                dataLoading.value = false
                toastMessage.value = error
                onComplete(isSuccess)
            }
        }
    }

    fun delete(id: String, onComplete: (isSuccess: Boolean) -> Unit) {
        dataLoading.value = true
        val db = SharedFireBase.store

        db.collection("groups").document(id).delete()
            .addOnCompleteListener { searchTask  ->
                dataLoading.value = false
                toastMessage.value = searchTask.exception?.message ?: "Success"
                onComplete(searchTask.isSuccessful)
            }
    }

    //helper update
    private fun update(id: String, data: HashMap<String, Any>, onComplete: (isSuccess: Boolean, error: String) -> Unit){
        val db = SharedFireBase.store
        db.collection("groups").document(id).update(data).addOnCompleteListener { editTask  ->
            onComplete(editTask.isSuccessful, editTask.exception?.message ?: "")
        }
    }

    //helper create
    private fun create(data: HashMap<String, Any>, onComplete: (isSuccess: Boolean, error: String) -> Unit){
        val db = SharedFireBase.store
        db.collection("groups").add(data).addOnCompleteListener { addTask  ->
            onComplete(addTask.isSuccessful, addTask.exception?.message ?: "")
        }
    }

    //helper any
    private fun any(name: String, onComplete: (isSuccess: Boolean, error: String) -> Unit){
        val db = SharedFireBase.store
        db.collection("groups")
            .whereEqualTo("name",  name).get()
            .addOnCompleteListener { searchTask  ->
                onComplete(searchTask.isSuccessful && !searchTask.result!!.isEmpty, searchTask.exception?.message ?: "")
            }
    }

    fun dataChanged(name: String, members: String) {
        if (!isNameValid(name)) {
            _form.value = GroupFormState(nameError = R.string.invalid_name)
        } else if (!isMembersValid(members)) {
            _form.value = GroupFormState(membersError = R.string.invalid_members)
        } else {
            _form.value = GroupFormState(isDataValid = true)
        }
    }

    // A placeholder name validation check
    private fun isNameValid(name: String): Boolean {
        return name.matches(Regex("^[a-zA-Z0-9]+\$"))
    }

    // A placeholder members validation check
    private fun isMembersValid(members: String): Boolean {
        for (email in members.split(";")){
            if(email.trim().isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches())
                return false
        }
        return true
    }
}
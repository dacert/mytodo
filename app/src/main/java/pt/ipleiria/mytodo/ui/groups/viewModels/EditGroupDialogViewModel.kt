package pt.ipleiria.mytodo.ui.groups.viewModels

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pt.ipleiria.mytodo.R
import pt.ipleiria.mytodo.base.BaseViewModel
import pt.ipleiria.mytodo.shared.SharedFireBase
import pt.ipleiria.mytodo.shared.SharedUser

class EditGroupDialogViewModel : BaseViewModel() {
    private val _form = MutableLiveData<GroupFormState>()
    val formState: LiveData<GroupFormState> = _form

    fun add(name: String, members: String, onComplete: (isSuccess: Boolean, error: String) -> Unit) {
        dataLoading.value = true
        val db = SharedFireBase.store

        db.collection("groups")
            .whereEqualTo("name",  name).get()
            .addOnCompleteListener { searchTask  ->
                if (searchTask.isSuccessful) {
                    if (searchTask.result!!.isEmpty) {
                        val data = hashMapOf(
                            "name" to name,
                            "members" to members.split(";").map { m -> m.trim() }.filter { m -> m.isNotEmpty() },
                            "owner" to SharedUser.email
                        )
                        db.collection("groups").add(data).addOnCompleteListener { addTask  ->
                            if(addTask.isSuccessful){
                                dataLoading.value = false
                            }
                            onComplete(addTask.isSuccessful, addTask.exception?.message ?: "")
                        }
                    } else {
                        onComplete(false, "Name is duplicated")
                    }
                } else {
                    onComplete(searchTask.isSuccessful, searchTask.exception?.message ?: "")
                }
            }
    }

    fun delete(id: String, onComplete: (isSuccess: Boolean, error: String) -> Unit) {
        dataLoading.value = true
        val db = SharedFireBase.store

        db.collection("groups").document(id).delete()
            .addOnCompleteListener { searchTask  ->
                dataLoading.value = false
                onComplete(searchTask.isSuccessful, searchTask.exception?.message ?: "")
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
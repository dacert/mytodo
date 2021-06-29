package pt.ipleiria.mytodo.ui.groups.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import pt.ipleiria.mytodo.R
import pt.ipleiria.mytodo.base.BaseViewModel
import pt.ipleiria.mytodo.dataLayer.models.Group
import pt.ipleiria.mytodo.dataLayer.repositories.GroupsRepository.any
import pt.ipleiria.mytodo.dataLayer.repositories.GroupsRepository.create
import pt.ipleiria.mytodo.dataLayer.repositories.GroupsRepository.delete
import pt.ipleiria.mytodo.dataLayer.repositories.GroupsRepository.update
import pt.ipleiria.mytodo.shared.SharedUser

class EditGroupDialogViewModel : BaseViewModel() {
    private val _form = MutableLiveData<GroupFormState>()
    val formState: LiveData<GroupFormState> = _form

    fun add(name: String, members: String, onComplete: (isSuccess: Boolean) -> Unit) {
        dataLoading.value = true
        val data = hashMapOf(
            "name" to name,
            "members" to members.split(";").map { m -> m.trim() }.plus(SharedUser.email).filter { m -> m.isNotEmpty() },
            "owner" to SharedUser.email,
            "timestamp" to FieldValue.serverTimestamp()
        )

        any(name) { isAny: Boolean, error: String ->
            if (!isAny && error.isEmpty()) {
                create(data) { isSuccess: Boolean, createError: String ->
                    dataLoading.value = false
                    toastMessage.value = if (!isSuccess) createError else "Success"
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
            "members" to newMembers.split(";").map { m -> m.trim() }.plus(SharedUser.email).filter { m -> m.isNotEmpty() },
            "timestamp" to FieldValue.serverTimestamp()
        )

        if(oldValue.name != newName) {
            any(newName) { isAny: Boolean, error: String ->
                if (!isAny && error.isEmpty()) {
                    update(oldValue.id, data) { isSuccess: Boolean, updateError: String ->
                        dataLoading.value = false
                        toastMessage.value = if (!isSuccess) updateError else "Success"
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
                toastMessage.value = if (!isSuccess) error else "Success"
                onComplete(isSuccess)
            }
        }
    }

    fun remove(id: String, onComplete: (isSuccess: Boolean) -> Unit) {
        dataLoading.value = true
        delete(id) { isSuccess: Boolean, error: String ->
            dataLoading.value = false
            toastMessage.value = if (!isSuccess) error else "Success"
            onComplete(isSuccess)
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
        val emailPattern = Regex(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
        for (email in members.split(";")){
            if(email.trim().isNotEmpty() && !email.trim().matches(emailPattern))
                return false
        }
        return true
    }
}
package pt.ipleiria.mytodo.ui.groups.viewModels

import androidx.lifecycle.MutableLiveData
import pt.ipleiria.mytodo.base.BaseViewModel
import pt.ipleiria.mytodo.dataLayer.models.Group
import pt.ipleiria.mytodo.dataLayer.repositories.GroupsRepository.fetch

class GroupsViewModel : BaseViewModel() {
    val listLive = MutableLiveData<List<Group>>()

    fun fetchList() {
        dataLoading.value = true
        fetch { isSuccess, result  ->
            if(isSuccess){
                listLive.value = result
            }
            dataLoading.value = false
            empty.value = (listLive.value?.size ?: 0) == 0
        }
    }
}
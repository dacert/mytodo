package pt.ipleiria.mytodo.ui.groups.viewModels

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import pt.ipleiria.mytodo.shared.SharedUser
import pt.ipleiria.mytodo.base.BaseViewModel
import pt.ipleiria.mytodo.models.Group
import pt.ipleiria.mytodo.shared.SharedFireBase

class GroupsViewModel : BaseViewModel() {
    val listLive = MutableLiveData<List<Group>>()

    private val toGroupObject: (o: QueryDocumentSnapshot) -> Group = { o ->
        o.toObject(Group::class.java).apply {
            id = o.id;
            members = members.filter { e -> e != SharedUser.email }
        }
    }

    fun fetchList() {
        dataLoading.value = true
        val db = SharedFireBase.store

        db.collection("groups")
            .whereArrayContains("members",  SharedUser.email)
            .orderBy("timestamp", Query.Direction.DESCENDING).get()
            .addOnCompleteListener { task1  ->
                if(task1.isSuccessful){
                    listLive.value = task1.result!!.map(toGroupObject)
                }
                dataLoading.value = false
                empty.value = (listLive.value?.size ?: 0) == 0
            }
    }
}
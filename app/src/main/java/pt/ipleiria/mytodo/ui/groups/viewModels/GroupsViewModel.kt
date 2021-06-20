package pt.ipleiria.mytodo.ui.groups.viewModels

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.QueryDocumentSnapshot
import pt.ipleiria.mytodo.shared.SharedUser
import pt.ipleiria.mytodo.base.BaseViewModel
import pt.ipleiria.mytodo.models.Group
import pt.ipleiria.mytodo.shared.SharedFireBase

class GroupsViewModel : BaseViewModel() {
    val listLive = MutableLiveData<List<Group>>()

    private val toGroupObject: (o: QueryDocumentSnapshot) -> Group = { o ->
        o.toObject(Group::class.java).apply { id = o.id }
    }

    fun fetchList() {
        dataLoading.value = true
        val db = SharedFireBase.store

        db.collection("groups")
                .whereEqualTo("owner",  SharedUser.email).get()
                .addOnCompleteListener { task1  ->
                    val temp:  MutableList<Group> = mutableListOf()
                    if(task1.isSuccessful){
                        temp.addAll(task1.result!!.map(toGroupObject))
                    }

                    db.collection("groups")
                            .whereArrayContains("members",  SharedUser.email).get()
                            .addOnCompleteListener { task2 ->
                                dataLoading.value = false
                                if(task2.isSuccessful){
                                    temp.addAll(task2.result!!.map(toGroupObject))
                                }
                                empty.value = temp.size < 0
                                listLive.value = temp
                    }
            }
    }
}
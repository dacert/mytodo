package pt.ipleiria.mytodo.ui.groups

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import pt.ipleiria.mytodo.shared.SharedUser
import pt.ipleiria.mytodo.base.BaseViewModel
import pt.ipleiria.mytodo.models.Group

class GroupsViewModel : BaseViewModel() {
    val listLive = MutableLiveData<List<Group>>()

    private val toGroupObject: (o: QueryDocumentSnapshot) -> Group = { o ->
        o.toObject(Group::class.java).apply { key = "groups/${o.id}" }
    }

    fun fetchList() {
        dataLoading.value = true
        val db = FirebaseFirestore.getInstance()

        db.collection("groups")
                .whereEqualTo("owner",  SharedUser.email).get()
                .addOnCompleteListener { task  ->
                    val temp:  MutableList<Group> = mutableListOf()
                    if(task.isSuccessful){
                        temp.addAll(task.result!!.map(toGroupObject))
                    }

                    db.collection("groups")
                            .whereArrayContains("members",  SharedUser.email).get()
                            .addOnCompleteListener { task ->
                                dataLoading.value = false
                                if(task.isSuccessful){
                                    temp.addAll(task.result!!.map(toGroupObject))
                                }
                                empty.value = temp.size < 0
                                listLive.value = temp
                    }
            }
    }
}
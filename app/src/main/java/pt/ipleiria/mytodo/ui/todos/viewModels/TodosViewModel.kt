package pt.ipleiria.mytodo.ui.todos.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import pt.ipleiria.mytodo.shared.SharedUser
import pt.ipleiria.mytodo.base.BaseViewModel
import pt.ipleiria.mytodo.models.Group
import pt.ipleiria.mytodo.models.Todo
import pt.ipleiria.mytodo.shared.SharedFireBase

class TodosViewModel : BaseViewModel() {
    val listLive = MutableLiveData<List<Todo>>()
    private lateinit var registration: ListenerRegistration

    private val toTodoObject: (DocumentSnapshot) -> Todo = { o ->
        o.toObject(Todo::class.java)?.apply { id = o.id }!!
    }

    fun fetchList(group: String) {
        dataLoading.value = true
        val db = SharedFireBase.store

        val query = db.collection("groups/${group}/todos")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        registration = query.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w("TodoViewModel", "listen:error", e)
                return@addSnapshotListener
            }
            dataLoading.value = false
            listLive.value = snapshots!!.documents.map(toTodoObject)
            empty.value = (listLive.value?.size ?: 0) == 0
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Stop listening to changes
        registration.remove()
    }
}
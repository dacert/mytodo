package pt.ipleiria.mytodo.dataLayer.repositories

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import pt.ipleiria.mytodo.dataLayer.models.Group
import pt.ipleiria.mytodo.shared.SharedFireBase
import pt.ipleiria.mytodo.shared.SharedUser

object GroupsRepository {

    private val toGroupObject: (o: QueryDocumentSnapshot) -> Group = { o ->
        o.toObject(Group::class.java).apply {
            id = o.id;
            members = members.filter { e -> e != SharedUser.email }
        }
    }

    fun fetch(onComplete: (isSuccess: Boolean, result: List<Group>?) -> Unit) {
        val db = SharedFireBase.store
        db.collection("groups")
            .whereArrayContains("members",  SharedUser.email)
            .orderBy("timestamp", Query.Direction.DESCENDING).get()
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful, task.result?.map(toGroupObject))
            }
    }

    //helper update
    fun update(id: String, data: HashMap<String, Any>, onComplete: (isSuccess: Boolean, error: String) -> Unit){
        val db = SharedFireBase.store
        db.collection("groups").document(id).update(data).addOnCompleteListener { editTask  ->
            onComplete(editTask.isSuccessful, editTask.exception?.message ?: "")
        }
    }

    //helper create
    fun create(data: HashMap<String, Any>, onComplete: (isSuccess: Boolean, error: String) -> Unit){
        val db = SharedFireBase.store
        db.collection("groups").add(data).addOnCompleteListener { addTask  ->
            onComplete(addTask.isSuccessful, addTask.exception?.message ?: "")
        }
    }

    //helper any
    fun any(name: String, onComplete: (isSuccess: Boolean, error: String) -> Unit){
        val db = SharedFireBase.store
        db.collection("groups")
            .whereEqualTo("name",  name).get()
            .addOnCompleteListener { searchTask  ->
                onComplete(searchTask.isSuccessful && !searchTask.result!!.isEmpty, searchTask.exception?.message ?: "")
            }
    }

    fun delete(id: String, onComplete: (isSuccess: Boolean, error: String) -> Unit) {
        val db = SharedFireBase.store
        db.collection("groups").document(id).delete()
            .addOnCompleteListener { deleteTask  ->
                onComplete(deleteTask.isSuccessful, deleteTask.exception?.message ?: "")
            }
    }
}
package pt.ipleiria.mytodo.dataLayer.repositories

import pt.ipleiria.mytodo.shared.SharedFireBase

object TodosRepository {

    //helper update
    fun update(groupId: String, id: String, data: HashMap<String, Any>, onComplete: (isSuccess: Boolean, error: String) -> Unit){
        val db = SharedFireBase.store
        db.collection("groups/${groupId}/todos").document(id).update(data).addOnCompleteListener { editTask  ->
            onComplete(editTask.isSuccessful, editTask.exception?.message ?: "")
        }
    }

    //helper create
    fun create(groupId: String, data: HashMap<String, Any>, onComplete: (isSuccess: Boolean, error: String) -> Unit){
        val db = SharedFireBase.store
        db.collection("groups/${groupId}/todos").add(data).addOnCompleteListener { addTask  ->
            onComplete(addTask.isSuccessful, addTask.exception?.message ?: "")
        }
    }

    fun delete(groupId: String, id: String, onComplete: (isSuccess: Boolean, error: String) -> Unit) {
        val db = SharedFireBase.store
        db.collection("groups/${groupId}/todos").document(id).delete()
            .addOnCompleteListener { deleteTask  ->
                onComplete(deleteTask.isSuccessful, deleteTask.exception?.message ?: "")
            }
    }
}
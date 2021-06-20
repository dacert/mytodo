package pt.ipleiria.mytodo.shared

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object SharedFireBase {
    val store = Firebase.firestore
    val auth = Firebase.auth
}
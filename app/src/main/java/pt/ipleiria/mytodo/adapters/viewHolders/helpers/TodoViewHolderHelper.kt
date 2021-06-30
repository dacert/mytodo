package pt.ipleiria.mytodo.adapters.viewHolders.helpers

import pt.ipleiria.mytodo.dataLayer.models.Todo
import pt.ipleiria.mytodo.shared.SharedUser

object TodoViewHolderHelper {
    fun isByCurrentUser(data: Todo): Boolean {
        return data.by == SharedUser.email
    }
}
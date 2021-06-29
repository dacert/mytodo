package pt.ipleiria.mytodo

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import pt.ipleiria.mytodo.adapters.viewHolders.helpers.TodoViewHolderHelper.isByCurrentUser
import pt.ipleiria.mytodo.dataLayer.models.Todo
import pt.ipleiria.mytodo.shared.SharedUser

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TodoViewHolderHelperUnitTest {
    @Before
    fun init(){
        SharedUser.email = "user@email.com"
    }

    @Test
    fun byLoggedUser_isInCorrect() {
        val r = isByCurrentUser(Todo())
        assertFalse(r)
    }

    @Test
    fun byLoggedUser_isCorrect() {
        val r = isByCurrentUser(Todo(by = "user@email.com"))
        assert(r)
    }
}
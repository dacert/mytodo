package pt.ipleiria.mytodo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.every
import io.mockk.invoke
import io.mockk.mockkObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import pt.ipleiria.mytodo.shared.SharedUser
import pt.ipleiria.mytodo.ui.todos.viewModels.EditTodoDialogViewModel
import pt.ipleiria.mytodo.dataLayer.repositories.TodosRepository

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class EditTodoDialogViewModelUnitTest {
    lateinit var viewModel: EditTodoDialogViewModel

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun init(){
        SharedUser.email = "user@email.com"
        mockkObject(TodosRepository)
        viewModel = EditTodoDialogViewModel()
    }

    @Test
    fun edit_isSuccess() {
        every { TodosRepository.update(any(), any(), any(), captureLambda()) } answers {
            lambda<(isSuccess: Boolean, error: String) -> Unit>().invoke(true, "")
        }
        viewModel.edit("1", "2", "text"){ isSuccess: Boolean ->
            assertEquals(viewModel.toastMessage.value, "Success")
            assert(isSuccess)
        }
    }

    @Test
    fun add_isSuccess() {
        every { TodosRepository.create(any(), any(), captureLambda()) } answers {
            lambda<(isSuccess: Boolean, error: String) -> Unit>().invoke(true, "")
        }
        viewModel.add("1", "text"){ isSuccess: Boolean ->
            assertEquals(viewModel.toastMessage.value, "Success")
            assert(isSuccess)
        }
    }

    @Test
    fun remove_isSuccess() {
        every { TodosRepository.delete(any(), any(), captureLambda()) } answers {
            lambda<(isSuccess: Boolean, error: String) -> Unit>().invoke(true, "")
        }
        viewModel.remove("1", "2"){ isSuccess: Boolean ->
            assertEquals(viewModel.toastMessage.value, "Success")
            assert(isSuccess)
        }
    }

    @Test
    fun edit_isNotSuccess() {
        every { TodosRepository.update(any(), any(), any(), captureLambda()) } answers {
            lambda<(isSuccess: Boolean, error: String) -> Unit>().invoke(false, "error")
        }
        viewModel.edit("1", "2", "text"){ isSuccess: Boolean ->
            assertEquals(viewModel.toastMessage.value, "error")
            assertFalse(isSuccess)
        }
    }

    @Test
    fun add_isNotSuccess() {
        every { TodosRepository.create(any(), any(), captureLambda()) } answers {
            lambda<(isSuccess: Boolean, error: String) -> Unit>().invoke(false, "error")
        }
        viewModel.add("1", "text"){ isSuccess: Boolean ->
            assertEquals(viewModel.toastMessage.value, "error")
            assertFalse(isSuccess)
        }
    }

    @Test
    fun remove_isNotSuccess() {
        every { TodosRepository.delete(any(), any(), captureLambda()) } answers {
            lambda<(isSuccess: Boolean, error: String) -> Unit>().invoke(false, "error")
        }
        viewModel.remove("1", "2"){ isSuccess: Boolean ->
            assertEquals(viewModel.toastMessage.value, "error")
            assertFalse(isSuccess)
        }
    }

    @Test
    fun dataChanged_name_isValid() {
        viewModel.dataChanged("name")
        assertNull(viewModel.formState.value!!.textError)
        assert(viewModel.formState.value!!.isDataValid)
    }

    @Test
    fun dataChanged_name_isInvalid() {
        viewModel.dataChanged("")
        assertNotNull(viewModel.formState.value!!.textError)
        assertFalse(viewModel.formState.value!!.isDataValid)
    }
}
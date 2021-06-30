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
import pt.ipleiria.mytodo.dataLayer.models.Group
import pt.ipleiria.mytodo.dataLayer.repositories.GroupsRepository
import pt.ipleiria.mytodo.shared.SharedUser
import pt.ipleiria.mytodo.ui.groups.viewModels.EditGroupDialogViewModel

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class EditGroupDialogViewModelUnitTest {
    lateinit var viewModel: EditGroupDialogViewModel

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun init(){
        SharedUser.email = "user@email.com"
        mockkObject(GroupsRepository)
        every { GroupsRepository.any(any(), captureLambda()) } answers {
            lambda<(isSuccess: Boolean, error: String) -> Unit>().invoke(false, "")
        }
        viewModel = EditGroupDialogViewModel()
    }

    @Test
    fun edit_isSuccess() {
        every { GroupsRepository.update(any(), any(), captureLambda()) } answers {
            lambda<(isSuccess: Boolean, error: String) -> Unit>().invoke(true, "")
        }
        viewModel.edit(Group(name = "name"),"newName", "user1@email.com;user2@email.com"){ isSuccess: Boolean ->
            assertEquals(viewModel.toastMessage.value, "Success")
            assert(isSuccess)
        }
    }

    @Test
    fun add_isSuccess() {
        every { GroupsRepository.create(any(), captureLambda()) } answers {
            lambda<(isSuccess: Boolean, error: String) -> Unit>().invoke(true, "")
        }
        viewModel.add("name", "user1@email.com;user2@email.com"){ isSuccess: Boolean ->
            assertEquals(viewModel.toastMessage.value, "Success")
            assert(isSuccess)
        }
    }

    @Test
    fun remove_isSuccess() {
        every { GroupsRepository.delete(any(), captureLambda()) } answers {
            lambda<(isSuccess: Boolean, error: String) -> Unit>().invoke(true, "")
        }
        viewModel.remove("1"){ isSuccess: Boolean ->
            assertEquals(viewModel.toastMessage.value, "Success")
            assert(isSuccess)
        }
    }

    @Test
    fun edit_isNotSuccess() {
        every { GroupsRepository.update(any(), any(), captureLambda()) } answers {
            lambda<(isSuccess: Boolean, error: String) -> Unit>().invoke(false, "error")
        }
        viewModel.edit(Group(name = "name"),"newName", "user1@email.com;user2@email.com"){ isSuccess: Boolean ->
            assertEquals(viewModel.toastMessage.value, "error")
            assertFalse(isSuccess)
        }
    }

    @Test
    fun edit_isDuplicate() {
        every { GroupsRepository.update(any(), any(), captureLambda()) } answers {
            lambda<(isSuccess: Boolean, error: String) -> Unit>().invoke(false, "error")
        }
        every { GroupsRepository.any(any(), captureLambda()) } answers {
            lambda<(isSuccess: Boolean, error: String) -> Unit>().invoke(true, "")
        }
        viewModel.edit(Group(name = "name"),"newName", "user1@email.com;user2@email.com"){ isSuccess: Boolean ->
            assertEquals(viewModel.toastMessage.value, "Name is duplicated")
            assertFalse(isSuccess)
        }
    }

    @Test
    fun add_isNotSuccess() {
        every { GroupsRepository.create(any(), captureLambda()) } answers {
            lambda<(isSuccess: Boolean, error: String) -> Unit>().invoke(false, "error")
        }
        viewModel.add("name", "user1@email.com;user2@email.com"){ isSuccess: Boolean ->
            assertEquals(viewModel.toastMessage.value, "error")
            assertFalse(isSuccess)
        }
    }

    @Test
    fun add_isDuplicate() {
        every { GroupsRepository.create(any(), captureLambda()) } answers {
            lambda<(isSuccess: Boolean, error: String) -> Unit>().invoke(false, "error")
        }
        every { GroupsRepository.any(any(), captureLambda()) } answers {
            lambda<(isSuccess: Boolean, error: String) -> Unit>().invoke(true, "")
        }
        viewModel.add("name", "user1@email.com;user2@email.com"){ isSuccess: Boolean ->
            assertEquals(viewModel.toastMessage.value, "Name is duplicated")
            assertFalse(isSuccess)
        }
    }

    @Test
    fun remove_isNotSuccess() {
        every { GroupsRepository.delete(any(), captureLambda()) } answers {
            lambda<(isSuccess: Boolean, error: String) -> Unit>().invoke(false, "error")
        }
        viewModel.remove("1"){ isSuccess: Boolean ->
            assertEquals(viewModel.toastMessage.value, "error")
            assertFalse(isSuccess)
        }
    }

    @Test
    fun dataChanged_name_isValid() {
        viewModel.dataChanged("name", "user1@email.com")
        assertNull(viewModel.formState.value!!.nameError)
        assert(viewModel.formState.value!!.isDataValid)
    }

    @Test
    fun dataChanged_name_isInvalid() {
        viewModel.dataChanged("", "user1@email.com")
        assertNotNull(viewModel.formState.value!!.nameError)
        assertFalse(viewModel.formState.value!!.isDataValid)
    }

    @Test
    fun dataChanged_members_isValid() {
        viewModel.dataChanged("name", "user1@email.com;user2@email.com")
        assertNull(viewModel.formState.value!!.membersError)
        assert(viewModel.formState.value!!.isDataValid)
    }

    @Test
    fun dataChanged_members_isInvalid() {
        viewModel.dataChanged("name", "user1")
        assertNotNull(viewModel.formState.value!!.membersError)
        assertFalse(viewModel.formState.value!!.isDataValid)
    }
}
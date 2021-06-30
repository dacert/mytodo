package pt.ipleiria.mytodo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.every
import io.mockk.invoke
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import pt.ipleiria.mytodo.dataLayer.models.Group
import pt.ipleiria.mytodo.dataLayer.repositories.GroupsRepository
import pt.ipleiria.mytodo.shared.SharedUser
import pt.ipleiria.mytodo.ui.groups.viewModels.GroupsViewModel

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class GroupsViewModelUnitTest {
    lateinit var viewModel: GroupsViewModel

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun init(){
        SharedUser.email = "user@email.com"
        mockkObject(GroupsRepository)
        viewModel = GroupsViewModel()
    }

    @Test
    fun fetch_isSuccess() {
        every { GroupsRepository.fetch(captureLambda()) } answers {
            val list = listOf<Group>()
            list.plus(Group())
            lambda<(isSuccess: Boolean, result: List<Group>) -> Unit>().invoke(true, list)
        }
        viewModel.fetchList()
        verify { GroupsRepository.fetch(captureLambda()) }
    }
}
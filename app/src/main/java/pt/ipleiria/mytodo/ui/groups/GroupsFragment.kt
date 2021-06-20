package pt.ipleiria.mytodo.ui.groups

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.groups_fragment.*
import pt.ipleiria.mytodo.adapters.GroupsAdapter
import org.jetbrains.anko.longToast
import pt.ipleiria.mytodo.R
import pt.ipleiria.mytodo.databinding.GroupsFragmentBinding
import pt.ipleiria.mytodo.setActivityTitle
import pt.ipleiria.mytodo.ui.groups.viewModels.GroupsViewModel

class GroupsFragment : Fragment(), EditGroupDialog.EditGroupListener {

    private lateinit var viewDataBinding: GroupsFragmentBinding
    private lateinit var adapter: GroupsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = GroupsFragmentBinding.inflate(inflater, container, false).apply {
            viewmodel = ViewModelProvider(this@GroupsFragment).get(GroupsViewModel::class.java)
            lifecycleOwner = viewLifecycleOwner
        }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.viewmodel?.fetchList()

        setupAdapter()
        setupObservers()
    }

    private fun setupObservers() {
        viewDataBinding.viewmodel?.listLive?.observe(viewLifecycleOwner, Observer {
            adapter.updateList(it)
        })

        viewDataBinding.viewmodel?.toastMessage?.observe(viewLifecycleOwner, Observer {
            activity?.longToast(it)
        })
    }

    private fun setupAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            adapter = GroupsAdapter()
            val layoutManager = LinearLayoutManager(activity)
            groups_rv.layoutManager = layoutManager
            groups_rv.addItemDecoration(DividerItemDecoration(activity, layoutManager.orientation))
            groups_rv.adapter = adapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        menu.findItem(R.id.action_remove).isVisible = false
        menu.findItem(R.id.action_edit).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id == R.id.action_add) {
            val dialog = EditGroupDialog.newInstance("Add new Group")
            dialog.listener = this
            dialog.show(parentFragmentManager, EditGroupDialog.TAG)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaved(isSuccess: Boolean) {
        if(isSuccess) viewDataBinding.viewmodel?.fetchList()
    }

}
package pt.ipleiria.mytodo.ui.groups

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.groups_fragment.*
import pt.ipleiria.mytodo.adapters.GroupsAdapter
import org.jetbrains.anko.longToast
import pt.ipleiria.mytodo.databinding.GroupsFragmentBinding

class GroupsFragment : Fragment() {

    private lateinit var viewDataBinding: GroupsFragmentBinding
    private lateinit var adapter: GroupsAdapter

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

}
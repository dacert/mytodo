package pt.ipleiria.mytodo.ui.todos

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.groups_fragment.*
import pt.ipleiria.mytodo.adapters.TodosAdapter
import pt.ipleiria.mytodo.databinding.TodosFragmentBinding
import pt.ipleiria.mytodo.ui.todos.viewModels.TodosViewModel

class TodosFragment : Fragment() {

    private lateinit var viewDataBinding: TodosFragmentBinding
    private lateinit var adapter: TodosAdapter
    private val args : TodosFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = TodosFragmentBinding.inflate(inflater, container, false).apply {
            viewmodel = ViewModelProvider(this@TodosFragment).get(TodosViewModel::class.java)
            lifecycleOwner = viewLifecycleOwner
        }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.viewmodel?.fetchList(args?.id)

        setupAdapter()
        setupObservers()
    }

    private fun setupObservers() {
        viewDataBinding.viewmodel?.listLive?.observe(viewLifecycleOwner, Observer {
            adapter.updateList(it)
        })

        viewDataBinding.viewmodel?.toastMessage?.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        })
    }

    private fun setupAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            adapter = TodosAdapter()
            val layoutManager = LinearLayoutManager(activity)
            todos_rv.layoutManager = layoutManager
            todos_rv.addItemDecoration(DividerItemDecoration(activity, layoutManager.orientation))
            todos_rv.adapter = adapter
        }
    }
}
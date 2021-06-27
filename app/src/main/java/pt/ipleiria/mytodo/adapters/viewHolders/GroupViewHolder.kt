package pt.ipleiria.mytodo.adapters.viewHolders

import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.databinding.ViewDataBinding
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.sdk27.coroutines.onClick
import pt.ipleiria.mytodo.BR
import pt.ipleiria.mytodo.R
import pt.ipleiria.mytodo.base.OnItemClickListener
import pt.ipleiria.mytodo.models.Group

class GroupViewHolder constructor(private val dataBinding: ViewDataBinding)
    : RecyclerView.ViewHolder(dataBinding.root) {

    fun setup(data: Group, editListener: OnItemClickListener) {
        dataBinding.setVariable(BR.data, data)
        dataBinding.executePendingBindings()

        itemView.findViewById<Button>(R.id.group_list_item_edit).onClick { editListener.onClick(data) }

        itemView.findViewById<TextView>(R.id.group_list_item_name).onClick {
            val bundle = bundleOf("id" to data.id, "title" to data.name)
            itemView.findNavController().navigate(R.id.action_groups_to_todos, bundle)
        }
    }
}
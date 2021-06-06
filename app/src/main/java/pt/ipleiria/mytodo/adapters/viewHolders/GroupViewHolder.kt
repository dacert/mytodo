package pt.ipleiria.mytodo.adapters.viewHolders

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import pt.ipleiria.mytodo.BR
import pt.ipleiria.mytodo.models.Group

class GroupViewHolder constructor(private val dataBinding: ViewDataBinding)
    : RecyclerView.ViewHolder(dataBinding.root) {

    fun setup(data: Group) {
        dataBinding.setVariable(BR.data, data)
        dataBinding.executePendingBindings()
    }
}
package pt.ipleiria.mytodo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pt.ipleiria.mytodo.databinding.GroupListItemBinding
import pt.ipleiria.mytodo.adapters.viewHolders.GroupViewHolder
import pt.ipleiria.mytodo.base.OnItemClickListener
import pt.ipleiria.mytodo.dataLayer.models.Group

class GroupsAdapter() : RecyclerView.Adapter<GroupViewHolder>() {
    var list: List<Group> = emptyList()
    lateinit var itemClickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val dataBinding = GroupListItemBinding.inflate(inflater, parent, false)
        return GroupViewHolder(dataBinding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.setup(list[position], itemClickListener)
    }

    fun updateList(list: List<Group>) {
        this.list = list
        notifyDataSetChanged()
    }
}
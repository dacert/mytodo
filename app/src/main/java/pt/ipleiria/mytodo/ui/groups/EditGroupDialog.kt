package pt.ipleiria.mytodo.ui.groups

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import pt.ipleiria.mytodo.R
import pt.ipleiria.mytodo.base.EditDialog
import pt.ipleiria.mytodo.dataLayer.models.Base
import pt.ipleiria.mytodo.dataLayer.models.Group
import pt.ipleiria.mytodo.shared.SharedUser
import pt.ipleiria.mytodo.ui.groups.viewModels.EditGroupDialogViewModel


class EditGroupDialog : EditDialog<EditGroupDialogViewModel>(EditGroupDialogViewModel::class.java) {

    companion object {
        const val TAG = "EditGroupDialog"

        fun newInstance(title: String, data: Base?): EditGroupDialog {
            val args = Bundle()
            args.putString(KEY_TITLE, title)
            args.putSerializable(KEY_DATA, data)

            val fragment = EditGroupDialog()
            fragment.arguments = args
            return fragment
        }
    }

    private var group: Group? = null
    private lateinit var membersEditText: EditText
    private lateinit  var nameEditText: EditText
    private lateinit var ownerText: TextView
    lateinit var listener: EditGroupListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_group_dialog, container, false)
    }

    override fun onInit(view: View) {
        group = arguments?.getSerializable(KEY_DATA) as Group?

        ownerText = view.findViewById(R.id.group_owner)
        nameEditText = view.findViewById(R.id.group_name)
        membersEditText = view.findViewById(R.id.group_members)

        //fil form
        nameEditText.setText(group?.name ?: "")
        ownerText.text = "Owner: ${group?.owner ?: SharedUser.email}"
        membersEditText.setText(group?.members?.joinToString(";") ?: "")

        nameEditText.isEnabled = isNew() || isOwner()
        membersEditText.isEnabled = isNew() || isOwner()

        viewModel.formState.observe(this,
            Observer { state ->
                if (state == null) {
                    return@Observer
                }
                saveButton.isEnabled = state.isDataValid
                state.nameError?.let {
                    nameEditText.error = getString(it)
                }
                state.membersError?.let {
                    membersEditText.error = getString(it)
                }
            })


        nameEditText.addTextChangedListener(afterTextChangedListener)
        membersEditText.addTextChangedListener(afterTextChangedListener)

        val onComplete = onComplete@ { isSuccess: Boolean ->
            listener.onSaved(isSuccess)
            if(isSuccess) {
                dismiss()
            }
            return@onComplete
        }

        saveButton.setOnClickListener {
            if(isNew()){
                viewModel.add(
                    nameEditText.text.toString().trim(),
                    membersEditText.text.toString().trim(),
                    onComplete)
            } else {
                viewModel.edit(group!!,
                    nameEditText.text.toString().trim(),
                    membersEditText.text.toString().trim(),
                    onComplete
                )
            }
        }
    }

    override fun afterTextChanged(s: Editable) {
        viewModel.dataChanged(
            nameEditText.text.toString(),
            membersEditText.text.toString()
        )
    }

    override fun onRemove() {
        viewModel.remove(group!!.id){ isSuccess ->
            listener.onSaved(isSuccess)
            if(isSuccess) {
                dismiss()
            }
        }
    }

    override fun isOwner(): Boolean {
        val data = arguments?.getSerializable(KEY_DATA) as Group?
        return data?.owner == SharedUser.email
    }

    interface EditGroupListener {
        fun onSaved(isSuccess: Boolean)
    }
}
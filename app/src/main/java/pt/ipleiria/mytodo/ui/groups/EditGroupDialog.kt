package pt.ipleiria.mytodo.ui.groups

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import pt.ipleiria.mytodo.R
import pt.ipleiria.mytodo.models.Group
import pt.ipleiria.mytodo.shared.SharedUser
import pt.ipleiria.mytodo.ui.groups.viewModels.EditGroupDialogViewModel


class EditGroupDialog : DialogFragment() {

    companion object {
        const val TAG = "EditGroupDialog"
        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_DATA = "KEY_DATA"

        fun newInstance(title: String, data: Group?): EditGroupDialog {
            val args = Bundle()
            args.putString(KEY_TITLE, title)
            args.putSerializable(KEY_DATA, data)
            val fragment = EditGroupDialog()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: EditGroupDialogViewModel
    lateinit var listener: EditGroupListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_group_dialog, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditGroupDialogViewModel::class.java)
        dialog!!.setCanceledOnTouchOutside(false)

        val titleText = view.findViewById<TextView>(R.id.dialog_tilte)
        val ownerText = view.findViewById<TextView>(R.id.group_owner)
        val nameEditText = view.findViewById<EditText>(R.id.group_name)
        val membersEditText = view.findViewById<EditText>(R.id.group_members)
        val saveButton = view.findViewById<Button>(R.id.save_group)
        val cancelButton = view.findViewById<Button>(R.id.cancel_group)
        val deleteButton = view.findViewById<Button>(R.id.delete_group)
        val loadingProgressBar = view.findViewById<ProgressBar>(R.id.group_loading)

        //set title
        titleText.text = arguments?.getString(KEY_TITLE)
        val group = arguments?.getSerializable(KEY_DATA) as Group?

        //fil form
        nameEditText.setText(group?.name ?: "")
        ownerText.text = "Owner: ${group?.owner ?: SharedUser.email}"
        membersEditText.setText(group?.members?.joinToString(";") ?: "")

        val isNew = group == null
        val isOwner = group != null && group?.owner == SharedUser.email

        deleteButton.visibility = if(isOwner) View.VISIBLE else View.GONE
        saveButton.visibility = if(isNew || isOwner) View.VISIBLE else View.GONE
        saveButton.isEnabled = !isNew
        nameEditText.isEnabled = isNew || isOwner
        membersEditText.isEnabled = isNew || isOwner

        viewModel.dataLoading.observe(this, Observer { isLoading ->
            if (isLoading == null) {
                return@Observer
            }
            loadingProgressBar.visibility = if(isLoading) View.VISIBLE else View.GONE
        })

        viewModel.toastMessage.observe(this, Observer {
            if(it.isNotEmpty()) Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        })

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

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                viewModel.dataChanged(
                    nameEditText.text.toString(),
                    membersEditText.text.toString()
                )
            }
        }
        nameEditText.addTextChangedListener(afterTextChangedListener)
        membersEditText.addTextChangedListener(afterTextChangedListener)

        cancelButton.setOnClickListener {
            dismiss()
        }

        deleteButton.setOnClickListener {
            showDeleteConfirmation(group!!.id){ isSuccess ->
                listener.onSaved(isSuccess)
                if(isSuccess) {
                    dismiss()
                }
            }
        }


        val onComplete = onComplete@ { isSuccess: Boolean ->
            listener.onSaved(isSuccess)
            if(isSuccess) {
                dismiss()
            }
            return@onComplete
        }

        saveButton.setOnClickListener {
            if(isNew){
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

    private fun showDeleteConfirmation(id: String, onDelete: (isSuccess: Boolean) -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        with(builder){
            setTitle(R.string.confirm_delete_title)
            setMessage(R.string.confirm_delete_msg)
            setNegativeButton(R.string.action_cancel) { _, _ -> Unit }
            setPositiveButton(R.string.action_ok) { _, _ ->
                viewModel.delete(id, onDelete)
            }
            show()
        }
    }

    interface EditGroupListener {
        fun onSaved(isSuccess: Boolean)
    }
}
package pt.ipleiria.mytodo.base

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import pt.ipleiria.mytodo.R

open class EditDialog<T: BaseViewModel>(private val modelClass: Class<T>): DialogFragment() {

    companion object {
        const val TAG = "EditDialog"
        const val KEY_TITLE = "KEY_TITLE"
        const val KEY_DATA = "KEY_DATA"
    }

    private lateinit var deleteConfirmation: AlertDialog.Builder
    protected lateinit var saveButton: Button
    protected lateinit var viewModel: T

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    protected fun isNew(): Boolean {
        return arguments?.getSerializable(KEY_DATA) == null
    }

    protected open fun isOwner(): Boolean {
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCanceledOnTouchOutside(false)
        viewModel = ViewModelProvider(this).get(modelClass)
        init(view)
    }

    private fun init(view: View){
        val titleText = view.findViewById<TextView>(R.id.dialog_title)
        saveButton = view.findViewById(R.id.dialog_action_save)
        val cancelButton = view.findViewById<Button>(R.id.dialog_action_cancel)
        val deleteButton = view.findViewById<Button>(R.id.dialog_action_delete)
        val loadingProgressBar = view.findViewById<ProgressBar>(R.id.dialog_loading)


        titleText.text = arguments?.getString(KEY_TITLE)
        deleteButton.visibility = if(isOwner()) View.VISIBLE else View.GONE
        saveButton.visibility = if(isNew() || isOwner()) View.VISIBLE else View.GONE
        saveButton.isEnabled = !isNew()


        viewModel.dataLoading.observe(this, Observer { isLoading ->
            if (isLoading == null) {
                return@Observer
            }
            loadingProgressBar.visibility = if(isLoading) View.VISIBLE else View.GONE
        })

        viewModel.toastMessage.observe(this, Observer {
            if(it.isNotEmpty()) Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        })

        cancelButton.setOnClickListener {
            dismiss()
        }

        deleteButton.setOnClickListener {
            deleteConfirmation.show()
        }
        buildDeleteConfirmation()

        onInit(view)
    }

    private fun buildDeleteConfirmation() {
        deleteConfirmation = AlertDialog.Builder(requireContext())
        with(deleteConfirmation){
            setTitle(R.string.confirm_delete_title)
            setMessage(R.string.confirm_delete_msg)
            setNegativeButton(R.string.action_cancel) { _, _ -> Unit }
            setPositiveButton(R.string.action_ok) { _, _ ->
                onRemove()
            }
        }
    }

    protected val afterTextChangedListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // ignore
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // ignore
        }

        override fun afterTextChanged(s: Editable) {
            this@EditDialog.afterTextChanged(s)
        }
    }

    protected open fun afterTextChanged(s: Editable) {
        //overwrite this function
    }

    protected open fun onInit(view: View) {
        //overwrite this function
    }

    protected open fun onRemove() {
        //overwrite this function
    }

}
package com.app.currencyconverter.common.ui

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.app.currencyconverter.R
import com.app.currencyconverter.common.callbacks.ProgressDialogCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProgressDialogFragment : DialogFragment() {

    private var title: String? = null
    private var message: String? = null

    private var mListener: ProgressDialogCallback? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(true)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        }
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            title = requireArguments().getString(ARG_PARAM1)
            message = requireArguments().getString(ARG_PARAM2)
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onButtonPressed()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.progress_dialog_fragment, container, false)
    }

    private fun onButtonPressed() {
        if (mListener != null) {
            mListener!!.onProgressDialogCancelled()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent is ProgressDialogCallback) {
            mListener = parent
        } else if (context is ProgressDialogCallback) {
            mListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    companion object {

        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        fun newInstance(title: String = "", message: String = ""): ProgressDialogFragment {
            val fragment = ProgressDialogFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, title)
            args.putString(ARG_PARAM2, message)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor

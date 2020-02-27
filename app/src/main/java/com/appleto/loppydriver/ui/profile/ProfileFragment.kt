package com.appleto.loppydriver.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.appleto.loppydriver.R
import com.appleto.loppydriver.helper.Const
import com.appleto.loppydriver.helper.PrefUtils
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edtFirstName.setText(activity?.let { PrefUtils.getStringValue(it, Const.NAME) })
        edtEmail.setText(activity?.let { PrefUtils.getStringValue(it, Const.EMAIL) })
        edtPhone.setText(activity?.let { PrefUtils.getStringValue(it, Const.MOBILE_NO) })
    }
}
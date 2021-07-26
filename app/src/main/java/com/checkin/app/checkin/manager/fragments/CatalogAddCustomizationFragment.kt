package com.checkin.app.checkin.manager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.checkin.app.checkin.R
import com.checkin.app.checkin.databinding.FragmentCatalogAddCustomizationBinding
import com.checkin.app.checkin.databinding.FragmentCatalogAddGroupBinding
import com.checkin.app.checkin.manager.activities.CatalogActivity
import com.checkin.app.checkin.misc.fragments.BaseFragment

class CatalogAddCustomizationFragment : BaseFragment() {

    override val rootLayout: Int = R.layout.fragment_catalog_add_customization
    private var _binding: FragmentCatalogAddCustomizationBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentCatalogAddCustomizationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.button2.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_catalog, CatalogAddItemsFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
        (requireActivity() as CatalogActivity).binding.tvActionBarTitle.text = "Add Ons"

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = CatalogAddCustomizationFragment()
    }
}
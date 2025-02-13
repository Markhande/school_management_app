package com.example.schoolerp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schoolerp.Adapter.BankStatementAdapter
import com.example.schoolerp.DataClasses.BankStatementData
import com.example.schoolerp.R
import com.example.schoolerp.databinding.FragmentAccountStatementBinding

class AccountStatement : Fragment() {
    private lateinit var binding:FragmentAccountStatementBinding
    private lateinit var bankStatementAdapter: BankStatementAdapter
    private lateinit var bankStatementList: MutableList<BankStatementData> // Initialized as MutableList

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAccountStatementBinding.bind(inflater.inflate(R.layout.fragment_account_statement,null))
        bankStatementList = mutableListOf()
        initView()
        initData()



        return binding.root
    }
    private fun initView(){
        bankStatementAdapter=BankStatementAdapter(bankStatementList)
        binding.recyclerViewAccountStatement.layoutManager=LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        binding.recyclerViewAccountStatement.adapter=bankStatementAdapter

    }
    private fun initData(){
        bankStatementList.add(BankStatementData("2024-10-01", "Purchase", "100", "0", "900"))
        bankStatementList.add(BankStatementData("2024-10-02", "Deposit", "0", "200", "1100"))
        bankStatementList.add(BankStatementData("2024-10-03", "Withdraw", "50", "0", "1050"))
        bankStatementList.add(BankStatementData("2024-10-01", "Purchase", "100", "0", "900"))
        bankStatementList.add(BankStatementData("2024-10-02", "Deposit", "0", "200", "1100"))
        bankStatementList.add(BankStatementData("2024-10-03", "Withdraw", "50", "0", "1050"))
        bankStatementList.add(BankStatementData("2024-10-01", "Purchase", "100", "0", "900"))
        bankStatementList.add(BankStatementData("2024-10-02", "Deposit", "0", "200", "1100"))
        bankStatementList.add(BankStatementData("2024-10-03", "Withdraw", "50", "0", "1050"))
        bankStatementList.add(BankStatementData("2024-10-01", "Purchase", "100", "0", "900"))
        bankStatementList.add(BankStatementData("2024-10-02", "Deposit", "0", "200", "1100"))
        bankStatementList.add(BankStatementData("2024-10-03", "Withdraw", "50", "0", "1050"))

        
        bankStatementAdapter.notifyDataSetChanged()
    }
}
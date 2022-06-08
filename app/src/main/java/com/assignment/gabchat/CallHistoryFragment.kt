package com.assignment.gabchat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.assignment.gabchat.adapter.CallHistoryAdapter
import com.sendbird.calls.DirectCallLog
import com.sendbird.calls.DirectCallLogListQuery
import com.sendbird.calls.SendBirdCall.createDirectCallLogListQuery
import com.sendbird.calls.SendBirdException
import com.sendbird.calls.handler.DirectCallLogListQueryResultHandler


class CallHistoryFragment : Fragment() {
    private lateinit var viewOfLayout: View
    private var callHistoryQuery: DirectCallLogListQuery? = null
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewOfLayout = inflater.inflate(R.layout.fragment_call_history, container, false)
        recyclerView = viewOfLayout.findViewById<RecyclerView>(R.id.recycler_history)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val params = DirectCallLogListQuery.Params()
        params.setLimit(100)
        callHistoryQuery = createDirectCallLogListQuery(params)

        callHistoryQuery!!.next(object : DirectCallLogListQueryResultHandler {
            override fun onResult(callLogs: List<DirectCallLog>?, e: SendBirdException?) {
                if (e == null) {
                    //if (callHistoryQuery!!.hasNext() && !callHistoryQuery!!.isLoading) {

                    val adapter = getContext()?.let { CallHistoryAdapter(it) }
                    adapter?.getCallLogo(callLogs)
                    adapter?.notifyDataSetChanged()
                    recyclerView.adapter = adapter
                    //  }

                } else {
                    Log.e("GABCHAT error (call history):", e.message.toString())
                }
            }

        })

        return viewOfLayout
    }

}
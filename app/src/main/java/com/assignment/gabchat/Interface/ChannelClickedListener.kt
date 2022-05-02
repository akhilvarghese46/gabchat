package com.assignment.gabchat.Interface

import com.assignment.gabchat.dataclass.ChannelModel

interface ChannelClickedListener {
    fun onChannelListener(channel: ChannelModel)
}
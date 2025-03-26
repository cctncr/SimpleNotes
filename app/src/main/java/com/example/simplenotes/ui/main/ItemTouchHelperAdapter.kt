package com.example.simplenotes.ui.main

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun onItemDismiss(position: Int)
    fun onReorderComplete()
}
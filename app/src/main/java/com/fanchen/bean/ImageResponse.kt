package com.fanchen.bean

data class ImageResponse(
        val `data`: DataX,
        val currentPage: Any,
        val error: Any,
        val pageSize: Any,
        val pagination: Any,
        val status: Int,
        val success: String,
        val token: String,
        val totalPage: Any,
        val totalRecord: Any
)
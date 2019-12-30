package com.example.simplerecyclerview.OkHTTP

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request

class OkHttpRequest(client: OkHttpClient) {
    internal var client = OkHttpClient()

    init {
        this.client = client
    }

    fun getCall(url: String, callback: Callback): Call {
        val request = Request.Builder().url(url).build()
        val call = client.newCall(request)
        call.enqueue(callback)
        return call
    }

    fun getJson(url: String, callback: Callback) {
        val gz = getCall(url, callback)

    }
}
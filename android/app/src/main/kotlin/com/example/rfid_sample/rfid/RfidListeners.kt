package com.example.rfid_sample.rfid

interface RfidListeners {
    fun onSuccess(`object`: Any?)

    fun onFailure(exception: Exception?)

    fun onFailure(message: String?)
}

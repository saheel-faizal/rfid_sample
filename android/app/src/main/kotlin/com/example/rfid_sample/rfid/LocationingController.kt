package com.example.rfid_sample.rfid

import android.os.AsyncTask
import android.util.Log
import com.zebra.demo.rfidreader.rfid.RFIDController.isInventoryAborted
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock

class LocationingController {
    var locateLock: Lock = object : Lock {
        private var isLocked = false

        @Synchronized
        override fun lock() {
            while (isLocked) {
                try {
                    (this as Object).wait()
                } catch (e: InterruptedException) {
                    Log.d(TAG, "Returned SDK Exception")
                }
            }
            isLocked = true
        }

        @Synchronized
        override fun unlock() {
            isLocked = false
            (this as Object).notify()
        }

        @Throws(InterruptedException::class)
        override fun lockInterruptibly() {
        }

        override fun tryLock(): Boolean {
            return false
        }

        @Throws(InterruptedException::class)
        override fun tryLock(time: Long, unit: TimeUnit): Boolean {
            return false
        }


        override fun newCondition(): Condition {
            return null
        }
    }

    fun locationing(locateTag: String?, rfidListeners: RfidListeners) {
        if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.isConnected) {
            if (!RFIDController.isLocatingTag) {
                RFIDController.currentLocatingTag = locateTag
                RFIDController.TagProximityPercent = 0
                if (locateTag != null && !locateTag.isEmpty()) {
                    RFIDController.isLocatingTag = true
                    object : AsyncTask<Void?, Void?, Boolean?>() {
                        private var invalidUsageException: InvalidUsageException? = null
                        private var operationFailureException: OperationFailureException? = null

                        protected override fun doInBackground(vararg voids: Void): Boolean? {
                            locateLock.lock()
                            try {
                                if (RFIDController.asciiMode) {
                                    RFIDController.mConnectedReader.Actions.TagLocationing.Perform(
                                        asciitohex.convert(locateTag),
                                        null,
                                        null
                                    )
                                    RFIDController.isLocatingTag = true
                                } else {
                                    RFIDController.mConnectedReader.Actions.TagLocationing.Perform(
                                        locateTag,
                                        null,
                                        null
                                    )
                                    RFIDController.isLocatingTag = true
                                }
                            } catch (e: InvalidUsageException) {
                                Log.d(TAG, "Returned SDK Exception")
                                invalidUsageException = e
                            } catch (e: OperationFailureException) {
                                Log.d(TAG, "Returned SDK Exception")
                                operationFailureException = e
                            }
                            return null
                        }

                        override fun onPostExecute(result: Boolean?) {
                            locateLock.unlock()
                            RFIDController.isLocatingTag = true
                            if (invalidUsageException != null) {
                                RFIDController.currentLocatingTag = null
                                RFIDController.isLocatingTag = false
                                rfidListeners.onFailure(invalidUsageException)
                            } else if (operationFailureException != null) {
                                RFIDController.currentLocatingTag = null
                                RFIDController.isLocatingTag = false
                                rfidListeners.onFailure(operationFailureException)
                            } else rfidListeners.onSuccess(null)
                        }
                    }.execute()
                } else {
                    Log.d(RFIDController.TAG, Constants.TAG_EMPTY)
                    rfidListeners.onFailure(Constants.TAG_EMPTY)
                }
            } else {
                isLocationingAborted = false
                mIsInventoryRunning = false
                isLocatingTag = false
                isInventoryAborted = false
                object : AsyncTask<Void?, Void?, Boolean?>() {
                    private var invalidUsageException: InvalidUsageException? = null
                    private var operationFailureException: OperationFailureException? = null

                    protected override fun doInBackground(vararg voids: Void): Boolean? {
                        locateLock.lock()
                        try {
                            RFIDController.mConnectedReader.Actions.TagLocationing.Stop()
                            if (((RFIDController.settings_startTrigger != null && (RFIDController.settings_startTrigger.triggerType === START_TRIGGER_TYPE.START_TRIGGER_TYPE_HANDHELD || RFIDController.settings_startTrigger.triggerType === START_TRIGGER_TYPE.START_TRIGGER_TYPE_PERIODIC)))
                                || (RFIDController.isBatchModeInventoryRunning != null && RFIDController.isBatchModeInventoryRunning)
                            ) ConnectionController.operationHasAborted(rfidListeners)
                        } catch (e: InvalidUsageException) {
                            invalidUsageException = e
                            Log.d(TAG, "Returned SDK Exception")
                        } catch (e: OperationFailureException) {
                            operationFailureException = e
                            Log.d(TAG, "Returned SDK Exception")
                        }
                        return null
                    }

                    override fun onPostExecute(result: Boolean?) {
                        locateLock.unlock()
                        RFIDController.isLocatingTag = false
                        RFIDController.currentLocatingTag = null
                        if (invalidUsageException != null) {
                            rfidListeners.onFailure(invalidUsageException)
                        } else if (operationFailureException != null) {
                            rfidListeners.onFailure(operationFailureException)
                        } else rfidListeners.onSuccess(null)
                    }
                }.execute()
            }
        } else rfidListeners.onFailure("No Active Connection with Reader")
    }


    companion object {
        private const val TAG = "LocationingController"
    }
}

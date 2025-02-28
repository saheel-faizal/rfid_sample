package com.example.rfid_sample.rfid

import android.os.AsyncTask
import android.util.Log
import com.zebra.demo.application.Application
import com.zebra.rfid.api3.AntennaInfo
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock

class InventoryController {
    var lock: Lock = object : Lock {
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

    fun inventoryWithTamperfind(
        memoryBankID: String?,
        rfidListeners: RfidListeners,
        antennaInfo: AntennaInfo?
    ) {
        if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.isConnected) {
            val tagAccess: TagAccess = TagAccess()
            val readAccessParams: ReadAccessParams = tagAccess.ReadAccessParams()
            //Set the param values
            readAccessParams.setCount(1)
            readAccessParams.setOffset(32)
            readAccessParams.setMemoryBank(MEMORY_BANK.MEMORY_BANK_EPC)
            try {
                //Read command with readAccessParams and accessFilter as null to read all the tags
                RFIDController.mConnectedReader.Actions.TagAccess.readEvent(
                    readAccessParams,
                    null,
                    antennaInfo
                )
                RFIDController.mIsInventoryRunning = true
                rfidListeners.onSuccess(null)
            } catch (e: InvalidUsageException) {
                if (e != null && e.getStackTrace().size > 0) {
                    Log.e(TAG, e.getStackTrace().get(0).toString())
                }
            } catch (e: OperationFailureException) {
                if (e != null && e.getStackTrace().size > 0) {
                    Log.e(TAG, e.getStackTrace().get(0).toString())
                }
                rfidListeners.onFailure(e)
            }
        } else rfidListeners.onFailure("No Active Connection with Reader")
    }


    fun inventoryWithMemoryBank(
        memoryBankID: String?,
        rfidListeners: RfidListeners,
        antennaInfo: AntennaInfo?
    ) {
        if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.isConnected) {
            val tagAccess: TagAccess = TagAccess()
            val readAccessParams: ReadAccessParams = tagAccess.ReadAccessParams()
            //Set the param values
            readAccessParams.setCount(0)
            readAccessParams.setOffset(0)
            if ("RESERVED".equals(memoryBankID, ignoreCase = true)) readAccessParams.setMemoryBank(
                MEMORY_BANK.MEMORY_BANK_RESERVED
            )
            if ("EPC".equals(memoryBankID, ignoreCase = true)) readAccessParams.setMemoryBank(
                MEMORY_BANK.MEMORY_BANK_EPC
            )
            if ("TID".equals(memoryBankID, ignoreCase = true)) readAccessParams.setMemoryBank(
                MEMORY_BANK.MEMORY_BANK_TID
            )
            if ("USER".equals(memoryBankID, ignoreCase = true)) readAccessParams.setMemoryBank(
                MEMORY_BANK.MEMORY_BANK_USER
            )
            try {
                //Read command with readAccessParams and accessFilter as null to read all the tags
                RFIDController.mConnectedReader.Actions.TagAccess.readEvent(
                    readAccessParams,
                    null,
                    antennaInfo
                )
                RFIDController.mIsInventoryRunning = true
                rfidListeners.onSuccess(null)
            } catch (e: InvalidUsageException) {
                if (e != null && e.getStackTrace().size > 0) {
                    Log.e(TAG, e.getStackTrace().get(0).toString())
                }
            } catch (e: OperationFailureException) {
                if (e != null && e.getStackTrace().size > 0) {
                    Log.e(TAG, e.getStackTrace().get(0).toString())
                }
                rfidListeners.onFailure(e)
            }
        } else rfidListeners.onFailure("No Active Connection with Reader")
    }

    fun performInventory(rfidListeners: RfidListeners, antennaInfo: AntennaInfo?) {
        object : AsyncTask<Void?, Void?, Boolean>() {
            var exception: OperationFailureException? = null
            var exceptionIN: InvalidUsageException? = null

            override fun onPreExecute() {
                super.onPreExecute()
            }

            protected override fun doInBackground(vararg voids: Void): Boolean {
                lock.lock()
                var isSuccess = true
                if (RFIDController.reportUniquetags != null && RFIDController.reportUniquetags.value == 1) {
                    RFIDController.mConnectedReader.Actions.purgeTags()
                }
                //Perform inventory
                try {
                    if (RFIDController.brandidcheckenabled) {
                        /* Perform Brandcheck for NXP tags*/
                        if (Application.strBrandID != null && Application.strBrandID.length() > 0) {
                            RFIDController.mConnectedReader.Actions.TagAccess.NXP.performBrandCheck(
                                Application.strBrandID,
                                Application.iBrandIDLen
                            )
                            Application.bBrandCheckStarted = true
                        } else RFIDController.mConnectedReader.Actions.Inventory.perform(
                            null,
                            null,
                            antennaInfo
                        )
                    } else RFIDController.mConnectedReader.Actions.Inventory.perform(
                        null,
                        null,
                        antennaInfo
                    )
                    RFIDController.mIsInventoryRunning = true
                    rfidListeners.onSuccess(null)
                    Log.d(RFIDController.TAG, "Inventory.perform")
                } catch (e: InvalidUsageException) {
                    isSuccess = false
                    exceptionIN = e
                    if (e != null && e.getStackTrace().size > 0) {
                        Log.e(TAG, e.getStackTrace().get(0).toString())
                    }
                } catch (e: OperationFailureException) {
                    isSuccess = false
                    exception = e
                    if (e != null && e.getStackTrace().size > 0) {
                        Log.e(TAG, e.getStackTrace().get(0).toString())
                    }
                }
                return isSuccess
            }

            override fun onPostExecute(result: Boolean) {
                lock.unlock()
                if (exception != null) {
                    if (RFIDController.batchMode != -1 && RFIDController.mConnectedReader.transport != "SERVICE_USB") {
                        if (RFIDController.batchMode == BATCH_MODE.ENABLE.getValue()) {
                            RFIDController.isBatchModeInventoryRunning = true
                        }
                    } else if (RFIDController.usbBatchMode != -1) {
                        if (RFIDController.usbBatchMode == USB_BATCH_MODE.ENABLE.getValue()) {
                            RFIDController.isBatchModeInventoryRunning = true
                        }
                    }
                    rfidListeners.onFailure(exception)
                } else if (exceptionIN != null) {
                    rfidListeners.onFailure(exceptionIN)
                } else rfidListeners.onSuccess(null)
            }
        }.execute()
    }

    fun stopInventory(rfidListeners: RfidListeners) {
        RFIDController.isInventoryAborted = true
        object : AsyncTask<Void?, Void?, Boolean>() {
            var exception: OperationFailureException? = null
            var exceptionIN: InvalidUsageException? = null


            protected override fun doInBackground(vararg voids: Void): Boolean {
                lock.lock()
                var isSuccess = false
                try {
                    RFIDController.mConnectedReader.Actions.Inventory.stop()
                    synchronized(RFIDController.isInventoryAborted) {
                        (RFIDController.isInventoryAborted as Object).notify()
                    }
                    if (((RFIDController.settings_startTrigger != null &&
                                (RFIDController.settings_startTrigger.triggerType === START_TRIGGER_TYPE.START_TRIGGER_TYPE_HANDHELD
                                        || RFIDController.settings_startTrigger.triggerType === START_TRIGGER_TYPE.START_TRIGGER_TYPE_PERIODIC)) ||
                                RFIDController.getRepeatTriggers())
                    ) {
                    } else isSuccess = true
                    Log.d(RFIDController.TAG, "Inventory.stop")
                } catch (e: InvalidUsageException) {
                    isSuccess = false
                    exceptionIN = e
                    if (e != null && e.getStackTrace().size > 0) {
                        Log.e(TAG, e.getStackTrace().get(0).toString())
                    }
                } catch (e: OperationFailureException) {
                    if (e != null && e.getStackTrace().size > 0) {
                        Log.e(TAG, e.getStackTrace().get(0).toString())
                    }
                    isSuccess = false
                    exception = e
                }
                return isSuccess
            }

            override fun onPostExecute(result: Boolean) {
                lock.unlock()
                if (exception != null) {
                    rfidListeners.onFailure(exception)
                } else if (exceptionIN != null) {
                    rfidListeners.onFailure(exceptionIN)
                } else {
                    if (result) rfidListeners.onSuccess(null)
                    else rfidListeners.onFailure(null as Exception?)
                }
            }
        }.execute()
    }


    fun updateTagIDs() {
        if (Application.tagsReadInventory == null) return
        if (Application.tagsReadInventory.size() === 0) return
        if (Application.tagIDs == null) {
            Application.tagIDs = ArrayList<Any>()
            for (i in Application.tagsReadInventory) {
                if (i.getMemoryBank() != null) {
                    Application.tagIDs.add(i.getMemoryBankData())
                } else {
                    Application.tagIDs.add(i.getTagID())
                }
            }
        } else if (Application.tagIDs.size() !== Application.tagsReadInventory.size()) {
            Application.tagIDs.clear()
            for (i in Application.tagsReadInventory) {
                if (i.getMemoryBank() != null) {
                    Application.tagIDs.add(i.getMemoryBankData())
                } else {
                    Application.tagIDs.add(i.getTagID())
                }
            }
        } /*else{
            //Do Nothing. Array is up to date
        }*/
    }


    companion object {
        var TAG: String = "InventoryController"
    }
}

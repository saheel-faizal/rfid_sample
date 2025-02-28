package com.example.rfid_sample.rfid

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.util.Log
import com.example.rfid_sample.common.LinkProfileUtil
import com.example.rfid_sample.common.asciitohex
import com.zebra.rfid.api3.AntennaInfo
import com.zebra.rfid.api3.Antennas
import com.zebra.rfid.api3.InvalidUsageException
import com.zebra.rfid.api3.LOCK_DATA_FIELD
import com.zebra.rfid.api3.LOCK_PRIVILEGE
import com.zebra.rfid.api3.MEMORY_BANK
import com.zebra.rfid.api3.OperationFailureException
import com.zebra.rfid.api3.TagAccess
import com.zebra.rfid.api3.TagData

class AccessOperationController {
    @SuppressLint("LongLogTag")
    fun accessOperationsRead(
        tagValue: String,
        offsetText: String,
        lengthText: String,
        accessRwPassword: String,
        bankItem: String,
        rfidListeners: RfidListeners,
        antennaInfo: AntennaInfo?
    ) {
        RFIDController.accessControlTag = tagValue
        RFIDController.isAccessCriteriaRead = true
        val tagAccess: TagAccess = TagAccess()
        val readAccessParams: TagAccess.ReadAccessParams = tagAccess.ReadAccessParams()
        try {
            readAccessParams.setAccessPassword(java.lang.Long.decode("0X$accessRwPassword"))
        } catch (nfe: NumberFormatException) {
            if (nfe != null) {
                Log.e(TAG, nfe.message!!)
            }
            rfidListeners.onFailure("Password field is empty, defaulting to 00")
        }
        readAccessParams.setCount(lengthText.toInt())
        readAccessParams.setMemoryBank(getAccessRWMemoryBank(bankItem))
        readAccessParams.setOffset(offsetText.toInt())
        object : AsyncTask<Void?, Void?, TagData?>() {
            private var invalidUsageException: InvalidUsageException? = null
            private var operationFailureException: OperationFailureException? = null

            protected fun doInBackground(vararg voids: Void): TagData? {
                try {
                    setAccessProfile(true)
                    //if we are dealing with more than 255 bits access operation then set bFilter false
                    val bFilter = (tagValue.length <= 24)
                    val tagData = RFIDController.mConnectedReader.Actions.TagAccess.readWait(
                        tagValue,
                        readAccessParams,
                        antennaInfo,
                        bFilter
                    )
                    return tagData
                } catch (e: InvalidUsageException) {
                    invalidUsageException = e
                    if (e != null && e.getStackTrace().size > 0) {
                        Log.e(TAG, e.getStackTrace().get(0).toString())
                    }
                } catch (e: OperationFailureException) {
                    operationFailureException = e
                    if (e != null && e.getStackTrace().size > 0) {
                        Log.e(TAG, e.getStackTrace().get(0).toString())
                    }
                }
                return null
            }

            override fun onPostExecute(tagData: TagData?) {
                if (invalidUsageException != null) {
                    rfidListeners.onFailure(invalidUsageException)
                } else if (operationFailureException != null) {
                    rfidListeners.onFailure(operationFailureException)
                } else rfidListeners.onSuccess(tagData)
            }

            override fun doInBackground(vararg params: Void?): TagData? {
                TODO("Not yet implemented")
            }
        }.execute()
    }

    @SuppressLint("LongLogTag")
    fun accessOperationsWrite(
        tagValue: String,
        offsetText: String,
        lengthText: String?,
        accessRWData: String,
        accessRwPassword: String,
        bankItem: String,
        rfidListeners: RfidListeners,
        antennaInfo: AntennaInfo?
    ) {
        var accessRWData = accessRWData
        RFIDController.isAccessCriteriaRead = true
        val tagAccess: TagAccess = TagAccess()
        val writeAccessParams: TagAccess.WriteAccessParams = tagAccess.WriteAccessParams()
        try {
            writeAccessParams.accessPassword = java.lang.Long.decode("0X$accessRwPassword")
        } catch (nfe: NumberFormatException) {
            if (nfe != null) {
                Log.e(TAG, nfe.message!!)
            }
            rfidListeners.onFailure("Password field is empty, defaulting to 00")
        }
        writeAccessParams.memoryBank = getAccessRWMemoryBank(bankItem)
        writeAccessParams.offset = offsetText.toInt()
        if (RFIDController.asciiMode == true) {
            accessRWData = asciitohex.convert(accessRWData)
            writeAccessParams.setWriteData(accessRWData)
            writeAccessParams.writeDataLength = accessRWData.length / 4
        } else {
            writeAccessParams.setWriteData(accessRWData)
            writeAccessParams.writeDataLength = accessRWData.length / 4
        }
        object : AsyncTask<Void?, Void?, Boolean>() {
            private var invalidUsageException: InvalidUsageException? = null
            private var operationFailureException: OperationFailureException? = null
            private var bResult = false

            protected  fun doInBackground(vararg voids: Void): Boolean {
                try {
                    setAccessProfile(true)
                    //if we are dealing with more than 255 bits access operation then set bFilter false
                    val bFilter = (tagValue.length <= 24)
                    RFIDController.mConnectedReader.Actions.TagAccess.writeWait(
                        tagValue,
                        writeAccessParams,
                        antennaInfo,
                        null,
                        bFilter,
                        false
                    )
                    bResult = true
                } catch (e: InvalidUsageException) {
                    invalidUsageException = e
                    if (e != null && e.getStackTrace().size > 0) {
                        Log.e(TAG, e.getStackTrace().get(0).toString())
                    }
                } catch (e: OperationFailureException) {
                    operationFailureException = e
                    if (e != null && e.getStackTrace().size > 0) {
                        Log.e(TAG, e.getStackTrace().get(0).toString())
                    }
                }
                return bResult
            }

            override fun onPostExecute(result: Boolean) {
                if (!result) {
                    if (invalidUsageException != null) {
                        rfidListeners.onFailure(invalidUsageException)
                    } else if (operationFailureException != null) {
                        rfidListeners.onFailure(operationFailureException)
                    }
                } else rfidListeners.onSuccess(null)
            }

            override fun doInBackground(vararg params: Void?): Boolean {
                TODO("Not yet implemented")
            }
        }.execute()
    }

    @SuppressLint("LongLogTag")
    fun accessOperationLock(
        tagId: String?,
        accessRwPassword: String,
        lockDataField: LOCK_DATA_FIELD?,
        lockPrivilege: LOCK_PRIVILEGE?,
        ALL_Memory_Bank: Boolean,
        rfidListeners: RfidListeners,
        antennaInfo: AntennaInfo?
    ) {
        RFIDController.accessControlTag = tagId
        RFIDController.isAccessCriteriaRead = true
        //Set the param values
        val tagAccess: TagAccess = TagAccess()
        val lockAccessParams: TagAccess.LockAccessParams = tagAccess.LockAccessParams()
        if (lockDataField != null) lockAccessParams.setLockPrivilege(lockDataField, lockPrivilege)
        if (ALL_Memory_Bank) {
            lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_EPC_MEMORY, lockPrivilege)
            lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_ACCESS_PASSWORD, lockPrivilege)
            lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_KILL_PASSWORD, lockPrivilege)
            lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_TID_MEMORY, lockPrivilege)
            lockAccessParams.setLockPrivilege(LOCK_DATA_FIELD.LOCK_USER_MEMORY, lockPrivilege)
        }

        try {
            lockAccessParams.accessPassword = java.lang.Long.decode("0X$accessRwPassword")
        } catch (nfe: NumberFormatException) {
            if (nfe != null) {
                Log.e(TAG, nfe.message!!)
            }
            rfidListeners.onFailure("Password field is empty, defaulting to 00")
        }
        object : AsyncTask<Void?, Void?, Boolean>() {
            private var invalidUsageException: InvalidUsageException? = null
            private var operationFailureException: OperationFailureException? = null
            private var bResult = false

            protected fun doInBackground(vararg voids: Void): Boolean {
                try {
                    setAccessProfile(true)
                    RFIDController.mConnectedReader.Actions.TagAccess.lockWait(
                        tagId,
                        lockAccessParams,
                        antennaInfo,
                        false
                    )
                    bResult = true
                } catch (e: InvalidUsageException) {
                    invalidUsageException = e
                    if (e != null && e.getStackTrace().size > 0) {
                        Log.e(TAG, e.getStackTrace().get(0).toString())
                    }
                } catch (e: OperationFailureException) {
                    operationFailureException = e
                    if (e != null && e.getStackTrace().size > 0) {
                        Log.e(TAG, e.getStackTrace().get(0).toString())
                    }
                }
                return bResult
            }

            override fun onPostExecute(result: Boolean) {
                if (!result) {
                    if (invalidUsageException != null) {
                        rfidListeners.onFailure(invalidUsageException)
                    } else if (operationFailureException != null) {
                        rfidListeners.onFailure(operationFailureException)
                    }
                } else rfidListeners.onSuccess(null)
            }

            override fun doInBackground(vararg params: Void?): Boolean {
                TODO("Not yet implemented")
            }
        }.execute()
    }

    @SuppressLint("LongLogTag")
    fun accessOperationsKill(
        tagId: String,
        accessRWpassword: String,
        rfidListeners: RfidListeners,
        antennaInfo: AntennaInfo?
    ) {
        RFIDController.accessControlTag = tagId
        RFIDController.isAccessCriteriaRead = true
        //Set the param values
        val tagAccess: TagAccess = TagAccess()
        val killAccessParams: TagAccess.KillAccessParams = tagAccess.KillAccessParams()
        try {
            killAccessParams.killPassword = java.lang.Long.decode("0X$accessRWpassword")
        } catch (nfe: NumberFormatException) {
            if (nfe != null) {
                Log.e(TAG, nfe.message!!)
            }
            rfidListeners.onFailure("Password field is empty, defaulting to 00")
        }
        object : AsyncTask<Void?, Void?, Boolean>() {
            private var invalidUsageException: InvalidUsageException? = null
            private var operationFailureException: OperationFailureException? = null
            private var bResult = false

            protected fun doInBackground(vararg voids: Void): Boolean {
                try {
                    setAccessProfile(true)
                    val bFilter = (tagId.length <= 24)
                    RFIDController.mConnectedReader.Actions.TagAccess.killWait(
                        tagId,
                        killAccessParams,
                        antennaInfo,
                        bFilter
                    )
                    bResult = true
                } catch (e: InvalidUsageException) {
                    invalidUsageException = e
                    if (e != null && e.getStackTrace().size > 0) {
                        Log.e(TAG, e.getStackTrace().get(0).toString())
                    }
                } catch (e: OperationFailureException) {
                    operationFailureException = e
                    if (e != null && e.getStackTrace().size > 0) {
                        Log.e(TAG, e.getStackTrace().get(0).toString())
                    }
                }
                return bResult
            }

            override fun onPostExecute(result: Boolean) {
                if (!result) {
                    if (invalidUsageException != null) {
                        rfidListeners.onFailure(invalidUsageException)
                    } else if (operationFailureException != null) {
                        rfidListeners.onFailure(operationFailureException)
                    }
                } else rfidListeners.onSuccess(null)
            }

            override fun doInBackground(vararg params: Void?): Boolean {
                TODO("Not yet implemented")
            }
        }.execute()
    }

    @SuppressLint("LongLogTag")
    fun setAccessProfile(bSet: Boolean) {
        if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.isConnected && RFIDController.mConnectedReader.isCapabilitiesReceived
            && !RFIDController.mIsInventoryRunning && !RFIDController.isLocatingTag
        ) {
            var antennaRfConfigLocal: Antennas.AntennaRfConfig
            try {
                if (bSet && RFIDController.antennaRfConfig.getrfModeTableIndex() != 0L) {
                    for (i in 1..RFIDController.mConnectedReader.ReaderCapabilities.numAntennaSupported) {
                        antennaRfConfigLocal = RFIDController.antennaRfConfig
                        // use of default profile for access operation
                        antennaRfConfigLocal.setrfModeTableIndex(0)
                        RFIDController.mConnectedReader.Config.Antennas.setAntennaRfConfig(
                            i.toInt(),
                            antennaRfConfigLocal
                        )
                        RFIDController.antennaRfConfig = antennaRfConfigLocal
                    }
                } else if (!bSet && RFIDController.antennaRfConfig.getrfModeTableIndex() != LinkProfileUtil.getInstance()
                        .getSimpleProfileModeIndex(RFIDController.ActiveProfile.LinkProfileIndex)
                ) {
                    for (i in 1..RFIDController.mConnectedReader.ReaderCapabilities.numAntennaSupported) {
                        antennaRfConfigLocal = RFIDController.antennaRfConfig
                        antennaRfConfigLocal.setrfModeTableIndex(
                            LinkProfileUtil.getInstance()
                                .getSimpleProfileModeIndex(RFIDController.ActiveProfile.LinkProfileIndex)
                                .toLong()
                        )
                        RFIDController.mConnectedReader.Config.Antennas.setAntennaRfConfig(
                            i.toInt(),
                            antennaRfConfigLocal
                        )
                        RFIDController.antennaRfConfig = antennaRfConfigLocal
                    }
                }
            } catch (e: InvalidUsageException) {
                if (e != null && e.getStackTrace().size > 0) {
                    Log.e(TAG, e.getStackTrace().get(0).toString())
                }
            } catch (e: OperationFailureException) {
                if (e != null && e.getStackTrace().size > 0) {
                    Log.e(TAG, e.getStackTrace().get(0).toString())
                }
            }
        }
    }


    companion object {
        private const val TAG = "AccessOperationController"


        fun getAccessRWMemoryBank(bankItem: String): MEMORY_BANK {
            if ("RESV".equals(
                    bankItem,
                    ignoreCase = true
                ) || bankItem.contains("PASSWORD")
            ) return MEMORY_BANK.MEMORY_BANK_RESERVED
            else if ("EPC".equals(
                    bankItem,
                    ignoreCase = true
                ) || bankItem.contains("PC")
            ) return MEMORY_BANK.MEMORY_BANK_EPC
            else if ("TID".equals(bankItem, ignoreCase = true)) return MEMORY_BANK.MEMORY_BANK_TID
            else if ("USER".equals(bankItem, ignoreCase = true)) return MEMORY_BANK.MEMORY_BANK_USER
            return MEMORY_BANK.MEMORY_BANK_EPC
        }
    }
}

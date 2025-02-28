package com.example.rfid_sample.rfid

import android.os.AsyncTask
import android.util.Log
import com.zebra.demo.LoggerFragment.NGEERRORLOGSTATE
import com.zebra.rfid.api3.BuildConfig
import com.zebra.rfid.api3.RfidEventsListener
import com.zebra.rfid.api3.VersionInfo

class ConnectionController {
    fun AutoConnectDevice(
        password: String?,
        rfidEventsListener: RfidEventsListener?,
        rfidListeners: RfidListeners,
        updateUIListener: UpdateUIListener
    ) {
        RFIDController.autoConnectDeviceTask = object : AsyncTask<Void?, Void?, Boolean?>() {
            override fun onCancelled() {
                super.onCancelled()
                RFIDController.autoConnectDeviceTask = null
                rfidListeners.onFailure(null as String?)
            }

            var exception: OperationFailureException? = null
            var exceptionIN: InvalidUsageException? = null

            override fun onPreExecute() {
                super.onPreExecute()
            }

            protected override fun doInBackground(vararg params: Void): Boolean? {
                try {
                    if (RFIDController.readers != null && RFIDController.mConnectedReader == null /* && LAST_CONNECTED_READER.startsWith("RFD8500")*/) {
                        if (RFIDController.readers.GetAvailableRFIDReaderList() != null) {
                            RFIDController.mConnectedDevice =
                                getConnectedDeviceFromRFIDReaderList(RFIDController.LAST_CONNECTED_READER)
                            if (RFIDController.mConnectedDevice != null) {
                                RFIDController.mConnectedReader =
                                    RFIDController.mConnectedDevice.rfidReader
                                try {
                                    if (!RFIDController.mConnectedReader.isConnected && !this.isCancelled) {
                                        updateUIListener.updateProgressMessage(RFIDController.mConnectedReader.hostName)
                                        RFIDController.mConnectedReader.password = password
                                        RFIDController.mConnectedReader.connect()
                                    } else {
                                        this.cancel(true)
                                    }
                                } catch (e: NullPointerException) {
                                    Log.d(RFIDController.TAG, "null pointer ")
                                    if (e != null && e.stackTrace.size > 0) {
                                        Log.e(TAG, e.stackTrace[0].toString())
                                    }
                                } catch (e: InvalidUsageException) {
                                    if (e != null && e.getStackTrace().size > 0) {
                                        Log.e(TAG, e.getStackTrace().get(0).toString())
                                    }
                                } //catch (OperationFailureException e) {

                                //                                    if( e!= null && e.getStackTrace().length>0){ Log.e(TAG, e.getStackTrace()[0].toString()); }
//                                    exception = e;
//                                }
                                try {
                                    if (RFIDController.mConnectedReader.Events != null) {
                                        RFIDController.mConnectedReader.Events.addEventsListener(
                                            rfidEventsListener
                                        )
                                    }
                                } catch (e: InvalidUsageException) {
                                    if (e != null && e.getStackTrace().size > 0) {
                                        Log.e(TAG, e.getStackTrace().get(0).toString())
                                    }
                                } catch (e: OperationFailureException) {
                                    if (e != null && e.getStackTrace().size > 0) {
                                        Log.e(TAG, e.getStackTrace().get(0).toString())
                                    }
                                } catch (e: NullPointerException) {
                                    Log.d(RFIDController.TAG, "null pointer ")
                                    if (e != null && e.stackTrace.size > 0) {
                                        Log.e(TAG, e.stackTrace[0].toString())
                                    }
                                }
                                if (exception == null) {
                                    try {
                                        RFIDController.getInstance().updateReaderConnection(true)
                                    } catch (e: InvalidUsageException) {
                                        if (e != null && e.getStackTrace().size > 0) {
                                            Log.e(TAG, e.getStackTrace().get(0).toString())
                                        }
                                    } catch (e: OperationFailureException) {
                                        if (e != null && e.getStackTrace().size > 0) {
                                            Log.e(TAG, e.getStackTrace().get(0).toString())
                                        }
                                    } catch (e: NullPointerException) {
                                        Log.d(RFIDController.TAG, "null pointer ")
                                        if (e != null && e.stackTrace.size > 0) {
                                            Log.e(TAG, e.stackTrace[0].toString())
                                        }
                                    }
                                } else {
                                    RFIDController.clearSettings()
                                }
                            }
                        }
                    }
                } catch (ex: InvalidUsageException) {
                    exceptionIN = ex
                } catch (e: OperationFailureException) {
                    if (e != null && e.getStackTrace().size > 0) {
                        Log.e(TAG, e.getStackTrace().get(0).toString())
                    }
                    exception = e
                }
                return null
            }


            override fun onPostExecute(result: Boolean?) {
                if (RFIDController.mConnectedReader != null && RFIDController.mConnectedReader.isConnected) {
                    if (exception != null) {
                        if (exception.getResults() === RFIDResults.RFID_READER_REGION_NOT_CONFIGURED) {
                            try {
                                RFIDController.mConnectedReader.Events.addEventsListener(
                                    rfidEventsListener
                                )
                            } catch (e: InvalidUsageException) {
                                if (e != null && e.getStackTrace().size > 0) {
                                    Log.e(TAG, e.getStackTrace().get(0).toString())
                                }
                            } catch (e: OperationFailureException) {
                                if (e != null && e.getStackTrace().size > 0) {
                                    Log.e(TAG, e.getStackTrace().get(0).toString())
                                }
                            }
                            RFIDController.regionNotSet = true
                        } else if (exception.getResults() === RFIDResults.RFID_BATCHMODE_IN_PROGRESS) {
                            RFIDController.isBatchModeInventoryRunning = true
                            RFIDController.mIsInventoryRunning = true
                            try {
                                if (RFIDController.mConnectedReader.Events != null) {
                                    RFIDController.mConnectedReader.Events.addEventsListener(
                                        rfidEventsListener
                                    )
                                }
                                //MainActivity.updateReaderConnection(false);
                                RFIDController.mConnectedReader.Events.setBatchModeEvent(true)
                                RFIDController.mConnectedReader.Events.setReaderDisconnectEvent(true)
                                RFIDController.mConnectedReader.Events.setBatteryEvent(true)
                                RFIDController.mConnectedReader.Events.setInventoryStopEvent(true)
                                RFIDController.mConnectedReader.Events.setInventoryStartEvent(true)
                                RFIDController.mConnectedReader.Events.setTagReadEvent(true)
                                RFIDController.mConnectedReader.Events.setWPAEvent(true)
                                RFIDController.mConnectedReader.Events.setScanDataEvent(true)
                            } catch (e: InvalidUsageException) {
                                if (e != null && e.getStackTrace().size > 0) {
                                    Log.e(TAG, e.getStackTrace().get(0).toString())
                                }
                            } catch (e: OperationFailureException) {
                                if (e != null && e.getStackTrace().size > 0) {
                                    Log.e(TAG, e.getStackTrace().get(0).toString())
                                }
                            } catch (e: NullPointerException) {
                                Log.d(RFIDController.TAG, "null pointer ")
                                if (e != null && e.stackTrace.size > 0) {
                                    Log.e(TAG, e.stackTrace[0].toString())
                                }
                            }
                        } else {
                            try {
                                RFIDController.mConnectedReader.disconnect()
                            } catch (e: InvalidUsageException) {
                                if (e != null && e.getStackTrace().size > 0) {
                                    Log.e(TAG, e.getStackTrace().get(0).toString())
                                }
                            } catch (e: OperationFailureException) {
                                if (e != null && e.getStackTrace().size > 0) {
                                    Log.e(TAG, e.getStackTrace().get(0).toString())
                                }
                            }
                            RFIDController.mConnectedReader = null
                            RFIDController.mConnectedDevice = null
                        }
                        rfidListeners.onFailure(exception)
                    } else {
                        rfidListeners.onSuccess(null)
                    }
                } else {
                    rfidListeners.onFailure("Device is not paired")
                }
                RFIDController.autoConnectDeviceTask = null
                //contextSettingDetails = null;
            }
        }.execute()
    }


    @Throws(InvalidUsageException::class, OperationFailureException::class)
    fun updateReaderConnection(fullUpdate: Boolean) {
        if (RFIDController.mConnectedReader == null) return
        val ngeErrorLogState = RFIDController.mConnectedReader.Logger.getLogConfig(NGEERRORLOGSTATE)
        val ngeLogState = RFIDController.mConnectedReader.Logger.getLogConfig(NGEPACKETLOGSTATE)
        RFIDController.mConnectedReader.Actions.enableNGEErrorLogs(
            "ON".equals(
                ngeErrorLogState,
                ignoreCase = true
            )
        )
        RFIDController.mConnectedReader.Actions.enableNGELogs(
            "ON".equals(
                ngeLogState,
                ignoreCase = true
            )
        )
        if (fullUpdate) RFIDController.mConnectedReader.PostConnectReaderUpdate()
        RFIDController.mConnectedReader.Events.setBatchModeEvent(true)
        RFIDController.mConnectedReader.Events.setReaderDisconnectEvent(true)
        RFIDController.mConnectedReader.Events.setInventoryStartEvent(true)
        RFIDController.mConnectedReader.Events.setInventoryStopEvent(true)
        RFIDController.mConnectedReader.Events.setTagReadEvent(true)
        RFIDController.mConnectedReader.Events.setHandheldEvent(true)
        RFIDController.mConnectedReader.Events.setBatteryEvent(true)
        RFIDController.mConnectedReader.Events.setPowerEvent(true)
        RFIDController.mConnectedReader.Events.setOperationEndSummaryEvent(true)
        RFIDController.mConnectedReader.Events.setWPAEvent(true)
        RFIDController.mConnectedReader.Events.setScanDataEvent(true)
        RFIDController.mConnectedReader.Events.setFirmwareUpdateEvent(true)
        RFIDController.regulatory = RFIDController.mConnectedReader.Config.regulatoryConfig
        RFIDController.regionNotSet = false
        RFIDController.rfModeTable =
            RFIDController.mConnectedReader.ReaderCapabilities.RFModes.getRFModeTableInfo(0)
        LinkProfileUtil.getInstance().populateLinkeProfiles()
        //
        LoadProfileToReader()
        val modelName = RFIDController.mConnectedDevice.name
        val model = RFIDController.mConnectedDevice.getDeviceCapability(modelName)
        if ("RE40".equals(model, ignoreCase = true)) {
            val contentRE40: ProfileFragmentRE40 = ProfileFragmentRE40()
            contentRE40.saveDataToAntenna()
        }
        //
        RFIDController.antennaRfConfig =
            RFIDController.mConnectedReader.Config.Antennas.getAntennaRfConfig(1)
        RFIDController.singulationControl =
            RFIDController.mConnectedReader.Config.Antennas.getSingulationControl(1)
        RFIDController.settings_startTrigger = RFIDController.mConnectedReader.Config.startTrigger
        RFIDController.settings_stopTrigger = RFIDController.mConnectedReader.Config.stopTrigger
        RFIDController.tagStorageSettings =
            RFIDController.mConnectedReader.Config.tagStorageSettings

        if (RFIDController.mConnectedReader.hostName.startsWith("RFD8500")
            || RFIDController.mConnectedReader.hostName.startsWith("RFD40P")
            || RFIDController.mConnectedReader.hostName.startsWith("RFD40+")
            || RFIDController.mConnectedReader.hostName.startsWith("RFD90")
        ) {
            // beeperVolume = BEEPER_VOLUME.QUIET_BEEP;
            RFIDController.dynamicPowerSettings = RFIDController.mConnectedReader.Config.dpoState
            RFIDController.sledBeeperVolume = RFIDController.mConnectedReader.Config.beeperVolume
            RFIDController.batchMode = RFIDController.mConnectedReader.Config.batchModeConfig.value
            RFIDController.mConnectedReader.Config.setTriggerMode(
                ENUM_TRIGGER_MODE.RFID_MODE,
                false
            )
            if (RFIDController.mConnectedReader.hostName.startsWith("RFD8500") != true) RFIDController.usbBatchMode =
                RFIDController.mConnectedReader.Config.usbBatchModeConfig.value
        }
        if (RFIDController.mConnectedReader.transport == "SERVICE_USB") {
            RFIDController.sledBeeperVolume = RFIDController.mConnectedReader.Config.beeperVolume
            RFIDController.batchMode = RFIDController.mConnectedReader.Config.batchModeConfig.value
            RFIDController.usbBatchMode =
                RFIDController.mConnectedReader.Config.usbBatchModeConfig.value
        }
        if (RFIDController.mConnectedReader.hostName.startsWith("RFD40+")
            || RFIDController.mConnectedReader.hostName.startsWith("RFD90")
        ) {
            RFIDController.scanBatchMode =
                RFIDController.mConnectedReader.Config.scanBatchModeConfig.value
        }

        RFIDController.reportUniquetags = RFIDController.mConnectedReader.Config.uniqueTagReport
        RFIDController.mConnectedReader.Config.getDeviceVersionInfo(Application.versionInfo)

        RFIDController.mConnectedReader.Config.setLedBlinkEnable(RFIDController.ledState)
        RFIDController.mConnectedReader.Config.getDeviceStatus(true, false, false)

        //BatteryStatistics batStats = RFIDController.mConnectedReader.Config.getBatteryStats();

        //
        if (RFIDController.ActiveProfile.content.equals("Reader Defined")) {
            UpdateActiveProfile()
        }
        var sdkversion: VersionInfo? = null
        if (RFIDController.mConnectedReader != null) sdkversion =
            RFIDController.mConnectedReader.versionInfo()


        //com.zebra.rfid.api3.BuildConfig.VERSION_NAME
        Log.d("DEMOAPP", "SDK version " + BuildConfig.VERSION_NAME)
        RFIDController.startTimer()
    }

    fun LoadProfileToReader() {
        // update profiles based on reader type
        UpdateProfilesForRegulatory()
        if (!RFIDController.ActiveProfile.content.equals("Reader Defined")) {
            try {
                for (i in 1..RFIDController.mConnectedReader.ReaderCapabilities.numAntennaSupported) {
                    // Antenna
                    var antennaRfConfigLocal: AntennaRfConfig =
                        RFIDController.mConnectedReader.Config.Antennas.getAntennaRfConfig(i.toInt())
                    antennaRfConfigLocal.setTransmitPowerIndex(RFIDController.ActiveProfile.powerLevel)
                    antennaRfConfigLocal.setrfModeTableIndex( /*LinkProfileUtil.getInstance().getSimpleProfileModeIndex*/
                        (RFIDController.ActiveProfile.LinkProfileIndex)
                    )
                    RFIDController.mConnectedReader.Config.Antennas.setAntennaRfConfig(
                        i.toInt(),
                        antennaRfConfigLocal
                    )
                    RFIDController.antennaRfConfig = antennaRfConfigLocal

                    // Singulation
                    var singulationControlLocal: SingulationControl =
                        RFIDController.mConnectedReader.Config.Antennas.getSingulationControl(i.toInt())
                    singulationControlLocal.setSession(SESSION.GetSession(RFIDController.ActiveProfile.SessionIndex))
                    if (RFIDController.ActiveProfile.id.equals("0")) singulationControlLocal.Action.setInventoryState(
                        INVENTORY_STATE.INVENTORY_STATE_AB_FLIP
                    )
                    else if (!RFIDController.ActiveProfile.id.equals("5")) singulationControlLocal.Action.setInventoryState(
                        INVENTORY_STATE.INVENTORY_STATE_A
                    )
                    // honour the prefilter over profile
                    if (RFIDController.mConnectedReader.Actions.PreFilters.length() > 0) {
                        val prefilter1: PreFilter? =
                            RFIDController.mConnectedReader.Actions.PreFilters.getPreFilter(0)
                        if (prefilter1 != null) {
                            if (RFIDController.NON_MATCHING) singulationControlLocal.Action.setInventoryState(
                                INVENTORY_STATE.INVENTORY_STATE_A
                            )
                            else singulationControlLocal.Action.setInventoryState(INVENTORY_STATE.INVENTORY_STATE_B)
                        }
                    }
                    RFIDController.mConnectedReader.Config.Antennas.setSingulationControl(
                        i.toInt(),
                        singulationControlLocal
                    )
                    RFIDController.singulationControl = singulationControlLocal
                }
                // DPO
                if (RFIDController.ActiveProfile.DPO_On) RFIDController.mConnectedReader.Config.dpoState =
                    DYNAMIC_POWER_OPTIMIZATION.ENABLE
                else RFIDController.mConnectedReader.Config.dpoState =
                    DYNAMIC_POWER_OPTIMIZATION.DISABLE
                RFIDController.dynamicPowerSettings =
                    RFIDController.mConnectedReader.Config.dpoState
            } catch (e: InvalidUsageException) {
            } catch (e: OperationFailureException) {
                if (e != null && e.getStackTrace().size > 0) {
                    Log.e(TAG, e.getStackTrace().get(0).toString())
                }
            }
        }
    }

    @Throws(InvalidUsageException::class)
    fun getConnectedDeviceFromRFIDReaderList(deviceName: String): ReaderDevice? {
        val readersListArray: ArrayList<ReaderDevice> =
            RFIDController.readers.GetAvailableRFIDReaderList()
        if (readersListArray.size == 1) {
            return readersListArray[0]
        } else {
            for (prevreader in readersListArray.indices.reversed()) {
                if (readersListArray[prevreader].getName() == deviceName) {
                    return readersListArray[prevreader]
                }
            }
        }
        return null
    }

    companion object {
        private const val TAG = "ConnectionController"

        @JvmStatic
        fun operationHasAborted(rfidListeners: RfidListeners) {
            if (RFIDController.isBatchModeInventoryRunning != null && RFIDController.isBatchModeInventoryRunning) {
                if (RFIDController.isInventoryAborted) {
                    RFIDController.isBatchModeInventoryRunning = false
                    RFIDController.isGettingTags = true
                    if (RFIDController.settings_startTrigger == null) {
                        object : AsyncTask<Void?, Void?, Boolean?>() {
                            protected override fun doInBackground(vararg voids: Void): Boolean? {
                                try {
                                    // execute following code after STOP is finished
                                    synchronized(RFIDController.isInventoryAborted) {
                                        (RFIDController.isInventoryAborted as Object).wait(50)
                                    }
                                    if (RFIDController.mConnectedReader == null) {
                                        RFIDController.getInstance().updateReaderConnection(true)
                                        RFIDController.getTagReportingFields()
                                        RFIDController.clearInventoryData()
                                        return null
                                    }
                                    if (RFIDController.mConnectedReader.isCapabilitiesReceived) {
                                        RFIDController.getInstance().updateReaderConnection(false)
                                    } else {
                                        RFIDController.getInstance().updateReaderConnection(true)
                                    }
                                    RFIDController.getTagReportingFields()
                                    RFIDController.mConnectedReader.Actions.batchedTags
                                } catch (e: InvalidUsageException) {
                                    if (e != null && e.getStackTrace().size > 0) {
                                        Log.e(TAG, e.getStackTrace().get(0).toString())
                                    }
                                } catch (e: OperationFailureException) {
                                    if (e != null && e.getStackTrace().size > 0) {
                                        Log.e(TAG, e.getStackTrace().get(0).toString())
                                    }
                                    Log.d(
                                        RFIDController.TAG,
                                        "OpFailEx " + e.getVendorMessage() + " " + e.getResults() + " " + e.getStatusDescription()
                                    )
                                } catch (e: InterruptedException) {
                                    if (e != null && e.stackTrace.size > 0) {
                                        Log.e(TAG, e.stackTrace[0].toString())
                                    }
                                }
                                return null
                            }

                            override fun onPostExecute(aBoolean: Boolean?) {
                                super.onPostExecute(aBoolean)
                                rfidListeners.onSuccess(null)
                            }
                        }.execute()
                    } else {
                        RFIDController.mConnectedReader.Actions.batchedTags
                        rfidListeners.onSuccess(null)
                    }
                } else rfidListeners.onSuccess(null)
            } else rfidListeners.onSuccess(null)
        }
    }
}

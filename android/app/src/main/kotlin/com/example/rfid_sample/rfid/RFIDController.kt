package com.example.rfid_sample.rfid

import android.bluetooth.BluetoothDevice
import android.media.ToneGenerator
import android.os.AsyncTask
import android.util.Log
import com.zebra.demo.application.Application
import com.zebra.rfid.api3.AntennaInfo
import com.zebra.rfid.api3.TAG_FIELD
import com.zebra.rfid.api3.TagStorageSettings
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Created by Kishor on 12/31/2015.
 */
class RFIDController {
    private val accessOperationController = AccessOperationController()
    private val inventoryController = InventoryController()
    private val connectionController = ConnectionController()
    private val locationingController = LocationingController()
    var selectedAntennas: ShortArray? = null
    fun AutoConnectDevice(
        password: String?,
        rfidEventsListener: RfidEventsListener?,
        rfidListeners: RfidListeners?,
        updateUIListener: UpdateUIListener?
    ) {
        connectionController.AutoConnectDevice(
            password,
            rfidEventsListener,
            rfidListeners!!,
            updateUIListener!!
        )
    }

    fun inventoryWithMemoryBank(memoryBankID: String?, rfidListeners: RfidListeners?) {
        val antennaInfo = AntennaInfo(selectedAntennas)

        inventoryController.inventoryWithMemoryBank(memoryBankID, rfidListeners!!, antennaInfo)
    }

    fun performInventory(rfidListeners: RfidListeners?) {
        val antennaInfo = AntennaInfo(selectedAntennas)
        inventoryController.performInventory(rfidListeners!!, antennaInfo)
    }

    fun inventoryWithTamperfind(memoryBankID: String?, rfidListeners: RfidListeners?) {
        val antennaInfo = AntennaInfo(selectedAntennas)
        inventoryController.inventoryWithTamperfind(memoryBankID, rfidListeners!!, antennaInfo)
    }

    fun stopInventory(rfidListeners: RfidListeners?) {
        inventoryController.stopInventory(rfidListeners!!)
    }

    fun updateTagIDs() {
        inventoryController.updateTagIDs()
    }

    fun locationing(locateTag: String?, rfidListeners: RfidListeners?) {
        locationingController.locationing(locateTag, rfidListeners!!)
    }

    @Throws(InvalidUsageException::class, OperationFailureException::class)
    fun updateReaderConnection(fullUpdate: Boolean?) {
        connectionController.updateReaderConnection(fullUpdate!!)
    }

    fun accessOperationsRead(
        tagValue: String?,
        offsetText: String?,
        lengthText: String?,
        accessRwPassword: String?,
        bankItem: String?,
        rfidListeners: RfidListeners?
    ) {
        val antennaInfo = AntennaInfo(selectedAntennas)
        accessOperationController.accessOperationsRead(
            tagValue!!,
            offsetText!!,
            lengthText!!,
            accessRwPassword!!,
            bankItem!!,
            rfidListeners!!,
            antennaInfo
        )
    }

    fun accessOperationsWrite(
        tagValue: String?,
        offsetText: String?,
        lengthText: String?,
        accessRWData: String?,
        accessRwPassword: String?,
        bankItem: String?,
        rfidListeners: RfidListeners?
    ) {
        val antennaInfo = AntennaInfo(selectedAntennas)
        accessOperationController.accessOperationsWrite(
            tagValue!!,
            offsetText!!,
            lengthText,
            accessRWData!!,
            accessRwPassword!!,
            bankItem!!,
            rfidListeners!!,
            antennaInfo
        )
    }

    fun accessOperationLock(
        tagId: String?,
        accessRwPassword: String?,
        lockDataField: LOCK_DATA_FIELD?,
        lockPrivilege: LOCK_PRIVILEGE?,
        ALL_Memory_Bank: Boolean,
        rfidListeners: RfidListeners?
    ) {
        val antennaInfo = AntennaInfo(selectedAntennas)
        accessOperationController.accessOperationLock(
            tagId,
            accessRwPassword!!,
            lockDataField,
            lockPrivilege,
            ALL_Memory_Bank,
            rfidListeners!!,
            antennaInfo
        )
    }

    fun accessOperationsKill(
        tagId: String?,
        accessRWpassword: String?,
        rfidListeners: RfidListeners?
    ) {
        val antennaInfo = AntennaInfo(selectedAntennas)
        accessOperationController.accessOperationsKill(
            tagId!!,
            accessRWpassword!!,
            rfidListeners!!,
            antennaInfo
        )
    }

    fun setAccessProfile(bSet: Boolean) {
        accessOperationController.setAccessProfile(bSet)
    }

    companion object {
        var mConnectedReader: RFIDReader? = null
        var mConnectedDevice: ReaderDevice? = null
        var readersList: ArrayList<ReaderDevice> = ArrayList<ReaderDevice>()

        //public static ArrayList<ReaderBTDevice> btReadrsList;
        //Boolean to keep track of whether the inventory is running or not
        @Volatile
        var mIsInventoryRunning: Boolean = false

        @Volatile
        var mInventoryStartPending: Boolean = false
        var inventoryMode: Int = 0
        var isBatchModeInventoryRunning: Boolean? = false
        var accessControlTag: String? = null

        //Variable to maintain the RR started time to maintain the read rate
        @Volatile
        var mRRStartedTime: Long = 0
        var preFilters: Array<PreFilters?>? = null
        var isAccessCriteriaRead: Boolean = false
        var preFilterIndex: Int = -1

        //For Notification
        @Volatile
        var INTENT_ID: Int = 100

        var tagListMatchAutoStop: Boolean = true
        var tagListMatchNotice: Boolean = false

        var isGettingTags: Boolean = false
        var EXPORT_DATA: Boolean = false

        var BTDevice: BluetoothDevice? = null
        var isLocatingTag: Boolean = false
        var currentLocatingTag: String? = null

        var importFileName: String = ""

        var settings_startTrigger: StartTrigger? = null
        var settings_stopTrigger: StopTrigger? = null
        var TagProximityPercent: Short = -1
        var tagStorageSettings: TagStorageSettings? = null
        var batchMode: Int = 0
        var usbBatchMode: Int = 0
        var scanBatchMode: Int = 0
        var BatteryData: BatteryData? = null
        var dynamicPowerSettings: DYNAMIC_POWER_OPTIMIZATION? = null
        var is_disconnection_requested: Boolean = false
        var is_connection_requested: Boolean = false

        //RFIDController Settings
        @Volatile
        var AUTO_DETECT_READERS: Boolean = false

        @Volatile
        var AUTO_RECONNECT_READERS: Boolean = false

        @Volatile
        var NOTIFY_READER_AVAILABLE: Boolean = false

        @Volatile
        var NOTIFY_READER_CONNECTION: Boolean = false

        @Volatile
        var NOTIFY_BATTERY_STATUS: Boolean = false
        var LAST_CONNECTED_READER: String = ""

        //Beeper
        var beeperVolume: BEEPER_VOLUME = BEEPER_VOLUME.HIGH_BEEP
        var sledBeeperVolume: BEEPER_VOLUME = BEEPER_VOLUME.HIGH_BEEP

        // Singulation control
        var singulationControl: SingulationControl? = null

        // Endpoint Config
        var endpointConfiguration: EndpointConfigurationInfo? = null

        // regulatory
        var regulatory: RegulatoryConfig? = null
        var regionNotSet: Boolean = false

        // antenna
        var rfModeTable: RFModeTable? = null
        var antennaRfConfig: AntennaRfConfig? = null
        var antennaPowerLevel: IntArray?
        var readers: Readers? = null

        var settingsactivityResumed: Boolean = false
        var mReaderDisappeared: ReaderDevice? = null
        var toneGenerator: ToneGenerator? = null

        //public static Activity contextSettingDetails = null;
        var currentFragment: String =
            "" //for MTC export data, when curr frag is rr it should export as previously.
        var SHOW_CSV_TAG_NAMES: Boolean = false
        var asciiMode: Boolean = false
        var sgtinMode: Boolean = false
        var ActiveProfile: ProfileContent.ProfilesItem? = null
        var PreFilterTagID: String? = null
        var NON_MATCHING: Boolean = false


        var reportUniquetags: UNIQUE_TAG_REPORT_SETTING? = null
        var ledState: Boolean = false
        var beeperspinner_status: Int = 0
        var TAG: String = "RFIDDEMO"

        var wifiState: Boolean = false

        var brandidcheckenabled: Boolean = false

        //   public static boolean iLogo1, iLogo2, iLogo3, iLogo4, iIgnore = false;
        var strCurrentImage: String = ""

        // public static Bitmap logoBitmap1, logoBitmap2, logoBitmap3, logoBitmap4;
        var bFound: Boolean = false

        //From MainActivity
        var isInventoryAborted: Boolean? = null
        var isTriggerRepeat: Boolean? = null
        var pc: Boolean = false
        var rssi: Boolean = false
        var phase: Boolean = false
        var channelIndex: Boolean = false
        var tagSeenCount: Boolean = false
        var isLocationingAborted: Boolean = false

        //public static boolean isPreFilterSimpleEnabled;
        //public static boolean isPreFilterAdvanceEnabled;
        var autoConnectDeviceTask: AsyncTask<Void, Void, Boolean>? = null

        @Volatile
        var instance: RFIDController? = null
            get() {
                var result = field
                if (result == null) {
                    synchronized(mutex) {
                        result = field
                        if (result == null) {
                            result = RFIDController()
                            field = result
                        }
                    }
                }
                return result
            }
            private set
        private val mutex = Any()

        var editEndpointPos: Int = -1
        var addEndpointPos: Int = -1


        val isPrefilterEnabled: Boolean
            get() {
                if (mConnectedReader == null || mConnectedReader.Actions == null || mConnectedReader.Actions.PreFilters.length() == 0) return false
                try {
                    return mConnectedReader.Actions.PreFilters.getPreFilter(0) != null
                } catch (e: InvalidUsageException) {
                    Log.d(TAG, "Returned SDK Exception")
                }
                if (mConnectedReader == null || mConnectedReader.Actions.PreFilters.length() <= 1) return false
                return false
            }

        val isIsPreFilterAdvanceEnabled: Boolean
            get() {
                if (mConnectedReader == null || mConnectedReader.Actions.PreFilters.length() <= 1) return false

                return true
            }


        val isSimplePreFilterEnabled: Boolean
            get() {
                if (mConnectedReader == null) return false

                if (mConnectedReader.Actions.PreFilters.length() == 0) return false

                try {
                    preFilters = arrayOfNulls<PreFilters>(2)
                    preFilters!![0] =
                        PreFilters(mConnectedReader.Actions.PreFilters.getPreFilter(0))
                    preFilters!![1] = null
                    preFilters!![1] =
                        PreFilters(mConnectedReader.Actions.PreFilters.getPreFilter(1))
                } catch (e: InvalidUsageException) {
                    Log.d(TAG, "Returned SDK Exception")
                }
                if (preFilters!![0].getMemoryBank()
                        .contains("EPC") && preFilters!![0].getAction() === 4 && preFilters!![1] == null
                ) return true

                return false
            }

        val tagReportingFields: Unit
            get() {
                pc = false
                phase = false
                channelIndex = false
                rssi = false
                if (tagStorageSettings != null) {
                    val tag_field = tagStorageSettings!!.tagFields
                    for (idx in tag_field.indices) {
                        if (tag_field[idx] === TAG_FIELD.PEAK_RSSI) rssi = true
                        if (tag_field[idx] === TAG_FIELD.PHASE_INFO) phase = true
                        if (tag_field[idx] === TAG_FIELD.PC) pc = true
                        if (tag_field[idx] === TAG_FIELD.CHANNEL_INDEX) channelIndex = true
                        if (tag_field[idx] === TAG_FIELD.TAG_SEEN_COUNT) tagSeenCount = true
                    }
                }
            }

        val repeatTriggers: Boolean
            get() = if ((settings_startTrigger != null && (settings_startTrigger.getTriggerType() === START_TRIGGER_TYPE.START_TRIGGER_TYPE_HANDHELD || settings_startTrigger.getTriggerType() === START_TRIGGER_TYPE.START_TRIGGER_TYPE_PERIODIC))
                || (isTriggerRepeat != null && isTriggerRepeat!!)
            ) true
            else false


        /**
         * method to start a timer task to get device battery status per every 60 seconds
         */
        private var scheduler: ScheduledExecutorService? = null
        private var taskHandle: ScheduledFuture<*>? = null

        fun startTimer() {
            if (scheduler == null) {
                scheduler = Executors.newScheduledThreadPool(1)
                val task = Runnable {
                    try {
                        if (mConnectedReader != null) mConnectedReader.Config.getDeviceStatus(
                            true,
                            true,
                            false
                        )
                        else stopTimer()
                    } catch (e: InvalidUsageException) {
                        Log.d(TAG, "Returned SDK Exception")
                    } catch (e: OperationFailureException) {
                        Log.d(TAG, "Returned SDK Exception")
                    }
                }
                taskHandle = scheduler.scheduleAtFixedRate(task, 0, 30, TimeUnit.SECONDS)
            }
        }

        /**
         * method to stop timer
         */
        fun stopTimer() {
            if (taskHandle != null) {
                taskHandle!!.cancel(true)
                scheduler!!.shutdown()
            }
            taskHandle = null
            scheduler = null
        }


        fun clearInventoryData() {
            if (!ActiveProfile.id.equals("1")) clearAllInventoryData()
        }

        fun clearAllInventoryData() {
            Application.TOTAL_TAGS = 0
            mRRStartedTime = 0
            Application.UNIQUE_TAGS = 0
            Application.UNIQUE_TAGS_CSV = 0
            Application.TAG_READ_RATE = 0
            Application.TAG_LIST_LOADED = false
            if (!(isBatchModeInventoryRunning != null && isBatchModeInventoryRunning!!)) {
                Application.missedTags = 0
                Application.matchingTags = 0
            }
            currentFragment = ""
            if (Application.tagIDs != null) Application.tagIDs.clear()
            if (Application.tagsReadInventory.size() > 0) Application.tagsReadInventory.clear()
            if (Application.inventoryList != null && Application.inventoryList.size() > 0) Application.inventoryList.clear()
            if (Application.TAG_LIST_MATCH_MODE) {
                Application.matchingTagsList.clear()
                Application.missingTagsList.clear()
                Application.unknownTagsList.clear()
                Application.tagsReadForSearch.clear()
            }
        }


        /**
         * method to clear reader's settings on disconnection
         */
        fun clearSettings() {
            antennaPowerLevel = null
            antennaRfConfig = null
            singulationControl = null
            rfModeTable = null
            regulatory = null
            batchMode = -1
            tagStorageSettings = null
            reportUniquetags = null
            dynamicPowerSettings = null
            settings_startTrigger = null
            settings_stopTrigger = null
            //beeperVolume = BEEPER_VOLUME.HIGH_BEEP;
            preFilters = null
            if (Application.versionInfo != null) Application.versionInfo.clear()
            regionNotSet = false
            isBatchModeInventoryRunning = null
            BatteryData = null
            is_disconnection_requested = false
            mConnectedDevice = null
            mConnectedReader = null
        }

        //clear saved data
        fun reset() {
            Application.UNIQUE_TAGS = 0
            Application.UNIQUE_TAGS_CSV = 0
            Application.TOTAL_TAGS = 0
            Application.TAG_READ_RATE = 0
            mRRStartedTime = 0
            Application.missedTags = 0
            Application.matchingTags = 0
            if (Application.tagsReadInventory != null) Application.tagsReadInventory.clear()
            if (Application.tagIDs != null) Application.tagIDs.clear()
            if (Application.TAG_LIST_MATCH_MODE) {
                Application.matchingTagsList.clear()
                Application.missingTagsList.clear()
                Application.unknownTagsList.clear()
                Application.tagsReadForSearch.clear()
            }
            mIsInventoryRunning = false
            inventoryMode = 0
            Application.memoryBankId = -1
            if (Application.inventoryList != null) Application.inventoryList.clear()
            mConnectedDevice = null
            INTENT_ID = 100
            antennaPowerLevel = null
            //Triggers
            settings_startTrigger = null
            settings_startTrigger = null
            //Beeper
            beeperVolume = BEEPER_VOLUME.HIGH_BEEP
            accessControlTag = null
            isAccessCriteriaRead = false
            // reader settings
            regulatory = null
            regionNotSet = false
            preFilters = null
            preFilterIndex = -1
            Application.PreFilterTag = ""
            PreFilterTagID = ""
            settings_startTrigger = null
            settings_stopTrigger = null
            if (Application.versionInfo != null) Application.versionInfo.clear()
            BatteryData = null
            isLocatingTag = false
            TagProximityPercent = -1
            Application.locateTag = null
            is_disconnection_requested = false
            is_connection_requested = false
            readers = null
            Application.mIsMultiTagLocatingRunning = false
        }
    }
}
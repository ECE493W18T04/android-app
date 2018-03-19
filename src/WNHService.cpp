#include "WNHService.h"
#include <Gap.h>

#define PAIRING_TIMEOUT_MS 20000 // 20s

const char*    WNHService::DEVICE_NAME = "WNH";
const uint16_t WNHService::uuid16_list[] = {WNHService::WNH_SERVICE_UUID};

WNHService::WNHService(BLEDevice &_ble, EventQueue &_eventQueue) :
    voiceControl(VOICE_CONTROL_DEFAULT),
    currentTime(CURRENT_TIME_INVALID),
    stateOverride(STATE_OVERRIDE_INVALID),
    disconnect(DISCONNECT_DEFAULT),

    clockPriority(CLOCK_PRIORITY_DEFAULT),
    mapsPriority(MAPS_PRIORITY_DEFAULT),
    callPriority(CALL_PRIORITY_DEFAULT),
    musicPriority(MUSIC_PRIORITY_DEFAULT),
    speedPriority(SPEED_PRIORITY_DEFAULT),
    fuelPriority(FUEL_PRIORITY_DEFAULT),

    mapsStreetName(MAPS_STREET_NAME_DEFAULT),
    musicSongName(MUSIC_SONG_NAME_DEFAULT),
    callName(CALL_NAME_DEFAULT),

    mapsDistanceNumber(MAPS_DISTANCE_VALUE_DEFAULT),
    speedNumber(SPEED_VALUE_DEFAULT),
    fuelNumber(FUEL_VALUE_DEFAULT),
    brightnessNumber(BRIGHTNESS_VALUE_DEFAULT),
    hueColorNumber(HUE_COLOR_VALUE_DEFAULT),
    satColorNumber(SAT_COLOR_VALUE_DEFAULT),
    maxCurrent(MAX_CURRENT_VALUE_DEFAULT),

    mapsDirection(MAPS_DIRECTION_ENUM_DEFAULT),
    mapsUnits(MAPS_UNITS_ENUM_DEFAULT),
    speedUnits(SPEED_UNITS_ENUM_DEFAULT),
    signalStatus(SIGNAL_STATUS_ENUM_DEFAULT),
    autoBrightness(AUTO_BRIGHT_ENUM_DEFAULT),
    ble(_ble),
    eventQueue(_eventQueue),
    btnMgr(_eventQueue),
    stateMgr(_eventQueue),

    voiceControlCharacteristic(VOICE_CONTROL_CHARACTERISTIC_UUID, &voiceControl, GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_NOTIFY),
    currentTimeCharacteristic(CURRENT_TIME_CHARACTERISTIC_UUID, &currentTime),
    stateOverrideCharacteristic(STATE_OVERRIDE_CHARACTERISTIC_UUID, &stateOverride),
    disconnectCharacteristic(DISCONNECT_CHARACTERISTIC_UUID, &disconnect),

    clockPriorityCharacteristic(CLOCK_PRIORITY_CHARACTERISTIC_UUID, &clockPriority),
    mapsPriorityCharacteristic(MAPS_PRIORITY_CHARACTERISTIC_UUID, &mapsPriority),
    callPriorityCharacteristic(CALL_NAME_CHARACTERISTIC_UUID, &callPriority),
    musicPriorityCharacteristic(MUSIC_PRIORITY_CHARACTERISTIC_UUID, &musicPriority),
    speedPriorityCharacteristic(SPEED_PRIORITY_CHARACTERISTIC_UUID, &speedPriority),
    fuelPriorityCharacteristic(FUEL_PRIORITY_CHARACTERISTIC_UUID, &fuelPriority),

    mapsStreetNameCharacteristic(MAPS_STREET_CHARACTERISTIC_UUID, ""),
    musicSongNameCharacteristic(MUSIC_SONG_CHARACTERISTIC_UUID, ""),
    callNameCharacteristic(CALL_NAME_CHARACTERISTIC_UUID, ""),

    mapsDistanceNumberCharacteristic(MAPS_DISTANCE_CHARACTERISTIC_UUID, &mapsDistanceNumber),
    speedNumberCharacteristic(SPEED_VALUE_CHARACTERISTIC_UUID, &speedNumber),
    fuelNumberCharacteristic(FUEL_VALUE_CHARACTERISTIC_UUID, &fuelNumber),
    brightnessCharacteristic(BRIGHTNESS_CHARACTERISTIC_UUID, &brightnessNumber),
    hueColorCharacteristic(HUE_COLOR_CHARACTERISTIC_UUID, &hueColorNumber),
    satColorCharacteristic(SAT_COLOR_CHARACTERISTIC_UUID, &satColorNumber),
    maxCurrentCharacteristic(MAX_CURRENT_CHARACTERISTIC_UUID, &maxCurrent),

    mapsDirectionCharacteristic(MAPS_DIRECTION_CHARACTERISTIC_UUID, &mapsDirection),
    mapsUnitsCharacteristic(MAPS_UNITS_CHARACTERISTIC_UUID, &mapsUnits),
    speedUnitsCharacteristic(SPEED_UNITS_CHARACTERISTIC_UUID, &speedUnits),
    signalStatusCharacteristic(SIGNAL_STATUS_CHARACTERISTIC_UUID, &signalStatus),
    autoBrightCharacteristic(AUTO_BRIGHT_CHARACTERISTIC_UUID, &autoBrightness)
{
    // setup initial BLE callbacks
    ble.onEventsToProcess(BLE::OnEventsToProcessCallback_t(this, &WNHService::scheduleBleEventsProcessing));
    ble.init(this, &WNHService::bleInitComplete);

    // setup WNH Service
    GattCharacteristic *charTable[] = {
        &voiceControlCharacteristic,
        &currentTimeCharacteristic,
        &stateOverrideCharacteristic,
        &disconnectCharacteristic,
        &clockPriorityCharacteristic,
        &mapsPriorityCharacteristic,
        &callPriorityCharacteristic,
        &musicPriorityCharacteristic,
        &speedPriorityCharacteristic,
        &fuelPriorityCharacteristic,
        &mapsStreetNameCharacteristic,
        &musicSongNameCharacteristic,
        &callNameCharacteristic,
        &mapsDistanceNumberCharacteristic,
        &speedNumberCharacteristic,
        &fuelNumberCharacteristic,
        &brightnessCharacteristic,
        &hueColorCharacteristic,
        &satColorCharacteristic,
        &maxCurrentCharacteristic,
        &mapsDirectionCharacteristic,
        &mapsUnitsCharacteristic,
        &speedUnitsCharacteristic,
        &signalStatusCharacteristic,
        &maxCurrentCharacteristic
    };
    uint16_t sizeOfGattCharTable = sizeof(charTable) / sizeof(GattCharacteristic *);
    for (int i = 0; i < sizeOfGattCharTable; i++) {
        charTable[i]->requireSecurity(SecurityManager::SECURITY_MODE_ENCRYPTION_WITH_MITM);
    }
    GattService         WNHBLEService(WNH_SERVICE_UUID, charTable, sizeOfGattCharTable);
    ble.gattServer().addService(WNHBLEService);

    // setup handlers
    btnMgr.setPairingHandler(Callback<void()>(this, &WNHService::beginPairingMode));
    btnMgr.setVoiceCommandHandler(Callback<void()>(this, &WNHService::sendVoiceCommandTrigger));
}
void WNHService::scheduleBleEventsProcessing(BLE::OnEventsToProcessCallbackContext* context) {
    BLE &ble = BLE::Instance();
    eventQueue.call(Callback<void()>(&ble, &BLE::processEvents));
}

void WNHService::disconnectionCallback(const Gap::DisconnectionCallbackParams_t *params)
{
    setupGapAdvertising(false);
    printf("Device Disconnected\n");
    // TODO shutdown external hardware
}

void WNHService::onDataWrittenCallback(const GattWriteCallbackParams *params) {
    if ((params->handle == this->currentTimeCharacteristic.getValueHandle()) && (params->len == 4)) {
        currentTime = *(params->data);
    } else if (params->handle == this->disconnectCharacteristic.getValueHandle()) {
        ble.gap().disconnect(Gap::REMOTE_USER_TERMINATED_CONNECTION);
    }
}

void WNHService::beginPairingMode() {
    setupGapAdvertising(true);
    eventQueue.call_in(PAIRING_TIMEOUT_MS, this, &WNHService::pairingModeTimeout);
    // TODO trigger display to show key
}

void WNHService::pairingModeTimeout() {
    // TODO handle pairing timoeut
}

void WNHService::sendVoiceCommandTrigger() {
    voiceControl = !voiceControl;
    BLE::Instance().gattServer().write(voiceControlCharacteristic.getValueHandle(), (uint8_t*)&voiceControl, sizeof(voiceControl));
}

void WNHService::onBleInitError(BLE &ble, ble_error_t error) {
    // TODO set a blink pattern to LED 2
}

void passkeyDisplayCallback(Gap::Handle_t handle, const SecurityManager::Passkey_t passkey)
{
    printf("Input passKey: ");
    for (unsigned i = 0; i < Gap::ADDR_LEN; i++) {
        printf("%c ", passkey[i]);
    }
    printf("\r\n");
}

void securitySetupCompletedCallback(Gap::Handle_t handle, SecurityManager::SecurityCompletionStatus_t status)
{
    if (status == SecurityManager::SEC_STATUS_SUCCESS) {
        printf("Security success\r\n");
    } else {
        printf("Security failed: %d\r\n", status);
    }
}

void WNHService::setupGapAdvertising(bool discoverable) {
    uint8_t flags = GapAdvertisingData::BREDR_NOT_SUPPORTED;
    bool enableBonding = true;
    bool requireMITM   = true;
    // const static SecurityManager::Passkey_t key = {'0', '0', '0', '0', '0', '0'};

    ble.securityManager().init(enableBonding, requireMITM, SecurityManager::IO_CAPS_DISPLAY_ONLY);
    if (discoverable) {
        flags |= GapAdvertisingData::LE_GENERAL_DISCOVERABLE;
    }

    ble.gap().accumulateAdvertisingPayload(flags);
    ble.gap().accumulateAdvertisingPayload(GapAdvertisingData::COMPLETE_LIST_16BIT_SERVICE_IDS, (uint8_t *)uuid16_list, sizeof(uuid16_list));
    ble.gap().accumulateAdvertisingPayload(GapAdvertisingData::COMPLETE_LOCAL_NAME, (uint8_t *)DEVICE_NAME, sizeof(DEVICE_NAME));
    ble.gap().setAdvertisingType(GapAdvertisingParams::ADV_CONNECTABLE_UNDIRECTED);
    ble.gap().setAdvertisingInterval(1000); /* 1000ms. */
    ble.gap().startAdvertising();
    ble.securityManager().onPasskeyDisplay(passkeyDisplayCallback);
    ble.securityManager().onSecuritySetupCompleted(securitySetupCompletedCallback);
}

void WNHService::connectionCallback(const Gap::ConnectionCallbackParams_t *params) {
    printf("Connected\n");
}

void printMacAddress()
{
    /* Print out device MAC address to the console*/
    Gap::AddressType_t addr_type;
    Gap::Address_t address;
    BLE::Instance().gap().getAddress(&addr_type, address);
    printf("DEVICE MAC ADDRESS: ");
    for (int i = 5; i >= 1; i--){
            printf("%02x:", address[i]);
        }
    printf("%02x\r\n", address[0]);
}

/**
 * Callback triggered when the ble initialization process has finished
 */
void WNHService::bleInitComplete(BLE::InitializationCompleteCallbackContext *params)
{
    BLE&        ble   = params->ble;
    ble_error_t error = params->error;

    if (error != BLE_ERROR_NONE) {
        /* In case of error, forward the error handling to onBleInitError */
        onBleInitError(ble, error);
        return;
    }

    /* Ensure that it is the default instance of BLE */
    if(ble.getInstanceID() != BLE::DEFAULT_INSTANCE) {
        return;
    }
    ble.gap().onDisconnection(this, &WNHService::disconnectionCallback);
    ble.gattServer().onDataWritten(this, &WNHService::onDataWrittenCallback);
    ble.gap().onConnection(this, &WNHService::connectionCallback);
    setupGapAdvertising(false);
    printMacAddress();
}

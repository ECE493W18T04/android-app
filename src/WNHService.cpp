#include "WNHService.h"

const char*    WNHService::DEVICE_NAME = "WNH";
const uint16_t WNHService::uuid16_list[] = {WNHService::WNH_SERVICE_UUID};

WNHService::WNHService(BLEDevice &_ble) :
    voiceControl(VOICE_CONTROL_DEFAULT),
    currentTime(CURRENT_TIME_INVALID),
    stateOverride(STATE_OVERRIDE_INVALID),

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

    mapsDirection(MAPS_DIRECTION_ENUM_DEFAULT),
    mapsUnits(MAPS_UNITS_ENUM_DEFAULT),
    speedUnits(SPEED_UNITS_ENUM_DEFAULT),
    signalStatus(SIGNAL_STATUS_ENUM_DEFAULT),
    ble(_ble),

    voiceControlCharacteristic(VOICE_CONTROL_CHARACTERISTIC_UUID, &voiceControl, GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_NOTIFY),
    currentTimeCharacteristic(CURRENT_TIME_CHARACTERISTIC_UUID, &currentTime),
    stateOverrideCharacteristic(STATE_OVERRIDE_CHARACTERISTIC_UUID, &stateOverride),

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

    mapsDirectionCharacteristic(MAPS_DIRECTION_CHARACTERISTIC_UUID, &mapsDirection),
    mapsUnitsCharacteristic(MAPS_UNITS_CHARACTERISTIC_UUID, &mapsUnits),
    speedUnitsCharacteristic(SPEED_UNITS_CHARACTERISTIC_UUID, &speedUnits),
    signalStatusCharacteristic(SIGNAL_STATUS_CHARACTERISTIC_UUID, &signalStatus)
{
    ble.init(this, &WNHService::bleInitComplete);
    GattCharacteristic *charTable[] = {
        &voiceControlCharacteristic,
        &currentTimeCharacteristic,
        &stateOverrideCharacteristic,
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
        &mapsDirectionCharacteristic,
        &mapsUnitsCharacteristic,
        &speedUnitsCharacteristic,
        &signalStatusCharacteristic
    };
    GattService         WNHBLEService(WNH_SERVICE_UUID, charTable, sizeof(charTable) / sizeof(GattCharacteristic *));
    ble.gattServer().addService(WNHBLEService);
    // btnMgr.setPairingHandler(this, &WNHService::beginPairingMode);
    // btnMgr.setVoiceCommandHandler(this, &WNHService::sendVoiceCommandTrigger);
}

void WNHService::disconnectionCallback(const Gap::DisconnectionCallbackParams_t *params)
{
    setupGapAdvertising(false);
    // TODO shutdown external hardware
}

void WNHService::onDataWrittenCallback(const GattWriteCallbackParams *params) {
    if ((params->handle == this->currentTimeCharacteristic.getValueHandle()) && (params->len == 4)) {
        currentTime = *(params->data);
    }
}

void WNHService::beginPairingMode() {
    setupGapAdvertising(true);
    // TODO set time limit
    // TODO trigger display to show key
}

void WNHService::sendVoiceCommandTrigger() {
    voiceControl = !voiceControl;
    // TODO add gap write
    // uint8_t val = (uint8_t)luxDevice.getLux();
    // BLE::Instance().gattServer().write(ledServicePtr->getValueHandle(), &val, 1);
}

void WNHService::onBleInitError(BLE &ble, ble_error_t error) {
    // TODO set a blink pattern to LED 2
}

void WNHService::setupGapAdvertising(bool discoverable) {
    uint8_t flags = GapAdvertisingData::BREDR_NOT_SUPPORTED;
    if (discoverable) {
        flags |= GapAdvertisingData::LE_GENERAL_DISCOVERABLE;
    }
    ble.gap().accumulateAdvertisingPayload(flags);
    ble.gap().accumulateAdvertisingPayload(GapAdvertisingData::COMPLETE_LIST_16BIT_SERVICE_IDS, (uint8_t *)uuid16_list, sizeof(uuid16_list));
    ble.gap().accumulateAdvertisingPayload(GapAdvertisingData::COMPLETE_LOCAL_NAME, (uint8_t *)DEVICE_NAME, sizeof(DEVICE_NAME));
    ble.gap().setAdvertisingType(GapAdvertisingParams::ADV_CONNECTABLE_UNDIRECTED);
    ble.gap().setAdvertisingInterval(1000); /* 1000ms. */
    ble.gap().startAdvertising();
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
    setupGapAdvertising(false);
}

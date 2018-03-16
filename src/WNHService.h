#ifndef __WNH_BLE_SERVICE_H__
#define __WNH_BLE_SERVICE_H__


/* A: Default Values */
#define VOICE_CONTROL_DEFAULT false
#define CURRENT_TIME_INVALID 0
#define STATE_OVERRIDE_INVALID 0xFF

/* B: Default Values */
#define CLOCK_PRIORITY_DEFAULT 15
#define MAPS_PRIORITY_DEFAULT 14

class WNHService {
public:
    /* A: Arbitrary */
    const static uint16_t WNH_SERVICE_UUID                   = 0xA000;
    const static uint16_t VOICE_CONTROL_CHARACTERISTIC_UUID  = 0xA001;
    const static uint16_t CURRENT_TIME_CHARACTERISTIC_UUID   = 0xA002;
    const static uint16_t STATE_OVERRIDE_CHARACTERISTIC_UUID = 0xA003;

    /* B: Priorities (who is Best) */
    const static uint16_t CLOCK_PRIORITY_CHARACTERISTIC_UUID = 0xB000;
    const static uint16_t MAPS_PRIORITY_CHARACTERISTIC_UUID  = 0xB001;
    const static uint16_t CALL_PRIORITY_CHARACTERISTIC_UUID  = 0xB002;
    const static uint16_t MUSIC_PRIORITY_CHARACTERISTIC_UUID = 0xB003;
    const static uint16_t SPEED_PRIORITY_CHARACTERISTIC_UUID = 0xB004;
    const static uint16_t FUEL_PRIORITY_CHARACTERISTIC_UUID  = 0xB005;

    /* C: Char Arrays */
    const static uint16_t MAPS_STREET_CHARACTERISTIC_UUID    = 0xC000;
    const static uint16_t MUSIC_SONG_CHARACTERISTIC_UUID     = 0xC001;
    const static uint16_t CALL_NAME_CHARACTERISTIC_UUID      = 0xC002;

    /* D: Decimal */
    const static uint16_t MAPS_DISTANCE_CHARACTERISTIC_UUID  = 0xD000;
    const static uint16_t SPEED_VALUE_CHARACTERISTIC_UUID    = 0xD001;
    const static uint16_t FUEL_VALUE_CHARACTERISTIC_UUID     = 0xD002;

    /* E: Enums */
    const static uint16_t MAPS_DIRECTION_CHARACTERISTIC_UUID = 0xE000;
    const static uint16_t MAPS_UNITS_CHARACTERISTIC_UUID     = 0xE001;
    const static uint16_t SPEED_UNITS_CHARACTERISTIC_UUID    = 0xE002;
    const static uint16_t SIGNAL_STATUS_CHARACTERISTIC_UUID  = 0xE003;

    WNHService(BLEDevice &_ble) :
        voiceControl(VOICE_CONTROL_DEFAULT),
        ble(_ble),

        voiceControlCharacteristic(VOICE_CONTROL_CHARACTERISTIC_UUID, &voiceControl, GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_NOTIFY),
        currentTimeCharacteristic(CURRENT_TIME_CHARACTERISTIC_UUID, &currentTime),
        stateOverrideCharacteristic(STATE_OVERRIDE_CHARACTERISTIC_UUID, &stateOverride),
        clockPriorityCharacteristic(CLOCK_PRIORITY_CHARACTERISTIC_UUID, &clockPriority),
        mapsPriorityCharacteristic(MAPS_PRIORITY_CHARACTERISTIC_UUID, &mapsPriority)
    {
        ble.init(this, &WNHService::bleInitComplete);
        GattCharacteristic *charTable[] = {&voiceControlCharacteristic, &stateOverrideCharacteristic};
        GattService         WNHBLEService(WNH_SERVICE_UUID, charTable, sizeof(charTable) / sizeof(GattCharacteristic *));
        ble.gattServer().addService(WNHBLEService);
    }

    void bleInitComplete(BLE::InitializationCompleteCallbackContext *params);
    void disconnectionCallback(const Gap::DisconnectionCallbackParams_t *params);
    void onDataWrittenCallback(const GattWriteCallbackParams *params);
private:
    void setupGapAdvertising(bool discoverable);

    bool voiceControl;
    uint32_t currentTime;
    uint8_t stateOverride;

    uint8_t clockPriority;
    uint8_t mapsPriority;

    BLEDevice                         &ble;
    ReadOnlyGattCharacteristic<bool>  voiceControlCharacteristic;
    WriteOnlyGattCharacteristic<uint32_t>  currentTimeCharacteristic;
    WriteOnlyGattCharacteristic<uint8_t>  stateOverrideCharacteristic;

    WriteOnlyGattCharacteristic<uint8_t>  clockPriorityCharacteristic;
    WriteOnlyGattCharacteristic<uint8_t>  mapsPriorityCharacteristic;
    // WriteOnlyGattCharacteristic<uint8_t>  callPriorityCharacteristic;
    // WriteOnlyGattCharacteristic<uint8_t>  musicPriorityCharacteristic;
    // WriteOnlyGattCharacteristic<uint8_t>  speedPriorityCharacteristic;
    // WriteOnlyGattCharacteristic<uint8_t>  fuelPriorityCharacteristic;
    //
    // WriteOnlyArrayGattCharacteristic<char, 256>  mapsStreetNameCharacteristic;
    // WriteOnlyArrayGattCharacteristic<char, 256>  musicSongNameCharacteristic;
    // WriteOnlyArrayGattCharacteristic<char, 256>  callNameCharacteristic;
    //
    // WriteOnlyGattCharacteristic<uint32_t>  mapsDistanceNumberCharacteristic;
    // WriteOnlyGattCharacteristic<uint16_t>  speedNumberCharacteristic;
    // WriteOnlyGattCharacteristic<uint8_t>  fuelNumberCharacteristic;
    //
    // WriteOnlyGattCharacteristic<uint8_t>  mapsDirectionCharacteristic;
    // WriteOnlyGattCharacteristic<uint8_t>  mapsUnitsCharacteristic;
    // WriteOnlyGattCharacteristic<uint8_t>  speedUnitsCharacteristic;
    // WriteOnlyGattCharacteristic<uint8_t>  signalStatusCharacteristic;
};

#endif /* __WNH_BLE_SERVICE_H__ */

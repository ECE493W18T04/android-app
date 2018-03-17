#ifndef __WNH_BLE_SERVICE_H__
#define __WNH_BLE_SERVICE_H__

#include <events/mbed_events.h>
#include "ble/BLE.h"
#include "mbed.h"
#include "ButtonManager.h"

/* A: Default Values */
#define VOICE_CONTROL_DEFAULT       false
#define CURRENT_TIME_INVALID        0
#define STATE_OVERRIDE_INVALID      0xFF

/* B: Default Values */
#define CLOCK_PRIORITY_DEFAULT      15
#define MAPS_PRIORITY_DEFAULT       14
#define CALL_PRIORITY_DEFAULT       13
#define MUSIC_PRIORITY_DEFAULT      12
#define SPEED_PRIORITY_DEFAULT      11
#define FUEL_PRIORITY_DEFAULT       10

/* C: Char arrays */
#define MAPS_STREET_NAME_DEFAULT    ""
#define MUSIC_SONG_NAME_DEFAULT     ""
#define CALL_NAME_DEFAULT           ""

/* D: Decimals */
#define MAPS_DISTANCE_VALUE_DEFAULT 0
#define SPEED_VALUE_DEFAULT         0
#define FUEL_VALUE_DEFAULT          0
#define BRIGHTNESS_VALUE_DEFAULT    50
#define HUE_COLOR_VALUE_DEFAULT     0
#define SAT_COLOR_VALUE_DEFAULT     0
#define MAX_CURRENT_VALUE_DEFAULT   1000

/* E: Enum */
#define MAPS_DIRECTION_ENUM_DEFAULT 0
#define MAPS_UNITS_ENUM_DEFAULT     0
#define SPEED_UNITS_ENUM_DEFAULT    0
#define SIGNAL_STATUS_ENUM_DEFAULT  0
#define AUTO_BRIGHT_ENUM_DEFAULT    1

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
    const static uint16_t BRIGHTNESS_CHARACTERISTIC_UUID     = 0xD003;
    const static uint16_t HUE_COLOR_CHARACTERISTIC_UUID      = 0xD004;
    const static uint16_t SAT_COLOR_CHARACTERISTIC_UUID      = 0xD005;
    const static uint16_t MAX_CURRENT_CHARACTERISTIC_UUID    = 0xD006;

    /* E: Enums */
    const static uint16_t MAPS_DIRECTION_CHARACTERISTIC_UUID = 0xE000;
    const static uint16_t MAPS_UNITS_CHARACTERISTIC_UUID     = 0xE001;
    const static uint16_t SPEED_UNITS_CHARACTERISTIC_UUID    = 0xE002;
    const static uint16_t SIGNAL_STATUS_CHARACTERISTIC_UUID  = 0xE003;
    const static uint16_t AUTO_BRIGHT_CHARACTERISTIC_UUID    = 0xE004;

    /* misc */
    const static char    *DEVICE_NAME;
    const static uint16_t uuid16_list[];

    WNHService(BLEDevice &_ble, EventQueue &_eventQueue);

    void bleInitComplete(BLE::InitializationCompleteCallbackContext *params);
    void disconnectionCallback(const Gap::DisconnectionCallbackParams_t *params);
    void onDataWrittenCallback(const GattWriteCallbackParams *params);
private:
    void setupGapAdvertising(bool discoverable);
    void beginPairingMode();
    void sendVoiceCommandTrigger();
    void onBleInitError(BLE &ble, ble_error_t error);
    void scheduleBleEventsProcessing(BLE::OnEventsToProcessCallbackContext* context);

    bool voiceControl;
    uint32_t currentTime;
    uint8_t stateOverride;

    uint8_t clockPriority;
    uint8_t mapsPriority;
    uint8_t callPriority;
    uint8_t musicPriority;
    uint8_t speedPriority;
    uint8_t fuelPriority;

    char *mapsStreetName;
    char *musicSongName;
    char *callName;

    uint32_t mapsDistanceNumber;
    uint16_t speedNumber;
    uint8_t fuelNumber;
    uint8_t brightnessNumber;
    uint16_t hueColorNumber;
    uint8_t satColorNumber;
    uint16_t maxCurrent;

    uint8_t mapsDirection;
    uint8_t mapsUnits;
    uint8_t speedUnits;
    uint8_t signalStatus;
    uint8_t autoBrightness;

    BLEDevice                         &ble;
    EventQueue                        &eventQueue;
    ButtonManager                     btnMgr;

    ReadOnlyGattCharacteristic<bool>      voiceControlCharacteristic;
    WriteOnlyGattCharacteristic<uint32_t> currentTimeCharacteristic;
    WriteOnlyGattCharacteristic<uint8_t>  stateOverrideCharacteristic;

    WriteOnlyGattCharacteristic<uint8_t>  clockPriorityCharacteristic;
    WriteOnlyGattCharacteristic<uint8_t>  mapsPriorityCharacteristic;
    WriteOnlyGattCharacteristic<uint8_t>  callPriorityCharacteristic;
    WriteOnlyGattCharacteristic<uint8_t>  musicPriorityCharacteristic;
    WriteOnlyGattCharacteristic<uint8_t>  speedPriorityCharacteristic;
    WriteOnlyGattCharacteristic<uint8_t>  fuelPriorityCharacteristic;

    WriteOnlyArrayGattCharacteristic<char, 256>  mapsStreetNameCharacteristic;
    WriteOnlyArrayGattCharacteristic<char, 256>  musicSongNameCharacteristic;
    WriteOnlyArrayGattCharacteristic<char, 256>  callNameCharacteristic;

    WriteOnlyGattCharacteristic<uint32_t>  mapsDistanceNumberCharacteristic;
    WriteOnlyGattCharacteristic<uint16_t>  speedNumberCharacteristic;
    WriteOnlyGattCharacteristic<uint8_t>   fuelNumberCharacteristic;
    WriteOnlyGattCharacteristic<uint8_t>   brightnessCharacteristic;
    WriteOnlyGattCharacteristic<uint16_t>  hueColorCharacteristic;
    WriteOnlyGattCharacteristic<uint8_t>   satColorCharacteristic;
    WriteOnlyGattCharacteristic<uint16_t>  maxCurrentCharacteristic;

    WriteOnlyGattCharacteristic<uint8_t>  mapsDirectionCharacteristic;
    WriteOnlyGattCharacteristic<uint8_t>  mapsUnitsCharacteristic;
    WriteOnlyGattCharacteristic<uint8_t>  speedUnitsCharacteristic;
    WriteOnlyGattCharacteristic<uint8_t>  signalStatusCharacteristic;
    WriteOnlyGattCharacteristic<uint8_t>  autoBrightCharacteristic;
};

#endif /* __WNH_BLE_SERVICE_H__ */

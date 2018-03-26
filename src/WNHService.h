#ifndef __WNH_BLE_SERVICE_H__
#define __WNH_BLE_SERVICE_H__

#include <events/mbed_events.h>
#include "ble/BLE.h"
#include "mbed.h"
#include "ButtonManager.h"
#include "StateManager.h"

/* A: Default Values */
#define VOICE_CONTROL_DEFAULT       false
#define CURRENT_TIME_INVALID        0
#define STATE_OVERRIDE_INVALID      0xFF
#define DISCONNECT_DEFAULT          0

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
    const static uint16_t DISCONNECT_CHARACTERISTIC_UUID     = 0xA004;

    /* B: Priorities (who is Best) */
    const static uint16_t CLOCK_MAPS_PRIORITY_CHARACTERISTIC_UUID = 0xB000;
    const static uint16_t CALL_MUSIC_PRIORITY_CHARACTERISTIC_UUID  = 0xB001;
    const static uint16_t SPEED_FUEL_PRIORITY_CHARACTERISTIC_UUID = 0xB002;

    /* C: Char Arrays */
    const static uint16_t MAPS_STREET_CHARACTERISTIC_UUID    = 0xC000;
    const static uint16_t MUSIC_SONG_CHARACTERISTIC_UUID     = 0xC001;
    const static uint16_t CALL_NAME_CHARACTERISTIC_UUID      = 0xC002;

    /* D: Decimal */
    const static uint16_t MAPS_DISTANCE_CHARACTERISTIC_UUID  = 0xD000;
    const static uint16_t SPEED_VALUE_CHARACTERISTIC_UUID    = 0xD001;
    const static uint16_t FUEL_VALUE_CHARACTERISTIC_UUID     = 0xD002;
    const static uint16_t BRIGHTNESS_CHARACTERISTIC_UUID     = 0xD003;
    const static uint16_t COLOR_CHARACTERISTIC_UUID          = 0xD004;
    const static uint16_t MAX_CURRENT_CHARACTERISTIC_UUID    = 0xD005;

    /* E: Enums */
    const static uint16_t MAPS_DIRECTION_AND_UNITS_CHARACTERISTIC_UUID = 0xE000;
    const static uint16_t SPEED_UNITS_AND_SIGNAL_CHARACTERISTIC_UUID    = 0xE001;
    const static uint16_t AUTO_BRIGHT_CHARACTERISTIC_UUID    = 0xE002;

    /* misc */
    const static char    *DEVICE_NAME;
    const static uint16_t uuid16_list[];

    WNHService(BLEDevice &_ble, EventQueue &_eventQueue);

    void bleInitComplete(BLE::InitializationCompleteCallbackContext *params);
    void disconnectionCallback(const Gap::DisconnectionCallbackParams_t *params);
    void onDataWrittenCallback(const GattWriteCallbackParams *params);
    void connectionCallback(const Gap::ConnectionCallbackParams_t *params);
private:
    void setupGapAdvertising(bool discoverable);
    void beginPairingMode();
    void sendVoiceCommandTrigger();
    void onBleInitError(BLE &ble, ble_error_t error);
    void scheduleBleEventsProcessing(BLE::OnEventsToProcessCallbackContext* context);
    void pairingModeTimeout();

    bool voiceControl;
    uint32_t currentTime;
    uint8_t stateOverride;
    uint8_t disconnect;

    uint8_t clockMapsPriority;
    uint8_t callMusicPriority;
    uint8_t speedFuelPriority;

    char *mapsStreetName;
    char *musicSongName;
    char *callName;

    uint32_t mapsDistanceNumber;
    uint16_t speedNumber;
    uint8_t fuelNumber;
    uint16_t colorNumber;
    uint16_t maxCurrent;

    uint8_t mapsDirectionAndUnits;
    uint8_t speedUnitsAndSignalStatus;
    uint8_t autoBrightness;

    BLEDevice                         &ble;
    EventQueue                        &eventQueue;
    ButtonManager                     btnMgr;
    StateManager                      stateMgr;

    /**
     * Changes to a new value when voice control button is pressed
     */
    ReadOnlyGattCharacteristic<bool>      voiceControlCharacteristic;
    /**
     * Time since 1980 jan 1 12:00am (i.e. unix time)
     */
    WriteOnlyGattCharacteristic<uint32_t> currentTimeCharacteristic;
    /**
     * Write a value for the state enum to override the state machine
     */
    WriteOnlyGattCharacteristic<uint8_t>  stateOverrideCharacteristic;
    /**
     * write an arbitrary value to get the device to initiate a disconnect
     */
    WriteOnlyGattCharacteristic<uint8_t>  disconnectCharacteristic;

    /**
     * MSB 4..7 clock priority
     * 0..3 navigation priority
     */
    WriteOnlyGattCharacteristic<uint8_t>  clockMapsPriorityCharacteristic;
    /**
     * MSB 4..7 call priority
     * 0..3 music priority
     */
    WriteOnlyGattCharacteristic<uint8_t>  callMusicPriorityCharacteristic;
    /**
     * MSB 4..7 speed priority
     * 0..3 fuel priority
     */
    WriteOnlyGattCharacteristic<uint8_t>  speedFuelPriorityCharacteristic;

    /**
     * street name for navigation, max 128 char
     */
    WriteOnlyArrayGattCharacteristic<char, 128>  mapsStreetNameCharacteristic;
    /**
     * song title for music, max 128 char
     */
    WriteOnlyArrayGattCharacteristic<char, 128>  musicSongNameCharacteristic;
    /**
     * name of caller for phone notification
     */
    WriteOnlyArrayGattCharacteristic<char, 128>  callNameCharacteristic;

    /**
     * Maps distance units
     */
    WriteOnlyGattCharacteristic<uint32_t>  mapsDistanceNumberCharacteristic;
    /**
     * speed units
     */
    WriteOnlyGattCharacteristic<uint16_t>  speedNumberCharacteristic;
    /**
     * fuel value (in a percent)
     */
    WriteOnlyGattCharacteristic<uint8_t>   fuelNumberCharacteristic;
    /**
     * color colorCharacteristic
     * MSB 7..15 Hue
     * 0..6 Saturation
     */
    WriteOnlyGattCharacteristic<uint16_t>  colorCharacteristic;
    /**
     * Max current allowed on system
     * Units is mA
     */
    WriteOnlyGattCharacteristic<uint16_t>  maxCurrentCharacteristic;

    /**
     * MSB 4..7 maps dir enum
     * 0..3 maps units enum
     */
    WriteOnlyGattCharacteristic<uint8_t>  mapsDirAndUnitsCharacteristic;
    /**
     * MSB 4..7 speed units enum
     * 0..3 Signal enum
     */
    WriteOnlyGattCharacteristic<uint8_t>  speedUnitsAndSignalCharacteristic;
    /**
     * MSB Auto brightness bit
     * 0..6 Brightness level
     */
    WriteOnlyGattCharacteristic<uint8_t>  autoBrightCharacteristic;
};

#endif /* __WNH_BLE_SERVICE_H__ */

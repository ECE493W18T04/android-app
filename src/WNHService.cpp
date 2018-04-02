#include "WNHService.h"
#include <Gap.h>

#define PAIRING_TIMEOUT_MS 60000 // 60s
#define SAT_MASK 0x7F
#define HUE_SHIFT 7
#define AUTO_BRIGHTNESS_MASK 0x80
#define UPPER_NIBBLE(X) (X >> 4)
#define LOWER_NIBBLE(X) (X & 0x0F)

const char*    WNHService::DEVICE_NAME = "WNH";
const uint16_t WNHService::uuid16_list[] = {WNHService::WNH_SERVICE_UUID};

Callback<void(Gap::Handle_t, const SecurityManager::Passkey_t)> pairingCb;
Callback<void(Gap::Handle_t handle, SecurityManager::SecurityCompletionStatus_t status)> securityCb;

WNHService::WNHService(BLEDevice &_ble, EventQueue &_eventQueue) :
    voiceControl(VOICE_CONTROL_DEFAULT),
    currentTime(CURRENT_TIME_INVALID),
    stateOverride(STATE_OVERRIDE_INVALID),
    disconnect(DISCONNECT_DEFAULT),

    clockMapsPriority(CLOCK_PRIORITY_DEFAULT),
    callMusicPriority(CALL_PRIORITY_DEFAULT),
    speedFuelPriority(SPEED_PRIORITY_DEFAULT),

    mapsStreetName(MAPS_STREET_NAME_DEFAULT),
    musicSongName(MUSIC_SONG_NAME_DEFAULT),
    callName(CALL_NAME_DEFAULT),

    mapsDistanceNumber(MAPS_DISTANCE_VALUE_DEFAULT),
    speedNumber(SPEED_VALUE_DEFAULT),
    fuelNumber(FUEL_VALUE_DEFAULT),
    colorNumber(SAT_COLOR_VALUE_DEFAULT),
    maxCurrent(MAX_CURRENT_VALUE_DEFAULT),

    mapsDirectionAndUnits(MAPS_DIRECTION_ENUM_DEFAULT),
    speedUnitsAndSignalStatus(SPEED_UNITS_ENUM_DEFAULT),
    autoBrightness(AUTO_BRIGHT_ENUM_DEFAULT),

    connected(false),

    ble(_ble),
    eventQueue(_eventQueue),
    btnMgr(_eventQueue),
    stateMgr(_eventQueue),

    voiceControlCharacteristic(VOICE_CONTROL_CHARACTERISTIC_UUID, &voiceControl, GattCharacteristic::BLE_GATT_CHAR_PROPERTIES_NOTIFY),
    currentTimeCharacteristic(CURRENT_TIME_CHARACTERISTIC_UUID, &currentTime),
    stateOverrideCharacteristic(STATE_OVERRIDE_CHARACTERISTIC_UUID, &stateOverride),
    disconnectCharacteristic(DISCONNECT_CHARACTERISTIC_UUID, &disconnect),

    clockMapsPriorityCharacteristic(CLOCK_MAPS_PRIORITY_CHARACTERISTIC_UUID, &clockMapsPriority),
    callMusicPriorityCharacteristic(CALL_MUSIC_PRIORITY_CHARACTERISTIC_UUID, &callMusicPriority),
    speedFuelPriorityCharacteristic(SPEED_FUEL_PRIORITY_CHARACTERISTIC_UUID, &speedFuelPriority),

    mapsStreetNameCharacteristic(MAPS_STREET_CHARACTERISTIC_UUID, ""),
    musicSongNameCharacteristic(MUSIC_SONG_CHARACTERISTIC_UUID, ""),
    callNameCharacteristic(CALL_NAME_CHARACTERISTIC_UUID, ""),

    mapsDistanceNumberCharacteristic(MAPS_DISTANCE_CHARACTERISTIC_UUID, &mapsDistanceNumber),
    speedNumberCharacteristic(SPEED_VALUE_CHARACTERISTIC_UUID, &speedNumber),
    fuelNumberCharacteristic(FUEL_VALUE_CHARACTERISTIC_UUID, &fuelNumber),
    colorCharacteristic(COLOR_CHARACTERISTIC_UUID, &colorNumber),
    maxCurrentCharacteristic(MAX_CURRENT_CHARACTERISTIC_UUID, &maxCurrent),

    mapsDirAndUnitsCharacteristic(MAPS_DIRECTION_AND_UNITS_CHARACTERISTIC_UUID, &mapsDirectionAndUnits),
    speedUnitsAndSignalCharacteristic(SPEED_UNITS_AND_SIGNAL_CHARACTERISTIC_UUID, &speedUnitsAndSignalStatus),
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
        &clockMapsPriorityCharacteristic,
        &callMusicPriorityCharacteristic,
        &speedFuelPriorityCharacteristic,
        &mapsStreetNameCharacteristic,
        &musicSongNameCharacteristic,
        &callNameCharacteristic,
        &mapsDistanceNumberCharacteristic,
        &speedNumberCharacteristic,
        &fuelNumberCharacteristic,
        &colorCharacteristic,
        &maxCurrentCharacteristic,
        &mapsDirAndUnitsCharacteristic,
        &speedUnitsAndSignalCharacteristic,
        &autoBrightCharacteristic
    };
    uint16_t sizeOfGattCharTable = sizeof(charTable) / sizeof(GattCharacteristic *);
    for (int i = 0; i < sizeOfGattCharTable; i++) {
        charTable[i]->requireSecurity(SecurityManager::SECURITY_MODE_ENCRYPTION_WITH_MITM);
    }
    GattService         WNHBLEService(WNH_SERVICE_UUID, charTable, sizeOfGattCharTable);
    ble_error_t err = ble.gattServer().addService(WNHBLEService);
    if (BLE_ERROR_NONE != err) {
        printf("there was an error adding the service: %d\n", err);
    }

    pairingCb.attach(this, &WNHService::passkeyDisplayCallback);
    securityCb.attach(this, &WNHService::securitySetupCompletedCallback);

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
    connected = false;
    stateMgr.powerOff();
}

void WNHService::onDataWrittenCallback(const GattWriteCallbackParams *params) {
    if ((params->handle == this->currentTimeCharacteristic.getValueHandle()) &&
            (params->len == sizeof(uint32_t))) {
        currentTime = *((uint32_t*)params->data);
        ClockNotification * clk = (ClockNotification*)stateMgr.getState(CLOCK_INDEX);
        clk->update(currentTime);
    } else if (params->handle == this->stateOverrideCharacteristic.getValueHandle() &&
            params->len == sizeof(uint8_t)) {
        stateOverride = *(params->data);
        stateMgr.forceState(stateOverride);
        printf("State Override: %d\n", stateOverride);
    } else if (params->handle == this->disconnectCharacteristic.getValueHandle()) {
        // Disconnect Handler, does not handle data
        ble.gap().disconnect(params->connHandle, Gap::REMOTE_USER_TERMINATED_CONNECTION);
    } else if (params->handle == this->clockMapsPriorityCharacteristic.getValueHandle() &&
            params->len == sizeof(uint8_t)) {
        clockMapsPriority = *(params->data);
        uint8_t clockPriority = UPPER_NIBBLE(clockMapsPriority);
        uint8_t mapsPriority =  LOWER_NIBBLE(clockMapsPriority);
        printf("Priority: Clock: %d, Maps: %d\n", clockPriority, mapsPriority);
        ClockNotification * clk = (ClockNotification*)stateMgr.getState(CLOCK_INDEX);
        NavigationNotification * maps = (NavigationNotification*)stateMgr.getState(NAVIGATION_INDEX);
        clk->setPriority(clockPriority);
        maps->setPriority(mapsPriority);
    } else if (params->handle == this->callMusicPriorityCharacteristic.getValueHandle() &&
            params->len == sizeof(uint8_t)) {
        callMusicPriority = *(params->data);
        uint8_t callPriority = UPPER_NIBBLE(callMusicPriority);
        uint8_t musicPriority = LOWER_NIBBLE(callMusicPriority);
        printf("Priority: Phone: %d, Music: %d\n", callPriority, musicPriority);
        PhoneNotification * phone = (PhoneNotification *)stateMgr.getState(PHONE_INDEX);
        MusicNotification * music = (MusicNotification *)stateMgr.getState(MUSIC_INDEX);
        phone->setPriority(callPriority);
        music->setPriority(musicPriority);
    } else if (params->handle == this->speedFuelPriorityCharacteristic.getValueHandle() &&
            params->len == sizeof(uint8_t)) {
        speedFuelPriority = *(params->data);
        uint8_t speedPriority = UPPER_NIBBLE(speedFuelPriority);
        uint8_t fuelPriority = LOWER_NIBBLE(speedFuelPriority);
        printf("Priority: Speed: %d, Fuel: %d\n", speedPriority, fuelPriority);
        VehicleSpeed * speed = (VehicleSpeed *)stateMgr.getState(VEHICLE_SPEED_INDEX);
        FuelLevel * fuel = (FuelLevel *)stateMgr.getState(FUEL_LEVEL_INDEX);
        speed->setPriority(speedPriority);
        fuel->setPriority(fuelPriority);
    } else if (params->handle == this->mapsStreetNameCharacteristic.getValueHandle() &&
            params->len <= MAX_CHAR_LENGTH) {
        char * str = (char*)params->data;
        NavigationNotification * maps = (NavigationNotification*)stateMgr.getState(NAVIGATION_INDEX);
        maps->update(str);
    } else if (params->handle == this->musicSongNameCharacteristic.getValueHandle() &&
            params->len <= MAX_CHAR_LENGTH) {
        char * str = (char*)params->data;
        MusicNotification * music = (MusicNotification *)stateMgr.getState(MUSIC_INDEX);
        music->update(str);
    } else if (params->handle == this->callNameCharacteristic.getValueHandle() &&
            params->len <= MAX_CHAR_LENGTH) {
        char * str = (char*)params->data;
        PhoneNotification * phone = (PhoneNotification *)stateMgr.getState(PHONE_INDEX);
        phone->update(str, params->len);
    } else if (params->handle == this->mapsDistanceNumberCharacteristic.getValueHandle() &&
            params->len == sizeof(uint32_t)) {
        mapsDistanceNumber = *((uint32_t*)params->data);
        NavigationNotification * maps = (NavigationNotification*)stateMgr.getState(NAVIGATION_INDEX);
        maps->update(mapsDistanceNumber);
    } else if (params->handle == this->speedNumberCharacteristic.getValueHandle() &&
            params->len == sizeof(uint16_t)) {
        speedNumber = *((uint16_t*)params->data);
        VehicleSpeed * speed = (VehicleSpeed *)stateMgr.getState(VEHICLE_SPEED_INDEX);
        speed->update(speedNumber);
    } else if (params->handle == this->fuelNumberCharacteristic.getValueHandle() &&
            params->len == sizeof(uint8_t)) {
        fuelNumber = *(params->data);
        FuelLevel * fuel = (FuelLevel *)stateMgr.getState(FUEL_LEVEL_INDEX);
        fuel->update(fuelNumber);
    } else if (params->handle == this->colorCharacteristic.getValueHandle() &&
            params->len == sizeof(uint16_t)) {
        colorNumber = *((uint16_t *)params->data);
        uint8_t satVal = colorNumber & SAT_MASK;
        uint16_t hueVal = colorNumber >> HUE_SHIFT;
        stateMgr.getGfxManager().setColor(hueVal, satVal);
    } else if (params->handle == this->maxCurrentCharacteristic.getValueHandle() &&
            params->len == sizeof(uint16_t)) {
        maxCurrent = *((uint16_t*)params->data);
        stateMgr.getGfxManager().setMaxCurrent(maxCurrent);
    } else if (params->handle == this->mapsDirAndUnitsCharacteristic.getValueHandle() &&
            params->len == sizeof(uint8_t)) {
        mapsDirectionAndUnits = *(params->data);
        NavigationNotification * maps = (NavigationNotification*)stateMgr.getState(NAVIGATION_INDEX);
        uint8_t direction = UPPER_NIBBLE(mapsDirectionAndUnits);
        uint8_t units = LOWER_NIBBLE(mapsDirectionAndUnits);
        maps->update(direction, units);
    } else if (params->handle == this->speedUnitsAndSignalCharacteristic.getValueHandle() &&
            params->len == sizeof(uint8_t)) {
        speedUnitsAndSignalStatus = *(params->data);
        uint8_t speedUnits = UPPER_NIBBLE(speedUnitsAndSignalStatus);
        uint8_t signalStatus = LOWER_NIBBLE(speedUnitsAndSignalStatus);
        VehicleSpeed * speed = (VehicleSpeed *)stateMgr.getState(VEHICLE_SPEED_INDEX);
        SignalOverlay * sigOverlay = (SignalOverlay *)stateMgr.getState(SIGNAL_INDEX);
        speed->update(speedUnits);
        sigOverlay->update(signalStatus);
    } else if (params->handle == this->autoBrightCharacteristic.getValueHandle() &&
            params->len == sizeof(uint8_t)) {
        autoBrightness = *(params->data);
        stateMgr.getGfxManager().setBrightnessConfig(autoBrightness);
    }
}

void WNHService::beginPairingMode() {
    setupGapAdvertising(true);
    eventQueue.call_in(PAIRING_TIMEOUT_MS, this, &WNHService::pairingModeTimeout);
    stateMgr.powerOn();
    stateMgr.forceState(PAIRING_INDEX);
}

void WNHService::pairingModeTimeout() {
    PairingState* pairState = (PairingState*) stateMgr.getState(PAIRING_INDEX);
    pairState->setActive(false);
    stateMgr.forceState(STATE_OVERRIDE_INVALID);
    setupGapAdvertising(false);
    if (!connected) stateMgr.powerOff();
}

void WNHService::sendVoiceCommandTrigger() {
    voiceControl = !voiceControl;
    BLE::Instance().gattServer().write(voiceControlCharacteristic.getValueHandle(), (uint8_t*)&voiceControl, sizeof(voiceControl));
}

void WNHService::onBleInitError(BLE &ble, ble_error_t error) {
    // TODO set a blink pattern to LED 2
}

void callPasskeyDisplayCallback(Gap::Handle_t handle, const SecurityManager::Passkey_t passkey) {
    // for some silly reason the security manager
    // does not use the MBED callback class so class
    // methods cannot be passed back, this is a workaround
    pairingCb.call(handle, passkey);
}

void WNHService::passkeyDisplayCallback(Gap::Handle_t handle, const SecurityManager::Passkey_t passkey)
{
    printf("Input passKey: ");
    for (unsigned i = 0; i < Gap::ADDR_LEN; i++) {
        printf("%c ", passkey[i]);
    }
    PairingState* pairState = (PairingState*) stateMgr.getState(PAIRING_INDEX);
    pairState->update(passkey);
    printf("\r\n");
}

void callSecuritySetupCompletedCallback(Gap::Handle_t handle, SecurityManager::SecurityCompletionStatus_t status) {
    // for some silly reason the security manager
    // does not use the MBED callback class so class
    // methods cannot be passed back, this is a workaround
    securityCb.call(handle, status);
}

void WNHService::securitySetupCompletedCallback(Gap::Handle_t handle, SecurityManager::SecurityCompletionStatus_t status)
{
    PairingState* pairState = (PairingState*) stateMgr.getState(PAIRING_INDEX);
    pairState->setActive(false);
    stateMgr.forceState(STATE_OVERRIDE_INVALID);
    setupGapAdvertising(false);

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
    ble.securityManager().onPasskeyDisplay(callPasskeyDisplayCallback);
    ble.securityManager().onSecuritySetupCompleted(callSecuritySetupCompletedCallback);
}

void WNHService::connectionCallback(const Gap::ConnectionCallbackParams_t *params) {
    printf("Connected\n");
    stateMgr.powerOn();
    connected = true;
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

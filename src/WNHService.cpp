#include "ble/BLE.h"
#include "mbed.h"
#include "WNHService.h"

const static char     DEVICE_NAME[] = "WNH";
static const uint16_t uuid16_list[] = {WNHService::WNH_SERVICE_UUID};

void WNHService::disconnectionCallback(const Gap::DisconnectionCallbackParams_t *params)
{
    BLE::Instance().gap().startAdvertising();
}


/**
 * This callback allows the LEDService to receive updates to the ledState Characteristic.
 *
 * @param[in] params
 *     Information about the characterisitc being updated.
 */
void WNHService::onDataWrittenCallback(const GattWriteCallbackParams *params) {
    if ((params->handle == this->currentTimeCharacteristic.getValueHandle()) && (params->len == 4)) {
        currentTime = *(params->data);
    }
}

/**
 * This function is called when the ble initialization process has failed
 */
void onBleInitError(BLE &ble, ble_error_t error)
{
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

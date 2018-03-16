#include "mbed.h"
#include "ble/BLE.h"
#include "WNHService.h"
// #include "ButtonManager.h"

// ButtonManager btnMgr = ButtonManager();
Ticker ticker;
DigitalOut alivenessLED(LED1, 0);
DigitalOut actuatedLED(LED2, 0);

void periodicCallback(void) {
    alivenessLED = !alivenessLED; /* Do blinky on LED1 to indicate system aliveness. */
    // uint8_t val = (uint8_t)luxDevice.getLux();
    // BLE::Instance().gattServer().write(ledServicePtr->getValueHandle(), &val, 1);
}

void btnCallback(void) {
    actuatedLED = !actuatedLED;
}

int main(void)
{
    ticker.attach(periodicCallback, 1); /* Blink LED every second */
    // btnMgr.setPairingHandler(btnCallback);

    BLE &ble = BLE::Instance();
    WNHService wnhService(ble);

    // if (!luxDevice.enablePower()) {}
    // luxDevice.setIntegrationTime(101);

    /* SpinWait for initialization to complete. This is necessary because the
     * BLE object is used in the main loop below. */
    while (ble.hasInitialized() == false) { /* spin loop */ }
    //
    for (;;) {
        ble.processEvents();
    }
}

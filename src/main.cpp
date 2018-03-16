#include "WNHService.h"

#define EVER ;;

Ticker ticker;
DigitalOut alivenessLED(LED1, 0);

void periodicCallback(void) {
    // blink LED1 at 1Hz to show we are alive
    alivenessLED = !alivenessLED; 
}

int main(void)
{
    ticker.attach(periodicCallback, 1); /* Blink LED every second */

    BLE &ble = BLE::Instance();
    WNHService wnhService(ble);

    // if (!luxDevice.enablePower()) {}
    // luxDevice.setIntegrationTime(101);

    /* SpinWait for initialization to complete. This is necessary because the
     * BLE object is used in the main loop below. */
    while (ble.hasInitialized() == false) { /* spin loop */ }
    //
    for(EVER) {
        ble.processEvents();
    }
}

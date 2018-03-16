#include "WNHService.h"

EventQueue eventQueue;
DigitalOut alivenessLED(LED1, 0);

void periodicCallback(void) {
    // blink LED1 at 1Hz to show we are alive
    alivenessLED = !alivenessLED; 
}

int main(void)
{
    eventQueue.call_every(500, periodicCallback);
    BLE &ble = BLE::Instance();
    WNHService wnhService(ble, eventQueue);

    // if (!luxDevice.enablePower()) {}
    // luxDevice.setIntegrationTime(101);

    eventQueue.dispatch_forever();
    return 0;
}

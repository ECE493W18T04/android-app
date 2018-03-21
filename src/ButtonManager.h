#ifndef __WNH_BTN_MANAGER_H__
#define __WNH_BTN_MANAGER_H__

#include <mbed_events.h>
#include "mbed.h"

class ButtonManager {
public:
    ButtonManager(EventQueue &_eventQueue) : btn1(BUTTON1), btn2(BUTTON2), btn3(BUTTON3), btn4(BUTTON4), eventQueue(_eventQueue) {}
    void setPairingHandler(Callback<void(void)> callback);
    void setVoiceCommandHandler(Callback<void(void)> callback);

private:
    void btn1Callback();
    void btn2Callback();
    InterruptIn btn1;
    InterruptIn btn2;
    InterruptIn btn3;
    InterruptIn btn4;
    Callback<void(void)> pairingHandler;
    Callback<void(void)> voiceCommandHandler;
    EventQueue &eventQueue;
};

#endif /* __WNH_BLE_SERVICE_H__ */

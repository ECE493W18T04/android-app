#ifndef __WNH_BTN_MANAGER_H__
#define __WNH_BTN_MANAGER_H__

#include "mbed.h"

class ButtonManager {
public:
    ButtonManager() : btn1(BUTTON1), btn2(BUTTON2), btn3(BUTTON3), btn4(BUTTON4) {}
    void setPairingHandler(Callback<void(void)> callback);
    void setVoiceCommandHandler(Callback<void(void)> callback);

private:
    InterruptIn btn1;
    InterruptIn btn2;
    InterruptIn btn3;
    InterruptIn btn4;
};

#endif /* __WNH_BLE_SERVICE_H__ */

#include "ButtonManager.h"

void ButtonManager::setPairingHandler(Callback<void(void)> callback) {
    btn1.rise(callback);
}

void ButtonManager::setVoiceCommandHandler(Callback<void(void)> callback) {
    btn2.rise(callback);
}

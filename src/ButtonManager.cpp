#include "ButtonManager.h"

void ButtonManager::setPairingHandler(Callback<void()> callback) {
    btn1.rise(callback);
}

void ButtonManager::setVoiceCommandHandler(Callback<void()> callback) {
    btn2.rise(callback);
}

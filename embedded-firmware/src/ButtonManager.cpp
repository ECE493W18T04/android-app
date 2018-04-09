#include "ButtonManager.h"

void ButtonManager::setPairingHandler(Callback<void()> callback) {
    pairingHandler = callback;
    btn1.rise(Callback<void()>(this, &ButtonManager::btn1Callback));
}

void ButtonManager::setVoiceCommandHandler(Callback<void()> callback) {
    voiceCommandHandler = callback;
    btn2.rise(Callback<void()>(this, &ButtonManager::btn2Callback));
}

void ButtonManager::btn1Callback() {
    eventQueue.call(pairingHandler);
}

void ButtonManager::btn2Callback() {
    eventQueue.call(voiceCommandHandler);
}

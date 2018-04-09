#include "SignalOverlay.h"

SignalOverlay::SignalOverlay(StateManager& _stateMgr) : TempState(_stateMgr) {
}

bool SignalOverlay::tick() {
    return SignalOverlay::tick();
}

bool SignalOverlay::kick() {
    return false;
}

void SignalOverlay::update(uint8_t direction) {
}

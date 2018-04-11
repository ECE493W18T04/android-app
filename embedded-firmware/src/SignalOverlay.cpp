#include "SignalOverlay.h"

/**
 * This class was unable to be completed as none of the
 * test data that we discovered for OpenXC had these
 * triggers and our test vehicles did not generate the
 * triggers either, therefore we did not implement the
 * functionality.
 */

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

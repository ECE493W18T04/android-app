#include "PairingState.h"

PairingState::PairingState(StateManager& _stateMgr) : TempState(_stateMgr) {
}

void PairingState::update(uint32_t key) {
}

bool PairingState::kick() {
    // TODO clear key
    return true;
}

bool PairingState::tick() {
    return TempState::tick();
}

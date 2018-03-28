#include "PairingState.h"
#include "GraphicsManager.h"
#include "StateManager.h"

const uint8_t keyMap[] = {0xE0, 0xA0, 0xE0, 0x40, 0x40, 0x60, 0x40, 0x60};

PairingState::PairingState(StateManager& _stateMgr) : TempState(_stateMgr) {
}

void PairingState::update(uint32_t key) {
}

bool PairingState::kick() {
    // TODO clear key
    setActive(true);
    return true;
}

bool PairingState::tick() {
    GraphicsManager& gfx = getManager().getGfxManager();
    gfx.erase();
    // TODO
    // if key set then show key
    // else draw symbol and word pair
    gfx.drawBitmap(keyMap, 0, 0, 3, 8);
    gfx.drawBuffer();
    return TempState::tick();
}

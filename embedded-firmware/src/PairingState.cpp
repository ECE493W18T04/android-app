#include "PairingState.h"
#include "GraphicsManager.h"
#include "StateManager.h"

#define KEY_STRING_LENGH (6*6)

const uint8_t keyMap[] = {0xE0, 0xA0, 0xE0, 0x40, 0x40, 0x60, 0x40, 0x60};

PairingState::PairingState(StateManager& _stateMgr) : TempState(_stateMgr), keySet(false) {
}

void PairingState::update(const uint8_t key[SIZE_OF_PAIRING_KEY]) {
    keySet = true;
    memcpy(this->key, key, sizeof(this->key));
}

bool PairingState::kick() {
    keySet = false;
    setActive(true);
    return true;
}

bool PairingState::tick() {
    GraphicsManager& gfx = getManager().getGfxManager();
    gfx.erase();
    if (keySet) {
        char keyBuffer[SIZE_OF_PAIRING_KEY + 1];
        sprintf(keyBuffer, "%c%c%c%c%c%c", key[0], key[1], key[2], key[3], key[4], key[5]);
        static int i = DISPLAY_WIDTH;
        if (i < -KEY_STRING_LENGH) {
            i = DISPLAY_WIDTH;
        }
        gfx.placeText(keyBuffer, i--);
    } else {
        gfx.drawBitmap(keyMap, 0, 0, 3, 8);
        gfx.placeText("PAIR", 6);
    }
    gfx.drawBuffer();
    return TempState::tick();
}

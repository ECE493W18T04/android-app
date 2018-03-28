#include "PhoneNotification.h"
#include "StateManager.h"
#include "GraphicsManager.h"

const uint8_t phone[] = {0xC0, 0xC0, 0x80, 0x80, 0x80, 0xC0, 0xC0};

PhoneNotification::PhoneNotification(StateManager& _stateMgr) : TempState(_stateMgr) {
}

bool PhoneNotification::tick() {
    GraphicsManager& gfx = getManager().getGfxManager();
    gfx.erase();
    gfx.drawBitmap(phone, 1, 0, 2, 7);
    gfx.drawBuffer();
    return TempState::tick();
}

bool PhoneNotification::kick() {
    return true;
}

void PhoneNotification::update(char data[], int len) {
}

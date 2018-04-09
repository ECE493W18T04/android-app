#include "PhoneNotification.h"
#include "StateManager.h"
#include "GraphicsManager.h"

const uint8_t phone[] = {0xC0, 0xC0, 0x80, 0x80, 0x80, 0xC0, 0xC0};

PhoneNotification::PhoneNotification(StateManager& _stateMgr) : TempState(_stateMgr), name("N/A") {
}

bool PhoneNotification::tick() {
    GraphicsManager& gfx = getManager().getGfxManager();
    static int slide = DISPLAY_WIDTH;
    gfx.erase();
    gfx.placeText(name, slide--);
    if (slide < -((int)(strlen(name) * (CHARACTER_WIDTH + 1)))) {
        slide = DISPLAY_WIDTH;
    }
    gfx.eraseSection(0, 0, 3, 8);
    gfx.drawBitmap(phone, 1, 0, 2, 7);
    gfx.drawBuffer();
    return TempState::tick();
}

bool PhoneNotification::kick() {
    return true;
}

void PhoneNotification::update(char data[]) {
    if (strlen(data) == 0) {
        strncpy(name, "N/A", MAX_CHAR_LENGTH);
        setActive(false);
    } else {
        setActive(true);
        strncpy(name, data, MAX_CHAR_LENGTH);
    }
    getManager().updateStates();
}

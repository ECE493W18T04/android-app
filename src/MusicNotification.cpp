#include "MusicNotification.h"
#include "StateManager.h"
#include "GraphicsManager.h"

#define MUSICNOTIFICATION_TICKS 100

const uint8_t note[] = {0x18, 0x68, 0x48, 0x58, 0xD8, 0xC0};

MusicNotification::MusicNotification(StateManager& _stateMgr) : TimedState(_stateMgr, MUSICNOTIFICATION_TICKS) {
}

bool MusicNotification::tick() {
    GraphicsManager& gfx = getManager().getGfxManager();
    static int slide = DISPLAY_WIDTH;
    gfx.erase();
    gfx.placeText(notification, slide--);
    if (slide < -((int)(strlen(notification) * (CHARACTER_WIDTH + 1)))) {
        slide = DISPLAY_WIDTH;
    }
    gfx.eraseSection(0, 0, 8, 6);
    gfx.drawBitmap(note, 0, 1, 5, 6);
    gfx.drawBuffer();
    return TimedState::tick();
}

bool MusicNotification::kick() {
    setActive(true);
    resetTicks();
    return true;
}

void MusicNotification::update(char data[]) {
    if (strlen(data) == 0) {
        setActive(false);
    } else {
        resetTicks();
        setActive(true);
        strncpy(notification, data, MAX_CHAR_LENGTH);
    }
    getManager().updateStates();
}

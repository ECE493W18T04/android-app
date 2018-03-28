#include "MusicNotification.h"
#include "StateManager.h"
#include "GraphicsManager.h"

#define MUSICNOTIFICATION_TICKS 100

const uint8_t note[] = {0x18, 0x68, 0x48, 0x58, 0xD8, 0xC0};

MusicNotification::MusicNotification(StateManager& _stateMgr) : TimedState(_stateMgr, MUSICNOTIFICATION_TICKS) {
}

bool MusicNotification::tick() {
    GraphicsManager& gfx = getManager().getGfxManager();
    gfx.erase();
    gfx.drawBitmap(note, 0, 1, 5, 6);
    gfx.drawBuffer();
    return TimedState::tick();
}

bool MusicNotification::kick() {
    return true;
}

void MusicNotification::update(char data[], int len) {
}

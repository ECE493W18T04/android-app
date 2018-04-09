#include "VehicleSpeed.h"
#include "GraphicsManager.h"
#include "StateManager.h"

#define SPEED_BITMAP_WIDTH 7

const uint8_t gauge[] = {0x38, 0x44, 0xA2, 0x92, 0xFE};

VehicleSpeed::VehicleSpeed(StateManager& _stateMgr) : DefaultState(_stateMgr), speed(0) {
}

bool VehicleSpeed::tick() {
    GraphicsManager& gfx = getManager().getGfxManager();
    char textBuffer[10];
    static int offset = DISPLAY_WIDTH;
    gfx.erase();
    sprintf(textBuffer, "%dkph", speed);
    if (offset < -(int)strlen(textBuffer) * (CHARACTER_WIDTH + 1) + SPEED_BITMAP_WIDTH) {
        offset = DISPLAY_WIDTH;
    }
    gfx.placeText(textBuffer, offset--);
    gfx.eraseSection(0, 0, SPEED_BITMAP_WIDTH, DISPLAY_HEIGHT);
    gfx.drawBitmap(gauge, 0, 2, SPEED_BITMAP_WIDTH, 5);
    gfx.drawBuffer();
    return DefaultState::tick();
}

void VehicleSpeed::update(uint16_t speed) {
    this->speed = speed;
    setActive(true);
    getManager().updateStates();
}

void VehicleSpeed::update(uint8_t units) {
    // This is pointless since OPENXC cannot do anything other than kph
    this->units = units;
}

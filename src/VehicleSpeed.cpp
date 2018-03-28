#include "VehicleSpeed.h"
#include "GraphicsManager.h"
#include "StateManager.h"

const uint8_t gauge[] = {0x38, 0x44, 0xA2, 0x92, 0xFE};

VehicleSpeed::VehicleSpeed(StateManager& _stateMgr) : DefaultState(_stateMgr) {
}

bool VehicleSpeed::tick() {
    GraphicsManager& gfx = getManager().getGfxManager();
    gfx.erase();
    gfx.drawBitmap(gauge, 0, 2, 7, 5);
    gfx.drawBuffer();
    return DefaultState::tick();
}

void VehicleSpeed::update(uint16_t speed) {
}

void VehicleSpeed::update(uint8_t units) {
}

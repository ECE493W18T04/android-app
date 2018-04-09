#include "FuelLevel.h"
#include "GraphicsManager.h"
#include "StateManager.h"

#define FUEL_LEVEL_TIME 50
#define FUEL_LEVEL_INVALID 0xFF

const uint8_t pump[] = {0x3C, 0xA4, 0xA4, 0xBC, 0xBC, 0xFC, 0x3C, 0x7E};

FuelLevel::FuelLevel(StateManager& _stateMgr) : TimedState(_stateMgr, FUEL_LEVEL_TIME), fuelLevel(FUEL_LEVEL_INVALID) {
}

void FuelLevel::update(uint8_t fuelLevel) {
    if (FUEL_LEVEL_INVALID != fuelLevel) {
        resetTicks();
        setActive(true);
        getManager().updateStates();
    }
    this->fuelLevel = fuelLevel;
}

bool FuelLevel::tick() {
    GraphicsManager& gfx = getManager().getGfxManager();
    gfx.erase();
    gfx.drawBitmap(pump, 1, 0, 7, 8);
    if (FUEL_LEVEL_INVALID == fuelLevel) {
        gfx.placeText("N/A", 10);
    } else {
        char buffer[10];
        sprintf(buffer, "%d%%", fuelLevel);
        gfx.placeText(buffer, 10);
    }
    gfx.drawBuffer();
    return TimedState::tick();
}

bool FuelLevel::kick() {
    resetTicks();
    return true;
}

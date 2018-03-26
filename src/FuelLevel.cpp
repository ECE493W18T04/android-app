#include "FuelLevel.h"

#define FUEL_LEVEL_TIME 50

FuelLevel::FuelLevel(StateManager& _stateMgr) : TimedState(_stateMgr, FUEL_LEVEL_TIME) {
}

void FuelLevel::update(uint8_t fuelLevel) {
}

bool FuelLevel::tick() {
    return TimedState::tick();
}

bool FuelLevel::kick() {
    return true;
}

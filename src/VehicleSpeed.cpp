#include "VehicleSpeed.h"

VehicleSpeed::VehicleSpeed(StateManager& _stateMgr) : DefaultState(_stateMgr) {
}

bool VehicleSpeed::tick() {
    return DefaultState::tick();
}

void VehicleSpeed::update(uint16_t speed) {
}

void VehicleSpeed::update(uint8_t units) {
}

#include "VehicleSpeed.h"

VehicleSpeed::VehicleSpeed(StateManager& _stateMgr) : DefaultState(_stateMgr) {
}

bool VehicleSpeed::tick() {
    return DefaultState::tick();
}

void VehicleSpeed::update(uint8_t speed, uint8_t units) {
}

#ifndef VEHICLESPEED_h
#define VEHICLESPEED_h

#include "DefaultState.h"

class VehicleSpeed : public DefaultState {
public:
    VehicleSpeed(StateManager& _stateMgr);
    bool tick();
    void update(uint8_t speed, uint8_t units);
private:
    uint8_t speed, units;
};

#endif // VEHICLESPEED_h

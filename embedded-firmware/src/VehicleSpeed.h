#ifndef VEHICLESPEED_h
#define VEHICLESPEED_h

/**
 * Handles the vehicle speed display state
 *
 * Satisfies: REQ-B-4.6.3.2
 */

#include "DefaultState.h"

class VehicleSpeed : public DefaultState {
public:
    VehicleSpeed(StateManager& _stateMgr);
    bool tick();
    void update(uint16_t speed);
    void update(uint8_t units);
private:
    uint8_t speed, units;
};

#endif // VEHICLESPEED_h

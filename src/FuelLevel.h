#ifndef FUELLEVEL_h
#define FUELLEVEL_h

#include "mbed.h"
#include "TimedState.h"

class FuelLevel : public TimedState {
public:
    FuelLevel(StateManager& _stateMgr);
    void update(uint8_t fuelLevel);
    bool tick();
private:
    uint8_t fuelLevel;
};

#endif // FUELLEVEL_h

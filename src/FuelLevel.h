#ifndef FUELLEVEL_h
#define FUELLEVEL_h

#include "TimedState.h"

class FuelLevel : TimedState {
public:
    FuelLevel(StateManager& _stateMgr, int ticks);
    void update(uint8_t fuelLevel);
    bool tick();
private:
    uint8_t fuelLevel;
}

#endif // FUELLEVEL_h

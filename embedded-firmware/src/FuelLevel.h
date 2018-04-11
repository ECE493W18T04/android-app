#ifndef FUELLEVEL_h
#define FUELLEVEL_h

/**
 * Helps handle temporary timed states for the priority queue
 *
 * Satisfies: REQ-A-4.5.3.3
 */

#include "mbed.h"
#include "TimedState.h"

class FuelLevel : public TimedState {
public:
    FuelLevel(StateManager& _stateMgr);
    void update(uint8_t fuelLevel);
    bool tick();
    bool kick();
private:
    uint8_t fuelLevel;
};

#endif // FUELLEVEL_h

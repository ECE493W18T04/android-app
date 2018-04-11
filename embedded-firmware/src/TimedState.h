#ifndef TIMEDSTATE_h
#define TIMEDSTATE_h

/**
 * Handles states with specific time limits
 *
 * Satisfies: REQ-A-4.5.3.3
 */

#include "State.h"

class TimedState : public State {
public:
    TimedState(StateManager& _stateMgr, int ticks);
    virtual bool tick();
protected:
    void resetTicks();
private:
    int ticksLeft;
    int resetTo;
};

#endif // TIMEDSTATE_h

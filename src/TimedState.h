#ifndef TIMEDSTATE_h
#define TIMEDSTATE_h

#include "State.h"

class TimedState : State {
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

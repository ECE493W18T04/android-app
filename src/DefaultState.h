#ifndef DEFAULTSTATE_h
#define DEFAULTSTATE_h

#include "State.h"

class DefaultState : State {
public:
    DefaultState(StateManager& _stateMgr);
    virtual bool tick();
};

#endif // DEFAULTSTATE_h

#ifndef DEFAULTSTATE_h
#define DEFAULTSTATE_h

#include "State.h"

class DefaultState : public State {
public:
    DefaultState(StateManager& _stateMgr);
    virtual bool tick();
    virtual bool kick();
};

#endif // DEFAULTSTATE_h

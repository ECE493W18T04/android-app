#ifndef TEMPSTATE_h
#define TEMPSTATE_h

#include "State.h"

class TempState : public State {
public:
    TempState(StateManager& _stateMgr);
    virtual bool tick();
};

#endif // TEMPSTATE_h

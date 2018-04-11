#ifndef TEMPSTATE_h
#define TEMPSTATE_h

/**
 * Handles states with indefinite timelines
 *
 * Satisfies: REQ-A-4.5.3.3
 */

#include "State.h"

class TempState : public State {
public:
    TempState(StateManager& _stateMgr);
    virtual bool tick();
};

#endif // TEMPSTATE_h

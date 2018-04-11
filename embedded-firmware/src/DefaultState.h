#ifndef DEFAULTSTATE_h
#define DEFAULTSTATE_h

/**
 * This class helps satifies features of the priority queue
 *
 * Satisfies: REQ-A-4.5.3.3
 */

#include "State.h"

class DefaultState : public State {
public:
    DefaultState(StateManager& _stateMgr);
    virtual bool tick();
    virtual bool kick();
};

#endif // DEFAULTSTATE_h

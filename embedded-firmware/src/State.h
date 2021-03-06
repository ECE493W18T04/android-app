#ifndef STATE_h
#define STATE_h

/**
 * Represents the basics of a state in a state machine
 *
 * Satisfies: REQ-B-4.7.3.2, REQ-B-4.7.3.3, REQ-A-4.5.3.3
 */

#include "mbed.h"

#define MAX_CHAR_LENGTH 128

class StateManager;

class State {
public:
    State(StateManager& _stateMgr);
    void setPriority(int _priority);
    void setActive(bool _state);
    bool getActive();
    int getPriority();
    virtual bool tick() = 0;
    virtual bool kick() = 0;
    StateManager& getManager();
private:
    int priority;
    bool state;
    StateManager& stateMgr;
};

#endif // STATE_h

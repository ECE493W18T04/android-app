#ifndef STATEMANAGER_h
#define STATEMANAGER_h

#include "mbed.h"
#include <events/mbed_events.h>
#include "GraphicsManager.h"
#include "State.h"

class StateManager {
public:
    StateManager(EventQueue &_eventQueue);
    void tick();
private:
    GraphicsManager graphicsMgr;
    State* currentState;
};

#endif // STATEMANAGER_h

#ifndef STATEMANAGER_h
#define STATEMANAGER_h

#include "mbed.h"
#include <events/mbed_events.h>
#include "GraphicsManager.h"

class StateManager {
public:
    StateManager(EventQueue &_eventQueue);
    void tick();
private:
    GraphicsManager graphicsMgr;
};

#endif // STATEMANAGER_h

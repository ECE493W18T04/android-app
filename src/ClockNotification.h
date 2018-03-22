#ifndef CLOCKNOTIFICATION_h
#define CLOCKNOTIFICATION_h

#include "DefaultState.h"

class ClockNotification : DefaultState {
public:
    ClockNotification(StateManager& _stateMgr);
    bool tick();
    void update(uint32_t time);
};

#endif // CLOCKNOTIFICATION_h

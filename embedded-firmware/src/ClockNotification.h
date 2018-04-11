#ifndef CLOCKNOTIFICATION_h
#define CLOCKNOTIFICATION_h

/**
 * This class represents the clock display state
 *
 * Satisfies: REQ-A-4.5.3.3
 */

#include "DefaultState.h"

class ClockNotification : public DefaultState {
public:
    ClockNotification(StateManager& _stateMgr);
    bool tick();
    void update(uint32_t time);
};

#endif // CLOCKNOTIFICATION_h

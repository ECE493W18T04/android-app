#ifndef NAVIGATIONNOTIFICATION_h
#define NAVIGATIONNOTIFICATION_h

/**
 * Handles the navigational state of the system
 *
 * Satisfies: Notification Relaying - Navigation
 */

#include "TempState.h"

class NavigationNotification : public TempState {
public:
    NavigationNotification(StateManager& _stateMgr);
    bool tick();
    bool kick();
    void update(char street[]);
    void update(uint8_t direction, uint8_t distanceUnits);
    void update(uint32_t distance);
private:
    char street[MAX_CHAR_LENGTH];
    uint8_t direction;
    double distance;
    uint8_t distanceUnits;
};

#endif

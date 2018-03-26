#ifndef NAVIGATIONNOTIFICATION_h
#define NAVIGATIONNOTIFICATION_h

#include "TempState.h"

class NavigationNotification : public TempState {
public:
    NavigationNotification(StateManager& _stateMgr);
    bool tick();
    bool kick();
    void update(char street[], int len);
    void update(uint8_t direction, uint8_t distanceUnits);
    void update(uint32_t distance);
private:
    char street[MAX_CHAR_LENGTH];
    uint8_t direction;
    uint16_t distance;
    uint8_t distanceUnits;
};

#endif

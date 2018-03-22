#include "NavigationNotification.h"

NavigationNotification::NavigationNotification(StateManager& _stateMgr) : TempState(_stateMgr) {
}

bool NavigationNotification::tick() {
    return TempState::tick();
}

void NavigationNotification::update(char street[], int len) {
}

void NavigationNotification::update(uint8_t direction) {
}

void NavigationNotification::update(uint16_t distance, uint8_t distanceUnits) {
}

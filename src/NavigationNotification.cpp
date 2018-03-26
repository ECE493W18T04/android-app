#include "NavigationNotification.h"

NavigationNotification::NavigationNotification(StateManager& _stateMgr) : TempState(_stateMgr) {
}

bool NavigationNotification::tick() {
    return TempState::tick();
}

void NavigationNotification::update(char street[], int len) {
}

void NavigationNotification::update(uint8_t direction, uint8_t distanceUnits) {
}

void NavigationNotification::update(uint32_t distance) {
    printf("Navigation: Distance %lu\n", distance);
}

#include "NavigationNotification.h"

NavigationNotification::NavigationNotification(StateManager& _stateMgr) : TempState(_stateMgr) {
}

bool NavigationNotification::tick() {
    return TempState::tick();
}

bool NavigationNotification::kick() {
    return true;
}

void NavigationNotification::update(char street[], int len) {
    printf("Navigation Street: %s\n", street);
}

void NavigationNotification::update(uint8_t direction, uint8_t distanceUnits) {
    printf("Navigation direction: %d, units: %d\n", direction, distanceUnits);
}

void NavigationNotification::update(uint32_t distance) {
    printf("Navigation: Distance %lu, %d, %d, %d, %d\n", distance, distance >> 24, (distance >> 16) & 0xFF, (distance >> 8) & 0xFF, distance & 0xFF);
}

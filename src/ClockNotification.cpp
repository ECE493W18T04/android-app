#include "ClockNotification.h"

ClockNotification::ClockNotification(StateManager& _stateMgr) : DefaultState(_stateMgr) {
    setActive(false);
}

bool ClockNotification::tick() {
    return DefaultState::tick();
}

void ClockNotification::update(uint32_t time) {
    printf("Current time %lu\n", time);
    setActive(true);
    set_time(time);
}

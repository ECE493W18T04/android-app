#include "ClockNotification.h"

ClockNotification::ClockNotification(StateManager& _stateMgr) : DefaultState(_stateMgr) {
}

bool ClockNotification::tick() {
    return DefaultState::tick();
}

void ClockNotification::update(uint32_t time) {
    setActive(true);
    set_time(time);
}

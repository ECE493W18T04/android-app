#include "ClockNotification.h"
#include "StateManager.h"

ClockNotification::ClockNotification(StateManager& _stateMgr) : DefaultState(_stateMgr) {
    setActive(false);
}

bool ClockNotification::tick() {
    char buffer[32];
    time_t seconds = time(NULL);
    // strftime(buffer, 32, "%I:%M %p\n", localtime(&seconds));
    // printf("%s", buffer);
    return DefaultState::tick();
}

void ClockNotification::update(uint32_t time) {
    printf("Current time %lu\n", time);
    setActive(true);
    set_time(time);
}

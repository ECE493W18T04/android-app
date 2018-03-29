#include "TimedState.h"

TimedState::TimedState(StateManager& _stateMgr, int ticks) : State(_stateMgr), resetTo(ticks), ticksLeft(ticks) {}

bool TimedState::tick() {
    ticksLeft--;
    if (ticksLeft < 0) {
        setActive(false);
        return false;
    }
    return true;
}

void TimedState::resetTicks() {
    ticksLeft = resetTo;
}

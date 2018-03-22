#include "TimedState.h"

TimedState::TimedState(StateManager& _stateMgr, int ticks) : State(_stateMgr) {
}

bool TimedState::tick() {
    // TODO
    return false;
}

void TimedState::resetTicks() {
}

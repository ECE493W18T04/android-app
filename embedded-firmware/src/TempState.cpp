#include "TempState.h"

TempState::TempState(StateManager& _stateMgr) : State(_stateMgr) {
}

bool TempState::tick() {
    return getActive();
}

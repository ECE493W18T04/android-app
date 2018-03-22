#include "DefaultState.h"

DefaultState::DefaultState(StateManager& _stateMgr) : State(_stateMgr) {
}

bool DefaultState::tick() {
    return true;
}

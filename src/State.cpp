#include "State.h"
#include "StateManager.h"

State::State(StateManager& _stateMgr) : state(false), stateMgr(_stateMgr) {
}

void State::setPriority(int _priority) {
    priority = _priority;
    stateMgr.updateStates();
}

void State::setActive(bool _state) {
    state = _state;
    stateMgr.updateStates();
}

bool State::getActive() {
    return state;
}

int State::getPriority() {
    return priority;
}

StateManager& State::getManager() {
    return stateMgr;
}

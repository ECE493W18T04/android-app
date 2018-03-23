#include "State.h"

State::State(StateManager& _stateMgr) : stateMgr(_stateMgr) {
}

void State::setPriority(int _priority) {
    priority = _priority;
}

void State::setActive(bool _state) {
    state = _state;
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

#include "StateManager.h"

StateManager::StateManager(EventQueue& _eventQueue) {
    _eventQueue.call_every(100, this, &StateManager::tick);
}

void StateManager::tick() {
    bool result = false;
    if (currentState) {
        result = currentState->tick();
    }
}

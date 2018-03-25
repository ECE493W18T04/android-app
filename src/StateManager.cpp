#include "StateManager.h"

StateManager::StateManager(EventQueue& _eventQueue) : graphicsMgr(_eventQueue), currentState(NULL), overlay(NULL) {
    for (int i = 0; i < STATE_COUNT; i++) {
        States[i] = NULL;
    }
    _eventQueue.call_every(100, this, &StateManager::tick);
    States[CLOCK_INDEX] = new ClockNotification(*this);
    States[VEHICLE_SPEED_INDEX] = new VehicleSpeed(*this);
    States[MUSIC_INDEX] = new MusicNotification(*this);
    States[FUEL_LEVEL_INDEX] = new FuelLevel(*this);
    States[NAVIGATION_INDEX] = new NavigationNotification(*this);
    States[PHONE_INDEX] = new PhoneNotification(*this);
    States[SIGNAL_INDEX] = new SignalOverlay(*this);
    States[PAIRING_INDEX] = new PairingState(*this);
}

void StateManager::tick() {
    bool result = false;
    if (currentState) {
        result = currentState->tick();
    }
    if (!result) {
        // something bad happened or we expired the state
        updateStates();
    }
    if (overlay) {
        overlay->tick();
    }
}

void StateManager::updateStates() {
    int i;
    int maxPriority = 255;
    for (i = 0; i < STATE_COUNT; i++) {
        if (States[i]) {
            if (!States[i]->getActive()) {
                // state isn't active, ignore it
                continue;
            }
            int priority = States[i]->getPriority();
            if (priority < maxPriority) {
                currentState = States[i];
                maxPriority = priority;
            }
        }
    }
}

void StateManager::pushOverlay(State* state) {
    if (state) {
        overlay = state;
    }
}

void StateManager::forceState(State* state) {
    if (state && state->getActive()) {
        currentState = state;
    }
}

State* StateManager::getState(int id) {
    if (id < STATE_COUNT) {
        return States[id];
    } else {
        return NULL;
    }
}

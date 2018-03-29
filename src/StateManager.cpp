#include "StateManager.h"

#define STATE_OVERRIDE_INVALID 0xFF

StateManager::StateManager(EventQueue& _eventQueue) : currentState(NULL), overlay(NULL), graphicsMgr(_eventQueue), forced(false), powered(false) {
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
    if (!powered) return;
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
    printf("Forced: %d\n", forced);
    printf("Powered: %d\n", powered);
    if (forced || !powered) return;
    for (i = 0; i < STATE_COUNT; i++) {
        if (States[i] && States[i]->getActive() && States[i]->getPriority() < maxPriority) {
            currentState = States[i];
            maxPriority = States[i]->getPriority();
        }
    }
}

void StateManager::pushOverlay(State* state) {
    if (state) {
        overlay = state;
    }
}

void StateManager::forceState(int id) {
    if (STATE_OVERRIDE_INVALID == id) {
        forced = false;
        currentState = NULL;
        graphicsMgr.erase();
        graphicsMgr.drawBuffer();
        updateStates();
        return;
    } else if (States[id] && States[id]->kick()) {
        forced = true;
        currentState = States[id];
    }
}

State* StateManager::getState(int id) {
    if (id < STATE_COUNT) {
        return States[id];
    } else {
        return NULL;
    }
}

GraphicsManager& StateManager::getGfxManager() {
    return graphicsMgr;
}

void StateManager::powerOn() {
    powered = true;
    // TODO turn on lux device
    for (int i = 0; i < STATE_COUNT; i++) {
        if (States[i]) {
            States[i]->setActive(false);
        }
    }
}

void StateManager::powerOff() {
    powered = false;
    currentState = NULL;
    graphicsMgr.erase();
    graphicsMgr.drawBuffer();
    // TODO turn off lux
}

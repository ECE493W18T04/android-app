#include "StateManager.h"

StateManager::StateManager(EventQueue& _eventQueue) : graphicsMgr(_eventQueue) {
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
}

void StateManager::pushState(State* state) {
}

void StateManager::pushOverlay(State* state) {
}

void StateManager::forceState(State* state) {
}

State* StateManager::getState(int id) {
    if (id < STATE_COUNT) {
        return States[id];
    } else {
        return NULL;
    }
}

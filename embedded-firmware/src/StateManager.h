#ifndef STATEMANAGER_h
#define STATEMANAGER_h

#include "mbed.h"
#include <events/mbed_events.h>
#include "GraphicsManager.h"
#include "ClockNotification.h"
#include "VehicleSpeed.h"
#include "MusicNotification.h"
#include "FuelLevel.h"
#include "NavigationNotification.h"
#include "PhoneNotification.h"
#include "SignalOverlay.h"
#include "PairingState.h"
#include "State.h"

#define STATE_COUNT 10

#define CLOCK_INDEX 0
#define VEHICLE_SPEED_INDEX 1
#define MUSIC_INDEX 2
#define FUEL_LEVEL_INDEX 3
#define NAVIGATION_INDEX 4
#define PHONE_INDEX 5
#define SIGNAL_INDEX 6
#define PAIRING_INDEX 7

class StateManager {
public:
    StateManager(EventQueue &_eventQueue);
    void tick();
    void updateStates();
    void pushOverlay(State* state);
    void forceState(int id);
    State* getState(int id);
    void powerOn();
    void powerOff();
    GraphicsManager& getGfxManager();
private:
    State* States[STATE_COUNT];
    State* currentState;
    State* overlay;
    GraphicsManager graphicsMgr;
    bool forced;
    bool powered;
};

#endif // STATEMANAGER_h

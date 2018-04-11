#ifndef PAIRINGSTATE_h
#define PAIRINGSTATE_h

/**
 * Handles pairing in state manager
 *
 * Satisfies: REQ-A-4.1.3.1
 */

#include "TempState.h"
#include "StateManager.h"

#define SIZE_OF_PAIRING_KEY 6

class PairingState : public TempState {
public:
    PairingState(StateManager& _stateMgr);
    void update(const uint8_t key[SIZE_OF_PAIRING_KEY]);
    bool tick();
    bool kick();
private:
    // I would love to use the BLE security type here,
    // but for some reason when I include the BLE header
    // it blows the build sky high with errors in both
    // mbed and GCC code
    uint8_t key[SIZE_OF_PAIRING_KEY];
    bool keySet;
};

#endif

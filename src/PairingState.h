#ifndef PAIRINGSTATE_h
#define PAIRINGSTATE_h

#include "TempState.h"

class PairingState : public TempState {
public:
    PairingState(StateManager& _stateMgr);
    void update(uint32_t key);
    bool tick();
private:
    uint32_t key;
};

#endif

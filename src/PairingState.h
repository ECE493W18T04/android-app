#ifndef PAIRINGSTATE_h
#define PAIRINGSTATE_h

#include "TempState.h"

class PairingState : TempState {
public:
    PairingState();
    update(uint32_t key);
private:
    uint32_t key;
};

#endif

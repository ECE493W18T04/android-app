#ifndef SIGNALOVERLAY_h
#define SIGNALOVERLAY_h

#include "TempState.h"

class SignalOverlay : public TempState {
public:
    SignalOverlay(StateManager& _stateMgr);
    bool tick();
    bool kick();
    void update(uint8_t direction);
private:
    uint8_t direction;
};

#endif // SIGNALOVERLAY_h

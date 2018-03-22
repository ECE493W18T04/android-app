#ifndef MUSICNOTIFICATION_h
#define MUSICNOTIFICATION_h

#include "TimedState.h"

#define MAX_CHAR_LENGTH 256

class MusicNotification : TimedState {
public:
    MusicNotification(StateManager& _stateMgr);
    bool tick();
    void update(char data[], int len);
private:
    char notification[MAX_CHAR_LENGTH];
};

#endif // MUSICNOTIFICATION_h

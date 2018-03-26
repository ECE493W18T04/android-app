#ifndef MUSICNOTIFICATION_h
#define MUSICNOTIFICATION_h

#include "TimedState.h"

class MusicNotification : public TimedState {
public:
    MusicNotification(StateManager& _stateMgr);
    bool tick();
    bool kick();
    void update(char data[], int len);
private:
    char notification[MAX_CHAR_LENGTH];
};

#endif // MUSICNOTIFICATION_h

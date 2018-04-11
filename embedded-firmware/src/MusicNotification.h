#ifndef MUSICNOTIFICATION_h
#define MUSICNOTIFICATION_h

/**
 * Handles Music state for displaying
 *
 * Satisfies: REQ-A-4.3.3.2
 */

#include "TimedState.h"

class MusicNotification : public TimedState {
public:
    MusicNotification(StateManager& _stateMgr);
    bool tick();
    bool kick();
    void update(char data[]);
private:
    char notification[MAX_CHAR_LENGTH];
};

#endif // MUSICNOTIFICATION_h

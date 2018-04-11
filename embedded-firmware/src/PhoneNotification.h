#ifndef PHONENOTIFICATION_h
#define PHONENOTIFICATION_h

/**
 * Handles phone displaying notification
 *
 * Satisfies: REQ-A-4.4.3.1
 */

#include "TempState.h"

class PhoneNotification : public TempState {
public:
    PhoneNotification(StateManager& _stateMgr);
    bool tick();
    bool kick();
    void update(char data[]);
private:
    char name[MAX_CHAR_LENGTH];
};

#endif // PHONENOTIFICATION_h

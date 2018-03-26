#ifndef PHONENOTIFICATION_h
#define PHONENOTIFICATION_h

#include "TempState.h"

class PhoneNotification : public TempState {
public:
    PhoneNotification(StateManager& _stateMgr);
    bool tick();
    bool kick();
    void update(char data[], int len);
private:
    char name[MAX_CHAR_LENGTH];
};

#endif // PHONENOTIFICATION_h

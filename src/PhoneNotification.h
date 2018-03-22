#ifndef PHONENOTIFICATION_h
#define PHONENOTIFICATION_h

#include "TempState.h"

class PhoneNotification : TempState {
public:
    bool tick();
    void update(char data[], int len);
private:
    char name[MAX_CHAR_LENGTH];
};

#endif // PHONENOTIFICATION_h

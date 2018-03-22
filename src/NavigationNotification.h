#ifndef NAVIGATIONNOTIFICATION_h
#define NAVIGATIONNOTIFICATION_h

#define MAX_CHAR_LENGTH 256

class NavigationNotification : TempState {
public:
    bool tick();
    void update(char[] street, int len);
    void update(uint8_t direction);
    void update(uint16_t distance, uint8_t distanceUnits);
private:
    char street[MAX_CHAR_LENGTH];
    uint8_t direction;
    uint16_t distance;
    uint8_t distanceUnits;
};

#endif

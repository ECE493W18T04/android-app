#ifndef AUTOBRIGHTNESSALG_H
#define AUTOBRIGHTNESSALG_H

#include <mbed.h>

#define min(x, y) (x < y ? x : y)
#define max(x, y) (x > y ? x : y)

typedef uint8_t auto_brightness_state; enum {
    UNLOCKED,
    LOCKED,
};

class AutoBrightnessAlg {
public:
    AutoBrightnessAlg();
    AutoBrightnessAlg(double alpha);
    void addSample(uint32_t dataSample);
    uint8_t getState();
    void setVariability(double alpha);
private:
    long map(long x, long in_min, long in_max, long out_min, long out_max);
    double _alpha;
    uint32_t _current_value;
    int8_t _current_state;
    auto_brightness_state _internal_state;
    uint16_t _ticks_in_current_state;
};

#endif

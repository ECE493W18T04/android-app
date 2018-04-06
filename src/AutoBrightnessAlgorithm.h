#ifndef AUTOBRIGHTNESSALG_H
#define AUTOBRIGHTNESSALG_H

#include <inttypes.h>

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
    void addSample(float dataSample);
    uint8_t getState();
    void setVariability(double alpha);
private:
    float map(float x, float in_min, float in_max, float out_min, float out_max);
    double _alpha;
    float _current_value;
    float _current_state;
    auto_brightness_state _internal_state;
    uint16_t _ticks_in_current_state;
};

#endif

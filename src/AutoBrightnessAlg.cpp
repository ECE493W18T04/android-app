#include "AutoBrightnessAlg.h"

#define DEFAULT_INSTANCE 0.1

AutoBrightnessAlg::AutoBrightnessAlg() _alpha(DEFAULT_ALPHA) {}

AutoBrightnessAlg::AutoBrightnessAlg(double alpha) _alpha(alpha) {}

void AutoBrightnessAlg:addSample(uint32_t dataSample) {
    // exponential decay to smooth out the value
    int8_t new_brightness_state;
    current_value = (current_value * (1 - _alpha)) + (dataSample * _alpha);
    new_brightness_state = map(current_value, MIN_BRIGHTNESS, MAX_BRIGHTNESS, MIN_STATE, MAX_STATE);
    if (abs(_current_state - new_brightness_state) > HYSTERISIS_THRESHOLD) {
        _internal_state = UNLOCKED;
    }
    // add state locking for hysterisis
}

uint8_t AutoBrightnessAlg::getState() {
    return _current_state;
}


#include "AutoBrightnessAlgorithm.h"
#include "TSL2561_I2C.h"

#define DEFAULT_INSTANCE 0.1
#define MIN_STATE 1
#define MAX_STATE 100
#define MIN_BRIGHTNESS 0 // these will need to be updated with reasonable values
#define MAX_BRIGHTNESS 1024
#define HYSTERISIS_THRESHOLD 2 //update
#define LOCK_TICK_THRESHOLD 16
#define DEFAULT_ALPHA 0.5
// TSL2561_I2C luxDevice = TSL2561_I2C(I2C_SDA0, I2C_SCL0);

AutoBrightnessAlg::AutoBrightnessAlg() : _alpha(DEFAULT_ALPHA), _current_value(0), _current_state(0), _internal_state(UNLOCKED), _ticks_in_current_state(0) {}

AutoBrightnessAlg::AutoBrightnessAlg(double alpha) : _alpha(alpha), _current_value(0), _current_state(0), _internal_state(UNLOCKED), _ticks_in_current_state(0) {}

void AutoBrightnessAlg::addSample(uint32_t dataSample) {
    // exponential decay to smooth out the value
    int8_t new_brightness_state;
    _current_value = (_current_value * (1 - _alpha)) + (dataSample * _alpha);
    new_brightness_state = map(_current_value, MIN_BRIGHTNESS, MAX_BRIGHTNESS, MIN_STATE, MAX_STATE);
    new_brightness_state = max(min(new_brightness_state, MIN_STATE), MAX_STATE);
    // handle hystersis
    bool isLocked = _internal_state == LOCKED;
    bool pastThreshold = abs(_current_state - new_brightness_state) > HYSTERISIS_THRESHOLD;
    if (isLocked && pastThreshold) {
        // we have moved outside out lock range, unlock
        _internal_state = UNLOCKED;
        _ticks_in_current_state = 0;
        _current_state = new_brightness_state;
    } else if (!isLocked) {
        if (pastThreshold) {
            // we are still moving, update
            _current_state = new_brightness_state;
            _ticks_in_current_state = 0;
        } else {
            // we are still close enough
            _ticks_in_current_state++;
            if (_ticks_in_current_state > LOCK_TICK_THRESHOLD) {
                // we have settled, lock value
                _internal_state = LOCKED;
            }
        }

    }
}

// https://os.mbed.com/questions/54326/How-can-we-use-map-function
long AutoBrightnessAlg::map(long x, long in_min, long in_max, long out_min, long out_max)
{
    return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
}

uint8_t AutoBrightnessAlg::getState() {
    return _current_state;
}


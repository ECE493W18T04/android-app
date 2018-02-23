#ifndef AUTOBRIGHTNESSALG_H
#define AUTOBRIGHTNESSALG_H

typedef uint8_t auto_brightness_state; enum {
    UNLOCKED;
    LOCKED;
}

class AutoBrightnessAlgorithm {
public:
    AutoBrightnessAlgorithm();
    AutoBrightnessAlgorithm(double alpha);
    void addSample(uint32_t dataSample);
    uint8_t getState();
    void setVariability(double alpha);
private:
    double _alpha;
    uint32_t _current_value = 0;
    int8_t _current_state = 0;
    auto_brightness_state _internal_state = UNLOCKED;
}

#endif

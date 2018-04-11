#ifndef ENVIRONMENTMANAGER_h
#define ENVIRONMENTMANAGER_h

/**
 * Handles environment limitations and pass out limits
 *
 * Satisfies: 5.1.2
 */

#include "mbed.h"

#define SAMPLE_SIZE 50
#define MARGIN 0.9
#define MAX_CURRENT_PER_LED 20
#define ALPHA 0.15
#define DEFAULT_MAX_CURRENT 200

class DisplayDriver;

class EnvironmentManager {
public:
    EnvironmentManager(DisplayDriver& dd);
    void addSample(uint16_t count);
    void setCurrentLimit(uint16_t);
private:
    uint16_t getMax();
    DisplayDriver& displayDriver;
    uint16_t currentSampleSet[SAMPLE_SIZE];
    double upperLimit;
    uint16_t maxCurrent;
};

#endif // ENVIRONMENTMANAGER_h

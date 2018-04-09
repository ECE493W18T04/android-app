#include "EnvironmentManager.h"
#include "DisplayDriver.h"

EnvironmentManager::EnvironmentManager(DisplayDriver& dd) : displayDriver(dd), upperLimit(0), maxCurrent(DEFAULT_MAX_CURRENT) {
}

void EnvironmentManager::addSample(uint16_t count) {
    static uint8_t i = 0;
    if (i < SAMPLE_SIZE) {
        currentSampleSet[i++] = count;
    } else {
        // full dataset, calculate new limit
        if (upperLimit < 1) {
            // limit is stale or not inited, kick start with raw value
            upperLimit = getMax();
        } else {
            upperLimit = (upperLimit * (1 - ALPHA)) + (ALPHA * getMax());
        }
        double current_ratio = (maxCurrent * MARGIN) / (upperLimit * MAX_CURRENT_PER_LED);
        current_ratio = min(current_ratio, 1) * 100;
        // set limit on driver
        displayDriver.setBrightnessLimit(current_ratio);
        // reset dataset and add pending sample
        i = 0;
        addSample(count);
    }
}

void EnvironmentManager::setCurrentLimit(uint16_t current) {
    maxCurrent = current;
}

uint16_t EnvironmentManager::getMax() {
    uint16_t current_max = 0;
    for (int i = 0; i < SAMPLE_SIZE; i++) {
        current_max = max(current_max, currentSampleSet[i]);
    }
    return current_max;
}

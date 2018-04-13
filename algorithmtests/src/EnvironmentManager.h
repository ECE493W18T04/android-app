#ifndef ENVIRONMENTMANAGER_h
#define ENVIRONMENTMANAGER_h

#include <inttypes.h>

#define SAMPLE_SIZE 1
#define MARGIN 0.9
#define MAX_CURRENT_PER_LED 20
#define ALPHA 0.15
#define DEFAULT_MAX_CURRENT 500

class EnvironmentManager {
public:
    EnvironmentManager();
    void addSample(uint16_t count);
    void setCurrentLimit(uint16_t);
    uint8_t getBrightnessLimit();
private:
    uint16_t getMax();
    double current_ratio;
    uint16_t currentSampleSet[SAMPLE_SIZE];
    double upperLimit;
    uint16_t maxCurrent;
};

#endif // ENVIRONMENTMANAGER_h

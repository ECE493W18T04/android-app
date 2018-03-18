#include "mbed.h"
#include "DisplayDriver.h"

#define DISPLAY_WIDTH 32
#define DISPLAY_HEIGHT 8

class GraphicsManager {
public:
    GraphicsManager();
private:
    DisplayDriver driver;
    uint32_t buffer[DISPLAY_WIDTH*DISPLAY_HEIGHT];
};

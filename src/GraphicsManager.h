#ifndef GRAPHICSMANAGER_h
#define GRAPHICSMANAGER_h

#include "mbed.h"
#include "DisplayDriver.h"

#define DISPLAY_WIDTH 32
#define DISPLAY_HEIGHT 8

class GraphicsManager {
public:
    GraphicsManager();
    void tick();
    void erase();
    void fill(uint8_t x, uint8_t y, uint8_t width, uint8_t height);
    int placeText(char text[], int horizonatalOffset);
    void eraseSection(uint8_t x, uint8_t y, uint8_t width, uint8_t height);
    void drawBitmap(uint8_t bitmap[], uint8_t x, uint8_t y, uint8_t width, uint8_t length);
    void drawBuffer();
    void setColor(uint16_t _hue, uint8_t _saturation);
private:
    uint8_t getCharByte(char c, uint8_t offset);
    void drawChar(char c, int x);
    void clearPixel(uint16_t x, uint16_t y);
    void setPixel(uint16_t x, uint16_t y);
    uint16_t hue;
    uint8_t sat;
    bool flipped;
    DisplayDriver driver;
    uint32_t buffer[DISPLAY_WIDTH*DISPLAY_HEIGHT];
};

#endif // GRAPHICSMANAGER_h

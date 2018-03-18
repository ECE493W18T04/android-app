#ifndef DISPLAYDRIVER_h
#define DISPLAYDRIVER_h

#include "mbed.h"

class DisplayDriver {
public:
    DisplayDriver(PinName MOSI, PinName MISO, PinName SCLK, uint32_t clockSpeed);
    void setBuffer(uint16_t _width, uint16_t _height, uint32_t _buffer[]);
    void draw();
    uint32_t getColor(uint8_t red, uint8_t green, uint8_t blue);
private:
    void drawPixel(uint16_t x, uint16_t y);
    void innerDraw();
    SPI port;
    uint16_t width;
    uint16_t height;
    uint32_t *buffer;
};

#endif // DISPLAYDRIVER_h

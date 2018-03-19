#ifndef DISPLAYDRIVER_h
#define DISPLAYDRIVER_h

#include "mbed.h"

class DisplayDriver {
public:
    DisplayDriver(PinName MOSI, PinName MISO, PinName SCLK, uint32_t clockSpeed);
    void setBuffer(uint16_t _width, uint16_t _height, uint32_t _buffer[]);
    void draw();
    uint32_t getColor(uint16_t hue, uint8_t sat);
private:
    double Hue_2_RGB( double v1, double v2, double vH );
    uint32_t getColorRGB(uint8_t red, uint8_t green, uint8_t blue);
    void drawPixel(uint16_t x, uint16_t y);
    void innerDraw();
    SPI port;
    uint8_t brightness;
    uint16_t width;
    uint16_t height;
    uint32_t *buffer;
};

#endif // DISPLAYDRIVER_h

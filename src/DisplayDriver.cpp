#include <mbed.h>
#include "DisplayDriver.h"

#define MAX_SAT 100.0
#define MAX_BRIGHTNESS 100.0

DisplayDriver::DisplayDriver(PinName MOSI, PinName MISO, PinName SCLK, uint32_t clockSpeed) : port(MOSI, MISO, SCLK) {
    port.format(8,3);
    port.frequency(clockSpeed);
}

void DisplayDriver::setBuffer(uint16_t _width, uint16_t _height, uint32_t _buffer[]) {
    width = _width;
    height = _height;
    buffer = _buffer;
}

void DisplayDriver::innerDraw() {
    port.write(0X00);  // Start
    port.write(0X00);
    port.write(0X00);
    port.write(0X00);

    bool zig = false;
    for (uint16_t x = 0; x < width; x++) {
        if (zig) {
            for (int16_t y = 0; y < height; y++) {
                drawPixel(x, y);
            }
        } else {
            for (int16_t y = height - 1; y >= 0; y--) {
                drawPixel(x, y);
            }
        }
        zig = !zig;
    }

    port.write(0XFF); // Stop
    port.write(0XFF);
    port.write(0XFF);
    port.write(0XFF);
}

void DisplayDriver::draw() {
    /**
     * No Idea why, but for some dumb reason
     * the last few LEDs won't turn on without
     * multiple writes
     */
    innerDraw();
    innerDraw();
}

// alg from https://stackoverflow.com/questions/3018313/algorithm-to-convert-rgb-to-hsv-and-hsv-to-rgb-in-range-0-255-for-both
uint32_t DisplayDriver::getColor(uint16_t hue, uint8_t sat) {
    double      hh, p, q, t, ff, r, g, b;
    long        i;
    double      s = sat / MAX_SAT;
    double      v = brightness / MAX_BRIGHTNESS;

    if(s <= 0.0) {       // < is bogus, just shuts up warnings
        r = v;
        g = v;
        b = v;
        return getColorRGB((uint8_t)(r*255), (uint8_t)(g*255), (uint8_t)(b*255));
    }
    hh = hue;
    if(hh >= 360.0) hh = 0.0;
    hh /= 60.0;
    i = (long)hh;
    ff = hh - i;
    p = v * (1.0 - s);
    q = v * (1.0 - (s * ff));
    t = v * (1.0 - (s * (1.0 - ff)));

    switch(i) {
    case 0:
        r = v;
        g = t;
        b = p;
        break;
    case 1:
        r = q;
        g = v;
        b = p;
        break;
    case 2:
        r = p;
        g = v;
        b = t;
        break;

    case 3:
        r = p;
        g = q;
        b = v;
        break;
    case 4:
        r = t;
        g = p;
        b = v;
        break;
    case 5:
    default:
        r = v;
        g = p;
        b = q;
        break;
    }
    return getColorRGB((uint8_t)(r*255), (uint8_t)(g*255), (uint8_t)(b*255));
}

uint32_t DisplayDriver::getColorRGB(uint8_t red, uint8_t green, uint8_t blue) {
    return 0xFF000000 | (red & 0xFF) | ((green & 0xFF) << 8) | ((blue & 0xFF) << 16);
}

void DisplayDriver::drawPixel(uint16_t x, uint16_t y) {
    uint32_t pixel = buffer[x*height + y];
    port.write((pixel >> 24) & 0xFF);
    port.write((pixel >> 16) & 0xFF);
    port.write((pixel >> 8) & 0xFF);
    port.write(pixel & 0xFF);
}

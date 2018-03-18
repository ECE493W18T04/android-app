#include <mbed.h>
#include "DisplayDriver.h"

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

uint32_t DisplayDriver::getColor(uint8_t red, uint8_t green, uint8_t blue) {
    return 0xFF000000 | (red & 0xFF) | ((green & 0xFF) << 8) | ((blue & 0xFF) << 16);
}

void DisplayDriver::drawPixel(uint16_t x, uint16_t y) {
    uint32_t pixel = buffer[x*height + y];
    port.write((pixel >> 24) & 0xFF);
    port.write((pixel >> 16) & 0xFF);
    port.write((pixel >> 8) & 0xFF);
    port.write(pixel & 0xFF);
}

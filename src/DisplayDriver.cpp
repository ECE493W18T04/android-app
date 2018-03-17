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

// int main() {
//     DisplayDriver dp(SPI_PSELMOSI0, SPI_PSELMISO0, SPI_PSELSCK0, 2000000);
//     uint32_t data[256] = {0};
//     printf("start\n");
//     dp.setBuffer(32, 8, data);
//     for (int i = 0; i < 32; i++) {
//         for (int j = 0; j < 8; j++) {
//             data[i*8 + j] = 0xE0000000;
//         }
//     }
//     dp.draw();
//     for (uint16_t i = 0; i < 256; i++) {
//         data[i] = dp.getColor(i, 0, 0);
//         dp.draw();
//         wait_ms(100);
//     }
//     while (1) {
//     }
// }

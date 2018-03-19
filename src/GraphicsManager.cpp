#include "GraphicsManager.h"

#define SPI_SPEED 2000000 // 2MHz
#define RGB_MASK 0x00FFFFFF

GraphicsManager::GraphicsManager() : driver(SPI_PSELMOSI0, SPI_PSELMISO0, SPI_PSELSCK0, SPI_SPEED) {
    driver.setBuffer(DISPLAY_WIDTH, DISPLAY_HEIGHT, buffer);
    // testing
    setColor(0, 100);
}

void GraphicsManager::tick() {
    // do random testing in here
    static int i = 0;
    buffer[(i++) % 256] = 0xFF0A0000;
    setColor((i % 360), 100);
    drawBuffer();
}

void GraphicsManager::setPixel(uint16_t x, uint16_t y) {
}

void GraphicsManager::erase() {
}

void GraphicsManager::fill(uint8_t x, uint8_t y, uint8_t width, uint8_t height) {
}

void GraphicsManager::placeText(char text[], int horizonatalOffset) {
}

void GraphicsManager::eraseSection(uint8_t x, uint8_t y, uint8_t width, uint8_t height) {
}

void GraphicsManager::drawBitmap(uint8_t bitmap[], uint8_t x, uint8_t y, uint8_t width, uint8_t length) {
}

void GraphicsManager::setColor(uint16_t _hue, uint8_t _sat) {
    hue = _hue;
    sat = _sat;
}

void GraphicsManager::drawBuffer() {
    uint32_t updatedColor = driver.getColor(hue, sat);
    for (uint16_t i = 0; i < DISPLAY_WIDTH*DISPLAY_HEIGHT; i++) {
        if (buffer[i] & RGB_MASK) {
            buffer[i] = updatedColor;
        }
    }
    driver.draw();
}

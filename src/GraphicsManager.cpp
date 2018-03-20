#include "GraphicsManager.h"

#define SPI_SPEED 2000000 // 2MHz
#define RGB_MASK 0x00FFFFFF
#define PIXEL_OFF 0xFF000000

GraphicsManager::GraphicsManager() : driver(SPI_PSELMOSI0, SPI_PSELMISO0, SPI_PSELSCK0, SPI_SPEED) {
    driver.setBuffer(DISPLAY_WIDTH, DISPLAY_HEIGHT, buffer);
    erase();
    driver.draw();
    // testing
    setColor(0, 0);
}

void GraphicsManager::tick() {
    // do random testing in here
    static int i = 0;
    erase();
    fill(0, 0, 32, 8);
    setColor((i % 360), 70);
    i++;
    drawBuffer();
}

void GraphicsManager::setPixel(uint16_t x, uint16_t y) {
    if (x >= DISPLAY_WIDTH || y >= DISPLAY_HEIGHT) return;
    buffer[x*DISPLAY_HEIGHT + y] = 1;
}

void GraphicsManager::clearPixel(uint16_t x, uint16_t y) {
    if (x >= DISPLAY_WIDTH || y >= DISPLAY_HEIGHT) return;
    buffer[x*DISPLAY_HEIGHT + y] = 0;
}

void GraphicsManager::erase() {
    memset(buffer, 0, sizeof(buffer));
}

void GraphicsManager::fill(uint8_t x, uint8_t y, uint8_t width, uint8_t height) {
    for (int dx = x; dx < x + width; dx++) {
        for (int dy = y; dy < y + height; dy++) {
            setPixel(dx, dy);
        }
    }
}

void GraphicsManager::placeText(char text[], int horizonatalOffset) {
}

void GraphicsManager::eraseSection(uint8_t x, uint8_t y, uint8_t width, uint8_t height) {
    for (int dx = x; dx < x + width; dx++) {
        for (int dy = y; dy < y + height; dy++) {
            clearPixel(dx, dy);
        }
    }
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
        } else {
            buffer[i] = PIXEL_OFF;
        }
    }
    driver.draw();
}

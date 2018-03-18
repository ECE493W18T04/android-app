#include "GraphicsManager.h"

#define SPI_SPEED 2000000 // 2MHz

GraphicsManager::GraphicsManager() : driver(SPI_PSELMOSI0, SPI_PSELMISO0, SPI_PSELSCK0, SPI_SPEED) {
    driver.setBuffer(DISPLAY_WIDTH, DISPLAY_HEIGHT, buffer);
}

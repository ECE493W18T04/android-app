#include "NavigationNotification.h"
#include "GraphicsManager.h"
#include "StateManager.h"

#define NAV_IMAGE_NONE        0
#define NAV_IMAGE_LEFT_UTURN  1
#define NAV_IMAGE_LEFT_BACK   2
#define NAV_IMAGE_LEFT        3
#define NAV_IMAGE_LEFT_EXIT   4
#define NAV_IMAGE_STRAIGHT    5
#define NAV_IMAGE_RIGHT_EXIT  6
#define NAV_IMAGE_RIGHT       7
#define NAV_IMAGE_RIGHT_BACK  8
#define NAV_IMAGE_RIGHT_UTURN 9
#define DISTANCE_SCALE        10.0
#define NAV_UNITS_KM          0
#define NAV_UNITS_M           1
#define NAV_UNITS_MI          2
#define NAV_UNITS_YD          3
#define NAV_UNITS_UNKNOWN     5

const uint8_t uturn[]       = {0x00, 0x3e, 0x22, 0xaa, 0xfa, 0x72, 0x22, 0x02};
const uint8_t left_back[]   = {0x04, 0x0C, 0x94, 0xA4, 0xC4, 0xF4};
const uint8_t left[]        = {0x30, 0x60, 0xfc, 0x64, 0x34, 0x04, 0x04, 0x04};
const uint8_t left_exit[]   = {0xF0, 0xC0, 0xA0, 0x90, 0x08, 0x08, 0x08, 0x08};
const uint8_t straight[]    = {0x00, 0x20, 0x70, 0xf8, 0xa8, 0x20, 0x20, 0x00};
const uint8_t right_exit[]  = {0x78, 0x18, 0x28, 0x48, 0x80, 0x80, 0x80, 0x80};
const uint8_t right[]       = {0x30, 0x18, 0xfc, 0x98, 0xb0, 0x80, 0x80, 0x80};
const uint8_t right_back[]  = {0x80, 0xC0, 0xA4, 0x94, 0x8C, 0xBC};
const uint8_t right_uturn[] = {0x00, 0xF8, 0x88, 0xAA, 0xBE, 0x9C, 0x88, 0x80};

const char* KM_TEXT = "km";
const char* M_TEXT = "m";
const char* MI_TEXT = "mi";
const char* YD_TEXT = "yd";
const char* EMPTY_TEXT = "";

NavigationNotification::NavigationNotification(StateManager& _stateMgr) : TempState(_stateMgr),direction(NAV_IMAGE_NONE) {
}

bool NavigationNotification::tick() {
    GraphicsManager& gfx = getManager().getGfxManager();
    char textBuffer[MAX_CHAR_LENGTH * 2];
    const char* unit;
    int offset;
    static int slide = 0;
    gfx.erase();
    switch (direction) {
    case NAV_UNITS_KM:
        unit = KM_TEXT;
        break;
    case NAV_UNITS_M:
        unit = M_TEXT;
        break;
    case NAV_UNITS_MI:
        unit = MI_TEXT;
        break;
    case NAV_UNITS_YD:
        unit = YD_TEXT;
        break;
    case NAV_UNITS_UNKNOWN:
    default:
        unit = EMPTY_TEXT;
        break;
    }
    sprintf(textBuffer, "%s, %.1f%s", street, distance, unit);
    gfx.placeText(textBuffer, slide--);
    if (direction == NAV_IMAGE_NONE) {
        offset = 0;
    } else {
        offset = 7;
        gfx.eraseSection(0, 0, 7, 8);
    }
    if (slide < -((int)strlen(textBuffer)*(CHARACTER_WIDTH + 1)) + offset) {
        slide = DISPLAY_WIDTH;
    }
    switch (direction) {
    case NAV_IMAGE_NONE:
        break;
    case NAV_IMAGE_LEFT_UTURN:
        gfx.drawBitmap(uturn, 0, 0, 7, 8);
        break;
    case NAV_IMAGE_LEFT_BACK:
        gfx.drawBitmap(left_back, 0, 2, 6, 6);
        break;
    case NAV_IMAGE_LEFT:
        gfx.drawBitmap(left, 0, 0, 6, 8);
        break;
    case NAV_IMAGE_LEFT_EXIT:
        gfx.drawBitmap(left_exit, 0, 0, 5, 8);
        break;
    case NAV_IMAGE_STRAIGHT:
        gfx.drawBitmap(straight, 0, 0, 5, 8);
        break;
    case NAV_IMAGE_RIGHT_EXIT:
        gfx.drawBitmap(right_exit, 0, 0, 5, 8);
        break;
    case NAV_IMAGE_RIGHT:
        gfx.drawBitmap(right, 0, 0, 6, 8);
        break;
    case NAV_IMAGE_RIGHT_BACK:
        gfx.drawBitmap(right_back, 0, 2, 6, 6);
        break;
    case NAV_IMAGE_RIGHT_UTURN:
        gfx.drawBitmap(right_uturn, 0, 0, 7, 8);
        break;
    }
    gfx.drawBuffer();
    return TempState::tick();
}

bool NavigationNotification::kick() {
    return true;
}

void NavigationNotification::update(char street[]) {
    if (strlen(street) == 0) {
        setActive(false);
    } else {
        setActive(true);
        strncpy(this->street, street, MAX_CHAR_LENGTH);
    }
    getManager().updateStates();
}

void NavigationNotification::update(uint8_t direction, uint8_t distanceUnits) {
    this->direction = direction;
    this->distanceUnits = distanceUnits;
    getManager().updateStates();
}

void NavigationNotification::update(uint32_t distance) {
    this->distance = distance / DISTANCE_SCALE;
    getManager().updateStates();
}

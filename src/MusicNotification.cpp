#include "MusicNotification.h"

#define MUSICNOTIFICATION_TICKS 100

MusicNotification::MusicNotification(StateManager& _stateMgr) : TimedState(_stateMgr, MUSICNOTIFICATION_TICKS) {
}

bool MusicNotification::tick() {
    return TimedState::tick();
}

void MusicNotification::update(char data[], int len) {
}

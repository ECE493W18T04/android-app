#include "PhoneNotification.h"

PhoneNotification::PhoneNotification(StateManager& _stateMgr) : TempState(_stateMgr) {
}

bool PhoneNotification::tick() {
    return TempState::tick();
}

bool PhoneNotification::kick() {
    return true;
}

void PhoneNotification::update(char data[], int len) {
}

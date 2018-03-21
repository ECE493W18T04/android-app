#ifndef STATE_h
#define STATE_h

class StateManager;

class State {
public:
    State(StateManager& _stateMgr);
    void setPriority(int priority);
    void setActive(bool state);
    int getPriority();
    virtual bool tick() = 0;
private:
    int priority;
    StateManager& stateMgr;
};

#endif // STATE_h

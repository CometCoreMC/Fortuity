package net.cometcore.fortuity.team;

public enum TeamState {
    ALIVE("Alive"),
    ELIMINATED("Eliminated");

    final String name;

    TeamState(String name) {
        this.name = name;
    }


    public String toString() {
        return this.name;
    }
}

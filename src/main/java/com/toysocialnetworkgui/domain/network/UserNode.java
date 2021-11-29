package com.toysocialnetworkgui.domain.network;

import com.toysocialnetworkgui.domain.User;

import java.util.Objects;

public class UserNode {
    public User u, prevU;
    public int steps;

    public UserNode(User u) {
        this.u = u;
        this.prevU = null;
        this.steps = 0;
    }

    public UserNode(User u, User pU, int steps) {
        this.u = u;
        this.prevU = pU;
        this.steps = steps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserNode userNode = (UserNode) o;
        return Objects.equals(u, userNode.u);
    }

    @Override
    public int hashCode() {
        return Objects.hash(u);
    }
}

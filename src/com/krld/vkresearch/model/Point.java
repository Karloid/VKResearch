package com.krld.vkresearch.model;

import com.krld.vkresearch.Utils;

public class Point {

    public double x;
    public double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void constraint(double max) {
        double distance = Utils.getDistance(this);
        if (distance > max) {
            multiple(max / distance);
        }
    }

    public void multiple(double multiplayer) {
        this.x *= multiplayer;
        this.y *= multiplayer;
    }

    public void minus(Point point) {
        this.x -= point.x;
        this.y -= point.y;
    }

    public Point clone() {
        return new Point(x, y);
    }

    public int xi() {
        return (int) x;
    }

    public int yi() {
        return (int) y;
    }

    public void plus(Point point) {
        if (point != null) {
            this.x += point.x;
            this.y += point.y;
        }
    }


}

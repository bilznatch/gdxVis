package com.lol.fraud.Visibility;

import com.badlogic.gdx.math.Vector2;

public class event {
    public enum event_type{
        start,
        end
    };
    event_type type;
    line_segment segment;

    event(event_type type, line_segment segment) {
        this.type = type;
        this.segment = segment;
    }

    public Vector2 point() {
        return segment.a;
    }
}

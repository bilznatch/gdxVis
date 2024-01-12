package com.lol.fraud.Visibility;

import com.badlogic.gdx.math.Vector2;

public class line_segment{
    Vector2 a;
    Vector2 b;

    line_segment(Vector2 start, Vector2 end) {
        a = new Vector2(start);
        b = new Vector2(end);
    }
    line_segment(float x1, float y1, float x2, float y2) {
        a = new Vector2(x1,y1);
        b = new Vector2(x2,y2);
    }
}
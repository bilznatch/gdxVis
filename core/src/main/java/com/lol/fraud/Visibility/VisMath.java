package com.lol.fraud.Visibility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import java.time.temporal.Temporal;
public class VisMath {
    private static Vector2 tempA = new Vector2(), tempB = new Vector2();
    public enum orientation
    {
        LEFT_TURN,
        RIGHT_TURN,
        COLLINEAR
    };
    static final float FLOAT_EPSILON = Math.ulp(1.0f);
    public static boolean approx_equal(float a, float b, float epsilon){
        return Math.abs(a - b) <= Math.max(Math.abs(a), Math.abs(b)) * epsilon;
    }
    public static boolean approx_equal(float a, float b){
        return Math.abs(a - b) <= Math.max(Math.abs(a), Math.abs(b)) * FLOAT_EPSILON;
    }
    public static boolean strictly_less(float a, float b, float epsilon){
        return (b - a) > Math.max(Math.abs(a), Math.abs(b)) * epsilon;
    }
    public static boolean strictly_less(float a, float b){
        return (b - a) > Math.max(Math.abs(a), Math.abs(b)) * FLOAT_EPSILON;
    }
    public static boolean approx_equal(Vector2 a, Vector2 b, float epsilon){
        return approx_equal(a.x, b.x, epsilon) &&
                approx_equal(a.y, b.y, epsilon);
    }
    public static boolean approx_equal(Vector2 a, Vector2 b){
        return approx_equal(a.x, b.x) &&
                approx_equal(a.y, b.y);
    }
    public static boolean strictly_less(Vector2 a, Vector2 b, float epsilon){
        return strictly_less(a.x, b.x, epsilon) &&
                strictly_less(a.y, b.y, epsilon);
    }
    public static boolean strictly_less(Vector2 a, Vector2 b){
        return strictly_less(a.x, b.x) &&
                strictly_less(a.y, b.y);
    }
    public static orientation compute_orientation(Vector2 a , Vector2 b, Vector2 c){
        float crs = new Vector2(b).sub(a).crs(new Vector2(c).sub(a));
        /*System.out.println(
                "A: " + a.toString() + ", " +
                "B: " + b.toString() + ", " +
                "C: " + c.toString() + ", " +
                        "CRS: " + crs
        );*/
        if(approx_equal(0,crs)){
            return orientation.COLLINEAR;
        }else if(strictly_less(0,crs)){
            return orientation.LEFT_TURN;
        }else{
            return orientation.RIGHT_TURN;
        }
    }
    public static boolean cmp_angle(Vector2 a, Vector2 b, Vector2 vertex) {
        boolean is_a_left = strictly_less(a.x, vertex.x);
        boolean is_b_left = strictly_less(b.x, vertex.x);
        if (is_a_left != is_b_left)
            return is_b_left;

        if (approx_equal(a.x, vertex.x) && approx_equal(b.x, vertex.x)) {
            if (!strictly_less(a.y, vertex.y) ||
                    !strictly_less(b.y, vertex.y)) {
                return strictly_less(b.y, a.y);
            }
            return strictly_less(a.y, b.y);
        }

        tempA.set(a).sub(vertex);
        tempB.set(b).sub(vertex);
        float det = tempA.crs(tempB);
        if (approx_equal(det, 0.f)) {
            return tempA.dot(tempA) < tempB.dot(tempB);
        }
        return det < 0;
    }
    public static boolean cmp_dist(line_segment x, line_segment y, Vector2 origin){
        Vector2 a = x.a.cpy(),
                b = x.b.cpy(),
                c = y.a.cpy(),
                d = y.b.cpy();
        if (approx_equal(b, c) || approx_equal(b, d)){
            tempA.set(a);
            a.set(b);
            b.set(tempA);
        }
        if (approx_equal(a, d)){
            tempA.set(c);
            c.set(d);
            d.set(tempA);
        }
        // cases with common endpoints
        if (approx_equal(a, c))
        {
            orientation oad = compute_orientation(origin, a, d);
            orientation oab = compute_orientation(origin, a, b);
            if (approx_equal(b, d) || oad != oab)
                return false;
            return compute_orientation(a, b, d) != compute_orientation(a, b, origin);
        }

        // cases without common endpoints
        orientation cda = compute_orientation(c, d, a);
        orientation cdb = compute_orientation(c, d, b);
        if (cdb == orientation.COLLINEAR && cda == orientation.COLLINEAR)
        {
            return distance_squared(origin, a) < distance_squared(origin, c);
        }
        else if (cda == cdb ||
                cda == orientation.COLLINEAR ||
                cdb == orientation.COLLINEAR)
        {
            orientation cdo = compute_orientation(c, d, origin);
            return cdo == cda || cdo == cdb;
        }
        else
        {
            orientation abo = compute_orientation(a, b, origin);
            return abo != compute_orientation(a, b, c);
        }
    }
    public static float distance_squared(Vector2 a, Vector2 b){
        tempA.set(a).sub(b);
        return tempA.dot(tempA);
    }
}
package com.lol.fraud.Visibility;

import com.badlogic.gdx.math.Vector2;

import static com.lol.fraud.Visibility.VisMath.*;


public class Ray2D {
    Vector2 origin = new Vector2();
    Vector2 direction = new Vector2();

    Ray2D(Vector2 origin, Vector2 direction){
        this.origin.set(origin);
        this.direction.set(direction);
    }

    /** Find the nearest intersection point of ray and line segment.
     * @param segment
     * @param out_point reference to a variable where the nearest
     *        intersection point will be stored (can be changed even
     *        when there is no intersection)
     * @return true iff the ray intersects the line segment
     */
    boolean intersects(line_segment segment, Vector2 out_point) {
        Vector2 ao = new Vector2(origin).sub(segment.a);
        Vector2 ab =  new Vector2(segment.b).sub(segment.a);
        float det = new Vector2(ab).crs(direction);
        if (approx_equal(det, 0.f))
        {
            orientation abo = compute_orientation(segment.a, segment.b, origin);
            if (abo != orientation.COLLINEAR)
                return false;
            float dist_a = new Vector2(ao).dot(direction);
            float dist_b = new Vector2(origin).sub(ab).dot(direction);

            if (dist_a > 0 && dist_b > 0)
                return false;
            else if ((dist_a > 0) != (dist_b > 0))
                out_point.set(origin);
            else if (dist_a > dist_b)  // at this point, both distances are negative
                out_point.set(segment.a); // hence the nearest point is A
            else
                out_point.set(segment.b);
            return true;
        }

        float u = new Vector2(ao).crs(direction) / det;
        if (strictly_less(u, 0.f) ||
                strictly_less(1.f, u))
            return false;

        float t = -(new Vector2(ab).crs(ao)) / det;
        out_point.set(new Vector2(origin).add(new Vector2(direction).scl(t)));
        return approx_equal(t, 0.f) || t > 0;
    }
}

package com.lol.fraud.Visibility;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.TreeSet;

import static com.lol.fraud.Visibility.VisMath.*;
public class VisibilityV2 {
    /** Calculate visibility polygon vertices in clockwise order.
     * Endpoints of the line segments (obstacles) can be ordered arbitrarily.
     * Line segments collinear with the point are ignored.
     * @param point - position of the observer
     * @param begin iterator of the list of line segments (obstacles)
     * @param end iterator of the list of line segments (obstacles)
     * @return vector of vertices of the visibility polygon
     */
    public static ArrayList<Vector2> vertices = new ArrayList<>();
    public static ArrayList<line_segment> segments = new ArrayList<>();
    public static ArrayList<Vector2> visibility_polygon(Vector2 point, ArrayList<Polygon> polys) {
        convertPolysToLines(polys);
        TreeSet<line_segment> state = new TreeSet<>((o1, o2) -> {
            boolean res = cmp_dist(o1,o2,point);
            if(res){
                return -1;
            }else{
                return 1;
            }
        });
        ArrayList<event> events = new ArrayList<>();

        for (line_segment segment:segments)
        {

            // Sort line segment endpoints and add them as events
            // Skip line segments collinear with the point
            orientation pab= compute_orientation(point, segment.a, segment.b);
            if (pab == orientation.COLLINEAR)
            {
                continue;
            }
            else if (pab == orientation.RIGHT_TURN)
            {
                events.add(new event(event.event_type.start, segment));
                events.add(new event(event.event_type.end, new line_segment(segment.b,segment.a)));
            }
            else
            {
                events.add(new event(event.event_type.start, new line_segment(segment.b,segment.a)));
                events.add(new event(event.event_type.end, segment));
            }

            // Initialize state by adding line segments that are intersected
            // by vertical ray from the point
            Vector2 a = new Vector2(segment.a), b = new Vector2(segment.b);
            if (a.x > b.x){
                Vector2 temp = new Vector2(a);
                a.set(b);
                b.set(temp);
            }


            orientation abp = compute_orientation(a, b, point);
            if (abp == orientation.RIGHT_TURN && (approx_equal(b.x, point.x) || (a.x < point.x && point.x < b.x)))
            {
                state.add(segment);
            }
        }
        for(event e: events){
            //Gdx.app.log("events: " + events.size(),e.segment.a +", "+ e.segment.b);
        }
        // sort events by angle
        events.sort((a, b) -> {
            if(approx_equal(a.point(),b.point())){
                if(a.type == event.event_type.end && b.type == event.event_type.start){
                    return -1;
                }else if(a.type == event.event_type.start && b.type == event.event_type.end){
                    return 1;
                }else{
                    return 0;
                }
            }
            boolean ret = cmp_angle(a.point(),b.point(), point);
            if(ret){
                return -1;
            }else{
                return 1;
            }
        });
        //Gdx.app.log("Events","BREAK");
        for(event e: events){
            //Gdx.app.log("events: " + events.size(),e.segment.a +", "+ e.segment.b);
        }
        // find the visibility polygon
        vertices.clear();
        for (event e : events)
        {
            if (e.type == event.event_type.end)
                state.removeIf(x ->
                        (approx_equal(x.a, e.segment.a) && approx_equal(x.b, e.segment.b)) ||
                                (approx_equal(x.b, e.segment.a) && approx_equal(x.a, e.segment.b)));

            if (state.isEmpty())
            {
                vertices.add(e.point());
            }
            else if (cmp_dist(e.segment, state.first(),point))
            {
                // Nearest line segment has changed
                // Compute the intersection point with this segment
                Vector2 intersection = new Vector2();
                Ray2D ray = new Ray2D(point, new Vector2(e.point()).sub(point));
                line_segment nearest_segment = state.first();
                ray.intersects(nearest_segment, intersection);

                if (e.type == event.event_type.start)
                {
                    vertices.add(intersection);
                    vertices.add(e.point());
                }
                else
                {
                    vertices.add(e.point());
                    vertices.add(intersection);
                }
            }

            if (e.type == event.event_type.start)
                state.add(e.segment);
        }

        //remove collinear points
        for (int i = 0; i < vertices.size();i++){
            Vector2 prev = i == 0 ? vertices.get(vertices.size()-1) : vertices.get(i-1);
            Vector2 next = i + 1 == vertices.size() ? vertices.get(0) : vertices.get(i+1);
            if (compute_orientation(prev, vertices.get(i), next) == orientation.COLLINEAR){
                vertices.get(i).set(prev);
            }
        }
        for(int i = vertices.size()-1;i>=1;i--){
            if(vertices.get(i).epsilonEquals(vertices.get(i-1))){
                vertices.remove(i);
            }
        }
        return vertices;
    }

    public static ArrayList<line_segment> convertPolysToLines(ArrayList<Polygon> polyList){
        segments.clear();
        for(Polygon p:polyList){
            float[] vertices = p.getTransformedVertices();
            for(int i = 0; i < vertices.length-1;i+=2) {
                if(i == vertices.length-2){
                    //figure out the current line segment
                    segments.add(new line_segment(vertices[i],vertices[i+1],vertices[0],vertices[1]));
                }else{
                    //figure out the current line segment
                    segments.add(new line_segment(vertices[i],vertices[i+1],vertices[i+2],vertices[i+3]));
                }
            }

        }
        return segments;
    }
}
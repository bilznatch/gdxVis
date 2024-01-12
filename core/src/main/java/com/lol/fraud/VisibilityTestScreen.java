package com.lol.fraud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.BloomEffect;
import com.crashinvaders.vfx.effects.GaussianBlurEffect;
import com.lol.fraud.Visibility.Visibility;
import com.lol.fraud.Visibility.VisibilityV2;
import com.lol.fraud.Visibility.line_segment;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.ArrayList;

/** First screen of the application. Displayed after the application is created. */
public class PlatformerTestScreen implements Screen, InputProcessor {
    SpriteBatch batch;
    ShapeDrawer sd;
    Pixmap whitepixelpixmap;
    Texture whitepixeltexture;
    TextureRegion whitepixelregion;
    Texture fboholder;
    TextureRegion fboregion;
    FitViewport viewport;
    OrthographicCamera camera;
    Vector2 mouse = new Vector2(0,0);
    ArrayList<Polygon> polyList = new ArrayList<Polygon>();
    ArrayList<Vector2> vis;
    Polygon visPoly = new Polygon();
    boolean debug = false;
    VfxManager vfxManager;
    GaussianBlurEffect vfxEffect;

    @Override
    public void show() {
        // Prepare your screen here.
        batch = new SpriteBatch();
        whitepixelpixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        whitepixelpixmap.setColor(Color.WHITE);
        whitepixelpixmap.fill();
        whitepixeltexture = new Texture(whitepixelpixmap);
        whitepixelregion = new TextureRegion(whitepixeltexture);
        sd = new ShapeDrawer(batch,whitepixelregion);
        camera = new OrthographicCamera(1920,1080);
        camera.position.set(960,540,1);
        viewport = new FitViewport(1920,1080,camera);
        viewport.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),true);
        Gdx.input.setInputProcessor(this);
        generatePolys();
        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
        BloomEffect bloom  = new BloomEffect();
        bloom.setBlurAmount(50);
        bloom.setBlurType(GaussianBlurEffect.BlurType.Gaussian5x5);
        bloom.setBloomSaturation(5);
        bloom.setBloomIntensity(1.5f);
        vfxManager.addEffect(bloom);
    }
    public void generatePolys(){
        Polygon poly1 = new Polygon();
        Polygon poly2 = new Polygon();
        Polygon poly3 = new Polygon();
        Polygon poly4 = new Polygon();
        Polygon poly5 = new Polygon();
        Polygon poly6 = new Polygon();
        Polygon poly7 = new Polygon();
        Polygon poly8 = new Polygon();
        Polygon poly9 = new Polygon();
        Polygon poly10 = new Polygon();
        float[] vertices = new float[]{
                0,0,20,0,20,20,0,20
        };
        float[] vertices2 = new float[]{
                0,0,1920,0,1920,1080,0,1080
        };
        float[] vertices3 = new float[]{
                400,425,
                450,375,
                450,400,
                400,450,
                350,400,
        };
        poly1.setVertices(vertices);
        poly1.setPosition(400,10);
        poly2.setVertices(vertices);
        poly2.setPosition(400,40);
        poly3.setVertices(vertices);
        poly3.setPosition(10,900);
        poly4.setVertices(vertices);
        poly4.setPosition(900,900);
        poly5.setVertices(vertices);
        poly5.setPosition(100,100);
        poly6.setVertices(vertices);
        poly6.setPosition(800,100);
        poly7.setVertices(vertices);
        poly7.setPosition(300,400);
        poly8.setVertices(vertices);
        poly8.setPosition(100,600);
        poly9.setVertices(vertices3);
        poly10.setVertices(vertices2);
        poly10.setPosition(0,0);

        /*for(int i = 1; i < 25; i++){
            for(int j = 1; j < 25; j++) {
                Polygon p = new Polygon(vertices);
                p.setPosition(i * 50, j * 50);
                polyList.add(p);
            }
        }*/
        polyList.add(poly1);
        polyList.add(poly2);
        polyList.add(poly3);
        polyList.add(poly4);
        polyList.add(poly5);
        polyList.add(poly6);
        polyList.add(poly7);
        polyList.add(poly8);
        polyList.add(poly9);
        polyList.add(poly10);
        VisibilityV2.convertPolysToLines(polyList);
        VisibilityV2.convertPolysToLines(polyList);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.DARK_GRAY);
        setMouse();
        getVisPoly();
        drawScene();
    }

    public void getVisPoly(){
        if(mouse.x<1920&&mouse.x>0&&mouse.y>0&&mouse.y<1080){
            long timer = System.currentTimeMillis();
            vis = VisibilityV2.visibility_polygon(mouse, polyList);
            System.out.println(System.currentTimeMillis()-timer);
        }else{
            vis = VisibilityV2.visibility_polygon(new Vector2(400,400),polyList);
        }
        float[] visArray = new float[vis.size()*2];
        for(int i = 0; i < vis.size();i++){
            visArray[2*i] = vis.get(i).x;
            visArray[2*i+1] = vis.get(i).y;
        }
        if(visArray.length>6){
            visPoly.setVertices(visArray);
        }
    }

    public void setMouse(){
        mouse.set(Gdx.input.getX(),Gdx.input.getY());
        viewport.unproject(mouse);
    }

    public void drawScene(){
        batch.setProjectionMatrix(camera.combined);
        vfxManager.cleanUpBuffers();
        vfxManager.beginInputCapture();
        batch.begin();
        sd.setColor(Color.YELLOW);
        if(visPoly.getVertices().length > 6)sd.filledPolygon(visPoly);
        batch.end();
        vfxManager.endInputCapture();
        vfxManager.applyEffects();
        vfxManager.renderToScreen();
        batch.begin();

        if(debug)drawDebugLines();
        sd.setColor(Color.BLACK);
        Vector2 temp = new Vector2();
        a: for (int i = 0; i < polyList.size()-1;i++) {
            b: for(int j = 0; j < polyList.get(i).getVertexCount();j++){
                polyList.get(i).getVertex(j,temp);
                if(visPoly.contains(temp)){
                    sd.filledPolygon(polyList.get(i));
                    continue a;
                }
            }
        }
        sd.setColor(Color.RED);
        sd.filledCircle(mouse,5);
        batch.end();
    }

    public void drawDebugLines(){
        sd.setColor(Color.CYAN);
        sd.setDefaultLineWidth(5);
        float[] vertices = visPoly.getTransformedVertices();
        for(int i = 0; i < vertices.length-1;i+=2) {
            if(i == vertices.length-2){
                //figure out the current line segment
                sd.line(new Vector2(vertices[i],vertices[i+1]),new Vector2(vertices[0],vertices[1]));
            }else{
                //figure out the current line segment
                sd.line(new Vector2(vertices[i],vertices[i+1]),new Vector2(vertices[i+2],vertices[i+3]));
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        viewport.update(width,height);
        vfxManager.resize(width, height);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.D){
            debug = !debug;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        camera.zoom+=(amountY/10f);
        camera.update();
        return false;
    }
}

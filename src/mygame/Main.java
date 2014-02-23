package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import nodes.Explosion;
import nodes.SealField;

/**
 * Sample 8 - how to let the user pick (select) objects in the scene using the
 * mouse or key presses. Can be used for shooting, opening doors, etc.
 */
public class Main extends SimpleApplication {

    private Explosion expl;

    public static void main(String[] args) {


        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        flyCam.setMoveSpeed(50);
        

        initCrossHairs(); // a "+" in the middle of the screen to help aiming

        /**
         * create four colored boxes and a floor to shoot at:
         */
        //rootNode.attachChild(makeFloor());
        SealField sealField = new SealField(assetManager, inputManager, cam, 10);

        sealField.setLocalTranslation(-40, 0, -40);

        rootNode.attachChild(sealField);

        rootNode.attachChild(assetManager.loadModel("Scenes/world.j3o"));
//        rootNode.attachChild(SkyFactory.createSky(
//                assetManager, "Textures/Sky/skybox.jpg", false));

        
    }

    @Override
    public void simpleUpdate(float tpf) {
    }

    /**
     * A floor to show that the "shot" can go through several objects.
     */
    protected Geometry makeFloor() {
        Box box = new Box(50f, .1f, 50f);
        Geometry floor = new Geometry("the Floor", box);
        floor.setLocalTranslation(0f, 0f, 0f);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Gray);
        floor.setMaterial(mat1);
        return floor;
    }

    /**
     * A centred plus sign to help the player aim.
     */
    protected void initCrossHairs() {
        setDisplayStatView(false);
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
                settings.getWidth() / 2 - ch.getLineWidth() / 2, settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
        guiNode.attachChild(ch);
    }
}
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import nodes.Explosion;
import nodes.SealField;

/**
 * Sample 8 - how to let the user pick (select) objects in the scene using the
 * mouse or key presses. Can be used for shooting, opening doors, etc.
 */
public class Main extends SimpleApplication implements ScreenController {

    private Explosion expl;
    private static Main context;

    public static void main(String[] args) {

        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        context = this;

        flyCam.setMoveSpeed(50);
        inputManager.addMapping("PRINT",
                new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
        inputManager.addListener(new ActionListener() {
            public void onAction(String name, boolean isPressed, float tpf) {
                System.err.println(cam.getRotation());
                System.err.println(cam.getLocation());
            }
        }, "PRINT");
        cam.setLocation(new Vector3f(-14.938178f, 38.859467f, -66.52032f));
        cam.setRotation(new Quaternion(0.32675794f, -6.513824E-4f, 1.0827737E-5f, 0.9451078f));
        initCrossHairs(); // a "+" in the middle of the screen to help aiming

        /**
         * create four colored boxes and a floor to shoot at:
         */
        //rootNode.attachChild(makeFloor());
        SealField sealField = new SealField(assetManager, inputManager, cam, 10);

        sealField.setLocalTranslation(-40, 0, -40);

        rootNode.attachChild(sealField);

        rootNode.attachChild(assetManager.loadModel("Scenes/world.j3o"));

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

    public static Main getApp() {
        return context;
    }
    private Nifty nifty;
    private Screen screen;

    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    public void onStartScreen() {
    }

    public void onEndScreen() {
    }

    public void exit() {

        System.exit(0);
        
    }
}
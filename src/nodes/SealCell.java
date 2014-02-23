/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nodes;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import java.util.Random;

/**
 *
 * @author Kage
 */
public class SealCell extends Node implements Cell, AnimEventListener {

    /*
     * Context
     */
    private AssetManager assetManager;
    private AnimControl control;
    private AnimChannel channel;
    private SealField sealField;
    /*
     * Mesh params
     */
    public static final float width = 5f;
    /*
     * Cell params
     */
    private boolean marked = false;
    private boolean selected = false;
    private boolean opened = false;
    /*
     * Child objects
     */
    private Node mark;
    private Node value;
    private int numericValue;
    private int row;
    private int col;
    /*
     * Cell model
     */
    private Node model;

    public SealCell(String name, float x, float y, float z, AssetManager assetManager, int row, int col, int value, SealField sealField) {

        super("cell." + row + "." + col);

        this.assetManager = assetManager;
        this.numericValue = value;
        this.row = row;
        this.col = col;
        this.sealField = sealField;
        
        setLocalTranslation(x, y, z);

        setQueueBucket(RenderQueue.Bucket.Transparent);

        model = (Node) assetManager.loadModel("Models/seal.j3o");
        Material material = new Material(
                assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setTexture("ColorMap", assetManager.loadTexture("Textures/Minefield/Cell/normal.png"));
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        model.setLocalTranslation(0f, 1f, 0);
        model.setLocalScale(2f);
        model.setQueueBucket(Bucket.Transparent);
        model.setMaterial(material);


        control = model.getChild("Cube").getControl(AnimControl.class);
        channel = control.createChannel();

        channel.setAnim("Normal");

        attachChild(model);

    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void select() {

        setSelected(true);

    }

    public void deselect() {

        setSelected(false);

    }

    public void open() {

        opened = true;

        
        if(getValue() != null)
            attachChild(getValue());

        channel.setLoopMode(LoopMode.DontLoop);
        
        sealField.checkCompletion();
    }

    public void mark() {

        channel.setLoopMode(LoopMode.DontLoop);

        attachChild(getMark());

        setMarked(true);

        sealField.checkCompletion();
    }

    public void unmark() {

        channel.setLoopMode(LoopMode.Loop);

        detachChild(getMark());
        setMarked(false);

    }

    public Node getMark() {

        if (mark == null) {
            initMark();
        }

        return mark;

    }

    public Node getValue() {

        if (value == null) {
            initValue();
        }

        return value;

    }

    private void initMark() {
//        Sphere sphere = new Sphere(30, 30, 0.2f);
//        mark = new Geometry("Mark", sphere);
//        mark.setLocalTranslation(0, 1f, 0);
//        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        mark_mat.setColor("Color", ColorRGBA.Red);
//        mark.setMaterial(mark_mat);

        Node model = (Node) assetManager.loadModel("Models/seal.j3o");
        model.setLocalTranslation(0f, 1.3f, 0);
        model.setLocalScale(2.3f);
        Material material = new Material(
                assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setTexture("ColorMap", assetManager.loadTexture("Textures/Minefield/Cell/locked.png"));
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        model.setQueueBucket(Bucket.Translucent);
        model.setMaterial(material);
        mark = model;
    }

    private void initValue() {

        Material material = new Material(
                assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        if (numericValue != 0) {
            material.setTexture("ColorMap", assetManager.loadTexture("Textures/Minefield/Cell/opened.png"));
        } else {
            sealField.openedEmptyCell(row, col);
            material.setTexture("ColorMap", assetManager.loadTexture("Textures/Minefield/Cell/empty.png"));
        }
        model.setLocalScale(2.5f);
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        model.setQueueBucket(Bucket.Transparent);
        model.setMaterial(material);

        if (numericValue != 0) {
            Node model = (Node) assetManager.loadModel("Models/seal.j3o");
            model.setLocalTranslation(0f, 1.3f, 0);
            model.setLocalScale(2f);
            material = new Material(
                    assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            if (numericValue > 0 && numericValue < 9) {
                material.setTexture("ColorMap", assetManager.loadTexture("Textures/Minefield/Cell/Text/" + numericValue + ".png"));
            } else {
                sealField.bombFound();
                material.setTexture("ColorMap", assetManager.loadTexture("Textures/Minefield/Cell/burned.png"));
            }
            material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            model.setQueueBucket(Bucket.Translucent);
            model.setMaterial(material);
            value = model;
            
            sealField.openedCell(row, col);
            //channel.setAnim("Open");
        }
        
    }

    @Override
    public String toString() {
        return getName();
    }

    public boolean isMarked() {
        return marked;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isOpened() {
        return opened;
    }

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
    }

    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {

        if (animName.equals("Open")) {
        }

    }
    
    public int getRow(){
        return row;
    }
    
    public int getCol(){
        return col;
    }
}

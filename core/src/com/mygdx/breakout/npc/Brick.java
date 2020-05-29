package com.mygdx.breakout.npc;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Brick {

    private float xPos;
    private float yPos;
    private Sprite brickImage;
    private boolean destroyed;
    private Body body;
    private PolygonShape shape;

    public Brick(float xPos, float yPos, Sprite brickImage, boolean destroyed) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.brickImage = brickImage;
        this.destroyed = destroyed;
    }

    public float getXPos() {
        return xPos;
    }

    public void setXPos(float xPos) {
        this.xPos = xPos;
    }

    public float getYPos() {
        return yPos;
    }

    public void setyPos(float yPos) {
        this.yPos = yPos;
    }

    public Sprite getBrickImage() {
        return brickImage;
    }

    public void setBrickImage(Sprite brickImage) {
        this.brickImage = brickImage;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public Body getBody() { return body; }

    public void setBody(Body body) {
        this.body = body;
    }

    public PolygonShape getShape() { return shape; }

    public void setShape(PolygonShape shape) {
        this.shape = shape;
    }



}

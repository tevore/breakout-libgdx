package com.mygdx.breakout.npc;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Ball {

    private float xPos;
    private float yPos;
    private Texture ballImage;
    private Body body;
    private PolygonShape shape;
    private boolean launched;

    public Ball(float xPos, float yPos, Texture ballImage, boolean launched) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.ballImage = ballImage;
        this.launched = launched;
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

    public void setYPos(float yPos) {
        this.yPos = yPos;
    }

    public Texture getBallImage() {
        return ballImage;
    }

    public void setBallImage(Texture ballImage) {
        this.ballImage = ballImage;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public PolygonShape getShape() {
        return shape;
    }

    public void setShape(PolygonShape shape) {
        this.shape = shape;
    }

    public boolean isLaunched() {
        return launched;
    }

    public void setLaunched(boolean launched) {
        this.launched = launched;
    }
}

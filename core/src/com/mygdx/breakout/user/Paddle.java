package com.mygdx.breakout.user;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Paddle {

    private float xPos;
    private float yPos;
    private Texture paddleImage;
    private Body body;
    private PolygonShape shape;

    public Paddle(float xPos, float yPos, Texture paddleImage) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.paddleImage = paddleImage;
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

    public Texture getPaddleImage() {
        return paddleImage;
    }

    public void setPaddleImage(Texture paddleImage) {
        this.paddleImage = paddleImage;
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
}

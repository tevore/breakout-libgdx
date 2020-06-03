package com.mygdx.breakout.levels;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.breakout.Level;
import com.mygdx.breakout.npc.Brick;

public class Level1 implements Level {

    private int level;

    public Level1() {
        this.level = 1;
    }

    @Override
    public int getLevelNum() {
        return level;
    }

    @Override
    public void generateLevel(TextureAtlas atlas, float gameViewHeight, Brick[][] brickArray, World world) {


        //assign where bricks will be place
        float rowAdvanceXSpace = 30;
        float rowAdvanceYSpace = gameViewHeight/2;


        for(int i = 0; i < brickArray.length; i++) {
            for(int j = 0; j < brickArray[0].length; j++) {
//                if((int)Math.round(Math.random()) == 1) {
                    brickArray[i][j] = new Brick(rowAdvanceXSpace, rowAdvanceYSpace,
                            atlas.createSprite("brick_extra"), false);
//                } else {
//                    brickArray[i][j] = new Brick(rowAdvanceXSpace, rowAdvanceYSpace, null, true);
//                }
                rowAdvanceXSpace += 20;
            }
            rowAdvanceXSpace = 30;
            rowAdvanceYSpace += 5;
        }


        //craft the physics for those bricks
        for(int i = 0; i < brickArray.length; i++) {
            for(int j = 0; j < brickArray[0].length; j++) {

                if(brickArray[i][j].getBrickImage() != null) {
                    BodyDef brickBodyDef = new BodyDef();
                    brickBodyDef.type = BodyDef.BodyType.StaticBody;
                    brickBodyDef.position.set(brickArray[i][j].getXPos(), brickArray[i][j].getYPos());
                    Body brickBody = world.createBody(brickBodyDef);
                    brickArray[i][j].setBody(brickBody);
                    brickBody.setUserData(brickArray[i][j]);

                    PolygonShape brickShape = new PolygonShape();
                    brickArray[i][j].setShape(brickShape);
                    brickShape.setAsBox(10, 2);

                    FixtureDef fixtureDef = new FixtureDef();
                    fixtureDef.shape = brickShape;
                    fixtureDef.density = 30f;
                    fixtureDef.friction = 0f;
                    fixtureDef.restitution = 5f;

                    brickBody.createFixture(fixtureDef);
                }
            }
        }


    }
}

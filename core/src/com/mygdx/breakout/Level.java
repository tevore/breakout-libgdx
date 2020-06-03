package com.mygdx.breakout;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.breakout.npc.Brick;

public interface Level {

    int getLevelNum();

    //level generation strategy
    void generateLevel(TextureAtlas atlas, float rowHeight, Brick[][] brickArray, World world);

//    float rowAdvanceXSpace = 30;
//    float rowAdvanceYSpace = gameViewHeight/2;




//        for(int i = 0; i < brickCoordinateArray.length; i++) {
//        for(int j = 0; j < brickCoordinateArray[0].length; j++) {
//            if((int)Math.round(Math.random()) == 1) {
//                brickCoordinateArray[i][j] = new Brick(rowAdvanceXSpace, rowAdvanceYSpace,
//                        textureAtlas.createSprite("brick"), false);
//            } else {
//                brickCoordinateArray[i][j] = new Brick(rowAdvanceXSpace, rowAdvanceYSpace, null, true);
//            }
//            rowAdvanceXSpace += 20;
//        }
//        rowAdvanceXSpace = 30;
//        rowAdvanceYSpace += 4;
//    }
}

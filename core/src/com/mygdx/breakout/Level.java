package com.mygdx.breakout;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.breakout.npc.Brick;

public interface Level {

    int getLevelNum();

    //level generation strategy
    void generateLevel(TextureAtlas atlas, float rowHeight, Brick[][] brickArray, World world);
}

package com.mygdx.breakout;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.breakout.levels.Level1;
import com.mygdx.breakout.npc.Brick;

import java.util.LinkedList;

public class LevelManager {

    private LinkedList<Level> levels;

    public LevelManager() {
        levels = new LinkedList<>();
    }

    public void addLevels() {
        levels.add(new Level1());
    }

    public int getCurrentLevel() {
        return levels.getFirst().getLevelNum();
    }

    public void generateLevelDetails(TextureAtlas atlas, float rowHeight, Brick[][] brickArray, World world) {
        levels.getFirst().generateLevel(atlas, rowHeight, brickArray, world);
    }
}

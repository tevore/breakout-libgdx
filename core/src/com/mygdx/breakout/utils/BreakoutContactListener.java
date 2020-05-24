package com.mygdx.breakout.utils;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.breakout.npc.Brick;

public class BreakoutContactListener implements ContactListener {

    public Array<Body> removables = new Array<>();

    @Override
    public void beginContact(Contact contact) {


                if (contact.getFixtureA().getBody().getUserData() != null) {
                    Body contactedBody = contact.getFixtureA().getBody();
                    if (contactedBody.getUserData().getClass().isAssignableFrom(Brick.class)) {
                        Brick impactedBrick = (Brick) contactedBody.getUserData();
                        impactedBrick.setDestroyed(true);
                        removables.add(contactedBody);
                    }
                }


    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}

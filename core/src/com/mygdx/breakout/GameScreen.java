package com.mygdx.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.breakout.npc.Ball;
import com.mygdx.breakout.npc.Brick;
import com.mygdx.breakout.user.Paddle;
import com.mygdx.breakout.utils.BreakoutContactListener;


/* TODO
    Review physics values for balls, paddle, bricks
    attach user data to Paddle generation
    Add score
    Add level
    Add lives
 */

public class GameScreen implements Screen {
    final Breakout game;

    int minVelocity = 45;
    int maxVelocity = 90;

    int gameViewWidth = 200;
    int gameViewHeight = 150;

    int paddleMoveSpeed = 50;
    float restitution = 0.45f;

    TextureAtlas textureAtlas;

    OrthographicCamera camera;

    Ball ball;
    Paddle paddle;
    Brick[][] brickCoordinateArray = new Brick[8][8];

    World world;
    Box2DDebugRenderer debugRenderer;

    CircleShape circle;
    PolygonShape paddleShape;


    Array<Body> removables;
    Array<Contact> contacts;
    BreakoutContactListener breakoutContactListener;


    Sprite pad;
    PolygonShape padShape;
    Body padBody;

    Sprite bola;
    CircleShape bolaShape;
    Body bolaBody;

    Sprite rightWall;
    PolygonShape rightWallShape;
    Body rightWallBody;

    Sprite leftWall;
    PolygonShape leftWallShape;
    Body leftWallBody;

    Sprite ceiling;
    PolygonShape ceilingShape;
    Body ceilingBody;

    public GameScreen(Breakout game) {
        this.game = game;

        textureAtlas = new TextureAtlas("arkanoid_spritesheet.atlas");
        pad = textureAtlas.createSprite("paddle");
        bola = textureAtlas.createSprite("ball");
        rightWall = textureAtlas.createSprite("brick");
        leftWall = textureAtlas.createSprite("brick");
        ceiling = textureAtlas.createSprite("brick");

        camera = new OrthographicCamera();
        //sets camera centered on width and height args
        camera.setToOrtho(false, gameViewWidth, gameViewHeight);

        setupBricks();

        //set up a world for physics
        Box2D.init();

        world = new World(new Vector2(0, 0), true);
//        world.setContinuousPhysics(true);
        World.setVelocityThreshold(0);
        breakoutContactListener = new BreakoutContactListener();
        world.setContactListener(breakoutContactListener);
        debugRenderer = new Box2DDebugRenderer();

        setUpPaddle();
        setUpBall();

        setupBrickPhysics();

        setUpBoundaries();

        removables = new Array<>();
        contacts = new Array<>();

    }

    private void setUpBoundaries() {
//        set up boundaries
        //right wall
        rightWall.setPosition(gameViewWidth, 0);

        BodyDef rightWallDef = new BodyDef();
        rightWallDef.type = BodyDef.BodyType.StaticBody;
        rightWallDef.position.set(rightWall.getX(), rightWall.getY()); //y was 20

        rightWallBody = world.createBody(rightWallDef);

        rightWallShape = new PolygonShape();
        rightWallShape.setAsBox(2f, gameViewHeight);

        FixtureDef fixtureDefRW = new FixtureDef();
        fixtureDefRW.shape = rightWallShape;
        fixtureDefRW.density = 100f;
        fixtureDefRW.friction = 0f;
        fixtureDefRW.restitution = restitution;

        rightWallBody.createFixture(fixtureDefRW);
        rightWallShape.dispose();

        //left wall
        leftWall.setPosition(0, 0);

        BodyDef leftWallDef = new BodyDef();
        leftWallDef.type = BodyDef.BodyType.StaticBody;
        leftWallDef.position.set(leftWall.getX(), leftWall.getY()); //y was 20

        leftWallBody = world.createBody(leftWallDef);

        leftWallShape = new PolygonShape();
        leftWallShape.setAsBox(2f, 200f);

        FixtureDef fixtureDefLW = new FixtureDef();
        fixtureDefLW.shape = leftWallShape;
        fixtureDefLW.density = 100f;
        fixtureDefLW.friction = 0f;
        fixtureDefLW.restitution = restitution;

        leftWallBody.createFixture(fixtureDefLW);
        leftWallShape.dispose();

        //ceiling
        ceiling.setPosition(0, gameViewHeight);

        BodyDef ceilingDef = new BodyDef();
        ceilingDef.type = BodyDef.BodyType.StaticBody;
        ceilingDef.position.set(ceiling.getX(), ceiling.getY()); //y was 20

        ceilingBody = world.createBody(ceilingDef);

        ceilingShape = new PolygonShape();
        ceilingShape.setAsBox(gameViewWidth, 2);

        FixtureDef fixtureDefCeil = new FixtureDef();
        fixtureDefCeil.shape = ceilingShape;
        fixtureDefCeil.density = 100f;
        fixtureDefCeil.friction = 0f;
        fixtureDefCeil.restitution = restitution;

        ceilingBody.createFixture(fixtureDefCeil);
        ceilingShape.dispose();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {

//        System.out.println(ball.getBody().getLinearVelocity().toString());

        padBody.setLinearVelocity(0,0);
        if(!ball.isLaunched()) {
            ball.getBody().setLinearVelocity(0, 0);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) ) {
            padBody.setLinearVelocity(-paddleMoveSpeed, 0);
            if(!ball.isLaunched()) {
                ball.getBody().setLinearVelocity(-paddleMoveSpeed, 0.0f);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            padBody.setLinearVelocity(paddleMoveSpeed, 0);
            if(!ball.isLaunched()) {
                ball.getBody().setLinearVelocity(paddleMoveSpeed, 0.0f);
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && !ball.isLaunched()) {
            ball.getBody().applyLinearImpulse(new Vector2(padBody.getLinearVelocity().x, 200f), new Vector2(ball.getBody().getPosition().x, ball.getBody().getPosition().y), true);
            ball.setLaunched(true);
        }

        //check if ball went out of bounds and reset
        if(ball.getBody().getPosition().y < -10) {
//            System.out.println("BALL IS OUT OF BOUNDS. Current y =  " + ball.getBody().getPosition().y);
            ball.getBody().setLinearVelocity(0, 0);
            ball.setLaunched(false);
            ball.getBody().setTransform(pad.getX()+10, pad.getY()+6, ball.getBody().getAngle());
        }

        //enforce a max velocity on the ball
        Vector2 currentBallVelocity = ball.getBody().getLinearVelocity();

        if(ball.isLaunched()) {
            if (currentBallVelocity.x > maxVelocity) {
                ball.getBody().setLinearVelocity(new Vector2(maxVelocity, currentBallVelocity.y));
            } else if (currentBallVelocity.x < -maxVelocity) {
                ball.getBody().setLinearVelocity(new Vector2(-maxVelocity, currentBallVelocity.y));
            }

            if (currentBallVelocity.y > maxVelocity) {
                ball.getBody().setLinearVelocity(new Vector2(currentBallVelocity.x, maxVelocity));
            } else if (currentBallVelocity.y < -maxVelocity) {
                ball.getBody().setLinearVelocity(new Vector2(currentBallVelocity.x, -maxVelocity));
            }

            //enforce a min velocity as well
            if (currentBallVelocity.x < minVelocity && currentBallVelocity.x >= 0) {
                ball.getBody().setLinearVelocity(new Vector2(minVelocity, currentBallVelocity.y));
            } else if (currentBallVelocity.x > -minVelocity && currentBallVelocity.x <= 0) {
                ball.getBody().setLinearVelocity(new Vector2(-minVelocity, currentBallVelocity.y));
            }

            if (currentBallVelocity.y < minVelocity && currentBallVelocity.y >= 0) {
                ball.getBody().setLinearVelocity(new Vector2(currentBallVelocity.x, minVelocity));
            } else if (currentBallVelocity.y > -minVelocity && currentBallVelocity.y <= 0) {
                ball.getBody().setLinearVelocity(new Vector2(currentBallVelocity.x, -minVelocity));
            }
        }

        world.step(Gdx.graphics.getDeltaTime(), 6, 2);


        contacts.addAll(world.getContactList());

        for(Body removable : breakoutContactListener.removables) {
            world.destroyBody(removable);
        }

        breakoutContactListener.removables.clear();

        Gdx.gl.glClearColor(0, 0, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell the camera to update its matrices.
        camera.update();

        game.batch.begin();

        //bricks
        for (int i = 0; i < brickCoordinateArray.length; i++) {
            for (int j = 0; j < brickCoordinateArray[0].length; j++) {
                if (!brickCoordinateArray[i][j].isDestroyed()) {
                    Sprite currentBrick = brickCoordinateArray[i][j].getBrickImage();
                    Body currentBrickBody = brickCoordinateArray[i][j].getBody();
                    currentBrick.setSize(20, 5);
                    currentBrick.setPosition(currentBrickBody.getPosition().x-10, currentBrickBody.getPosition().y-2.5f);
                    currentBrick.setOriginCenter();
                    currentBrick.draw(game.batch);
                }
            }
        }

        //walls
        rightWall.setSize(2, gameViewHeight);
        rightWall.setOriginCenter();
        rightWall.setPosition(rightWallBody.getPosition().x, rightWallBody.getPosition().y);
        rightWall.draw(game.batch);

        leftWall.setSize(2, gameViewHeight);
        leftWall.setOriginCenter();
        leftWall.setPosition(leftWallBody.getPosition().x, leftWallBody.getPosition().y);
        leftWall.draw(game.batch);

        ceiling.setSize(gameViewWidth, 2);
        ceiling.setOriginCenter();
        ceiling.setPosition(ceilingBody.getPosition().x, ceilingBody.getPosition().y);
        ceiling.draw(game.batch);

        //paddle
        pad.setSize(20, 5);

        pad.setOriginCenter();
        pad.setPosition(padBody.getPosition().x - 9.5f, padBody.getPosition().y - 2);
        pad.draw(game.batch);

        //ball
        ball.getBallImage().setSize(4, 4);
        ball.getBallImage().setOriginCenter();
        ball.getBallImage().setPosition(ball.getBody().getPosition().x - 2, ball.getBody().getPosition().y - 2);
        ball.getBallImage().draw(game.batch);

        game.batch.end();

//        debugRenderer.render(world, camera.combined);
    }


    private void setupBrickPhysics() {

          for(int i = 0; i < brickCoordinateArray.length; i++) {
              for(int j = 0; j < brickCoordinateArray[0].length; j++) {

                  if(brickCoordinateArray[i][j].getBrickImage() != null) {
                      BodyDef brickBodyDef = new BodyDef();
                      brickBodyDef.type = BodyDef.BodyType.StaticBody;
                      brickBodyDef.position.set(brickCoordinateArray[i][j].getXPos(), brickCoordinateArray[i][j].getYPos());
                      Body brickBody = world.createBody(brickBodyDef);
                      brickCoordinateArray[i][j].setBody(brickBody);
                      brickBody.setUserData(brickCoordinateArray[i][j]);

                      PolygonShape brickShape = new PolygonShape();
                      brickCoordinateArray[i][j].setShape(brickShape);
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

    private void setUpBall() {

        //set position to be same as paddle to start
        bola.setPosition(pad.getX(), pad.getY() + 5);

        BodyDef bolaBodyDef = new BodyDef();
        bolaBodyDef.type = BodyDef.BodyType.DynamicBody;
        //to not rotate around the axis
//        bolaBodyDef.fixedRotation = true;
        //TODO review this positional code
        bolaBodyDef.position.set(bola.getX(), bola.getY()+2); //y was 20

        bolaBody = world.createBody(bolaBodyDef);

        bolaShape = new CircleShape();
        //Useful for linking movement between rendered sprite and attached physics component
//        padShape.setAsBox(9.5f, 2);
        bolaShape.setRadius(1.5f);

        FixtureDef fixtureDefBola = new FixtureDef();
        fixtureDefBola.shape = bolaShape;
        fixtureDefBola.density = 0f;
        fixtureDefBola.friction = 0f;
        fixtureDefBola.restitution = restitution;

        bolaBody.createFixture(fixtureDefBola);

        ball = new Ball(bola.getX(), bola.getY(), bola, bolaBody, false);
        bolaBody.setUserData(ball);

        bolaShape.dispose();
    }

    private void setUpPaddle() {

        pad.setPosition(50, 20);

        BodyDef padBodyDef = new BodyDef();
        padBodyDef.type = BodyDef.BodyType.KinematicBody;
        //to not rotate around the axis
        padBodyDef.fixedRotation = true;
        //TODO review this positional code
        padBodyDef.position.set(pad.getX(), pad.getY()+2); //y was 20

        padBody = world.createBody(padBodyDef);

        padShape = new PolygonShape();
        //Useful for linking movement between rendered sprite and attached physics component
        padShape.setAsBox(9.5f, 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = padShape;
        fixtureDef.density = 100f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = restitution;


        padBody.createFixture(fixtureDef);
        padShape.dispose();
    }

    private void setupBricks() {

        float rowAdvanceXSpace = 30;
        float rowAdvanceYSpace = gameViewHeight/2;



        //TODO make brick null if not created?
        for(int i = 0; i < brickCoordinateArray.length; i++) {
            for(int j = 0; j < brickCoordinateArray[0].length; j++) {
                if((int)Math.round(Math.random()) == 1) {
                    brickCoordinateArray[i][j] = new Brick(rowAdvanceXSpace, rowAdvanceYSpace,
                            textureAtlas.createSprite("brick"), false);
                } else {
                    brickCoordinateArray[i][j] = new Brick(rowAdvanceXSpace, rowAdvanceYSpace, null, true);
                }
                rowAdvanceXSpace += 20;
            }
            rowAdvanceXSpace = 30;
            rowAdvanceYSpace += 4;
        }


//                for(int i = 0; i < brickCoordinateArray.length; i++) {
//            for(int j = 0; j < brickCoordinateArray[0].length; j++) {
//                System.out.print(brickCoordinateArray[i][j].isDestroyed() + " ");
//            }
//            System.out.println("");
//        }


    }

    @Override
    public void resize(int width, int height) {
        game.batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        textureAtlas.dispose();

        //dispose of all shapes in brick coordinate array
        for(int i = 0; i < brickCoordinateArray.length; i++) {
            for(int j = 0; j < brickCoordinateArray[0].length; j++) {
                brickCoordinateArray[i][j].getShape().dispose();
            }
        }

        // Remember to dispose of any shapes after you're done with them!
        // BodyDef and FixtureDef don't need disposing, but shapes do.
        circle.dispose();
        paddleShape.dispose();

        leftWallShape.dispose();
        rightWallShape.dispose();
        ceilingShape.dispose();

        world.dispose();
    }
}

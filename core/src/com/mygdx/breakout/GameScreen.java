package com.mygdx.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.breakout.npc.Ball;
import com.mygdx.breakout.npc.Brick;
import com.mygdx.breakout.user.Paddle;
import com.mygdx.breakout.utils.BreakoutContactListener;


//TODO BRAND NEW TODOs
/* TODOs
    Fix physics values for balls, paddle, bricks
    create bricks
    add bricks and ball via texture atlas
    create wall bounds with some of the extra images stretched out?
    attach user data to Ball/Paddle/Brick generation
 */

//TODO add velocity every time the ball hits something
public class GameScreen implements Screen {
    final Breakout game;

    int gameViewWidth = 200;
    int gameViewHeight = 200;

    int paddleMoveSpeed = 50;

    TextureAtlas textureAtlas;

    OrthographicCamera camera;

    Ball ball;
    Paddle paddle;
    Brick[][] brickCoordinateArray = new Brick[8][6];

    World world;
    Box2DDebugRenderer debugRenderer;

    CircleShape circle;
    PolygonShape paddleShape;
    Body paddleBody;
    Body circleBody;


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
        world.setContinuousPhysics(true);
        World.setVelocityThreshold(0);
        breakoutContactListener = new BreakoutContactListener();
        world.setContactListener(breakoutContactListener);
        debugRenderer = new Box2DDebugRenderer();

//        setUpPaddle();
//        setUpBall();

        setupBrickPhysics();

        setUpBoundaries();

        removables = new Array<>();
        contacts = new Array<>();

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
        fixtureDef.density = 8f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 1f;


        padBody.createFixture(fixtureDef);
        padShape.dispose();



        bola.setPosition(50, 100);

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
        fixtureDefBola.density = 2.6f;
        fixtureDefBola.friction = 0f;
        fixtureDefBola.restitution = 1f;


        bolaBody.createFixture(fixtureDefBola);
        bolaShape.dispose();



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
        fixtureDefRW.restitution = 1f;


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
        fixtureDefLW.restitution = 1f;


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
        fixtureDefCeil.restitution = 1f;


        ceilingBody.createFixture(fixtureDefCeil);
        ceilingShape.dispose();

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {


//        world.set


        //do physics here?
//        paddleBody.setLinearVelocity(0, 0);
//
//        if(!ball.isLaunched()) {
//            circleBody.setLinearVelocity(0, 0);
//        }
//
//        //TODO do not apply linear velocity if paddle is colliding with a wall if ball is not launched

//        padBody.setLinearVelocity(padBody.getLinearVelocity().x, 0);
        padBody.setLinearVelocity(0,0);

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) ) {
//            paddleBody.setLinearVelocity(-paddleMoveSpeed, 0.0f);
            padBody.setLinearVelocity(-paddleMoveSpeed, 0);
//            if(!ball.isLaunched()) {
//                circleBody.setLinearVelocity(-paddleMoveSpeed, 0.0f);
//            }
        }
//
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
//            paddleBody.setLinearVelocity(paddleMoveSpeed, 0.0f);
            padBody.setLinearVelocity(paddleMoveSpeed, 0);
//            if(!ball.isLaunched()) {
//                circleBody.setLinearVelocity(paddleMoveSpeed, 0.0f);
//            }
        }

        //TESTING PURPOSES
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            bolaBody.applyLinearImpulse(new Vector2(0, 100f), new Vector2(bolaBody.getPosition().x, bolaBody.getPosition().y), true);
        }
//
//        //TODO review this instant addition of velocity
//        if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && !ball.isLaunched()) {
//            ball.setLaunched(true);
//            circleBody.applyLinearImpulse(circleBody.getLinearVelocity().x * 2, 10f, 0, 0, true);
//        }
//
//        //TODO apply force velocity in magnitude ever so slightly every tick
//        if(ball.isLaunched() && circleBody.getLinearVelocity().y == 0) {
//            System.out.println("Applying linear impulse!");
//            circleBody.applyLinearImpulse(circleBody.getLinearVelocity().x, 10f, 0, 0, true);
//        }
//
//
//
//
//        paddle.setXPos(paddleBody.getPosition().x - ((paddleImage.getWidth()/4/2)));
//        paddle.setYPos(paddleBody.getPosition().y - ((paddleImage.getHeight()/4/2)));
//
//        ball.setXPos(circleBody.getPosition().x - ((ballImage.getWidth() / 4) / 2)); //this alone sets the image and body together
//        ball.setYPos(circleBody.getPosition().y - ((ballImage.getHeight()/4/2)));

//TODO assign some impulse force in the opposite x direction when ball impacted


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

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
//        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        //We divide the height and width of the image by 4 since it is rather large. hence, all calculations needs to go that way as well

        //TODO review this positional code as well
//        for (int i = 0; i < brickCoordinateArray.length; i++) {
//            for (int j = 0; j < brickCoordinateArray[0].length; j++) {
//                if (!brickCoordinateArray[i][j].isDestroyed()) {
//                    game.batch.draw(
//                            brickCoordinateArray[i][j].getBrickImage(),
//                            brickCoordinateArray[i][j].getXPos(),
//                            brickCoordinateArray[i][j].getYPos(),
//                            brickCoordinateArray[i][j].getBrickImage().getWidth(),
//                            brickCoordinateArray[i][j].getBrickImage().getHeight());
//                }
//
//            }
//        }



//        game.batch.draw(paddleImage, paddle.getXPos(), paddle.getYPos(), paddleImage.getWidth() / 4, paddleImage.getHeight() / 4);
//        game.batch.draw(paddleImage, paddle.getXPos(), paddle.getYPos(), paddleImage.getWidth(), paddleImage.getHeight());


        //have the ball follow the paddle if it is not launched yet

//        game.batch.draw(ballImage, ball.getXPos(), ball.getYPos(), ballImage.getWidth(), ballImage.getHeight());

        pad.setSize(20, 5);

        pad.setOriginCenter();
        pad.setPosition(padBody.getPosition().x - 9.5f, padBody.getPosition().y - 2);
        pad.draw(game.batch);

        bola.setSize(4, 4);
        bola.setOriginCenter();
        bola.setPosition(bolaBody.getPosition().x, bolaBody.getPosition().y);
        bola.draw(game.batch);

        game.batch.end();


        debugRenderer.render(world, camera.combined);

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
                      brickShape.setAsBox(5, 2);

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

        // First we create a body definition
        BodyDef circleBodyDef = new BodyDef();
        // We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        circleBodyDef.type = BodyDef.BodyType.DynamicBody;
        // Set our body's starting position in the world
        //TODO move to be centered on top of our paddle
        circleBodyDef.position.set(paddleBody.getPosition().x, (paddleBody.getPosition().y + 25));

        // Create our body in the world using our body definition
        circleBody = world.createBody(circleBodyDef);
//        circleBody.setUserData();

        circle = new CircleShape();
        circle.setRadius(15f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 5f;

        // Create our fixture and attach it to the body
        circleBody.createFixture(fixtureDef);

//        ball = new Ball(circleBody.getPosition().x, circleBody.getPosition().y, ballImage, false);
        circleBody.setUserData(ball);

    }

    private void setUpPaddle() {

        BodyDef paddleBodyDef = new BodyDef();
        paddleBodyDef.type = BodyDef.BodyType.DynamicBody;
        //to not rotate around the axis
        paddleBodyDef.fixedRotation = true;
        //TODO review this positional code
//        paddleBodyDef.position.set(new Vector2((100/2 - ((paddleImage.getWidth()/8)/2)) , 1)); //y was 20

        paddleBody = world.createBody(paddleBodyDef);

        paddleShape = new PolygonShape();
        //Useful for linking movement between rendered sprite and attached physics component
//        paddleShape.setAsBox(paddleImage.getWidth()/8, paddleImage.getHeight()/8);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = paddleShape;
        fixtureDef.density = 100f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 5f;

        paddleBody.createFixture(fixtureDef);

//        paddle = new Paddle(paddleBody.getPosition().x, paddleBody.getPosition().y, paddleImage);
        paddleBody.setUserData(paddle);
    }

    private void setupBricks() {

        float rowAdvanceXSpace = 100;
        float rowAdvanceYSpace = 100;



        //TODO make brick null if not created?
        for(int i = 0; i < brickCoordinateArray.length; i++) {
            for(int j = 0; j < brickCoordinateArray[0].length; j++) {
                if((int)Math.round(Math.random()) == 1) {
                    brickCoordinateArray[i][j] = new Brick(rowAdvanceXSpace, rowAdvanceYSpace,
                            textureAtlas.createSprite("brick"), false);
                } else {
                    brickCoordinateArray[i][j] = new Brick(rowAdvanceXSpace, rowAdvanceYSpace, null, true);
                }
                rowAdvanceXSpace += 10;
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

//        extendViewport.update(width, height, true);

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
//        ballImage.dispose();
//        paddleImage.dispose();
//        blueBrickImage.dispose();

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

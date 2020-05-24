package com.mygdx.breakout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.breakout.npc.Ball;
import com.mygdx.breakout.npc.Brick;
import com.mygdx.breakout.user.Paddle;
import com.mygdx.breakout.utils.BreakoutContactListener;

import static com.badlogic.gdx.physics.box2d.World.setVelocityThreshold;

//TODO add velocity every time the ball hits something
public class GameScreen implements Screen {
    final Breakout game;

    int totalContacts =  0;
    int viewportWidth = 800;
    int viewportHeight = 600;
    int paddleMoveSpeed = 200;

    Texture ballImage;
    Texture paddleImage;
    //TODO add more brick images later to an array of Textures to randomly choose colors
    Texture blueBrickImage;

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
    PolygonShape leftWallShape;
    PolygonShape rightWallShape;
    PolygonShape ceilingShape;
    Body leftWallBody;
    Body rightWallBody;
    Body ceilingBody;

    Array<Body> removables;
    Array<Contact> contacts;
    BreakoutContactListener breakoutContactListener;

    public GameScreen(Breakout game) {
        this.game = game;

        //TODO move this Texture instantiation to individual data classes
        ballImage = new Texture(Gdx.files.internal("ball.png"));
        paddleImage = new Texture(Gdx.files.internal("paddle.png"));
        blueBrickImage = new Texture(Gdx.files.internal("blue_brick.png"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, viewportWidth, viewportHeight);


        setupBricks();

        //set up a world for physics
        Box2D.init();

        world = new World(new Vector2(0, 0), true);
        world.setContinuousPhysics(true);
        World.setVelocityThreshold(0f);
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

        //set up boundaries
        BodyDef rightWallBodyDef = new BodyDef();
        rightWallBodyDef.type = BodyDef.BodyType.StaticBody;
        rightWallBodyDef.position.set(viewportWidth + 100, viewportHeight/2);

        rightWallBody = world.createBody(rightWallBodyDef);

        rightWallShape = new PolygonShape();
        rightWallShape.setAsBox(viewportWidth/8, viewportHeight);

        FixtureDef rightWallFixtureDef = new FixtureDef();
        rightWallFixtureDef.shape = rightWallShape;
        rightWallFixtureDef.density = 0f;
        rightWallFixtureDef.friction = 0f;
        rightWallFixtureDef.restitution = 3.5f; // Make it bounce a little bit

        rightWallBody.createFixture(rightWallFixtureDef);

        //set up boundaries
        BodyDef leftWallBodyDef = new BodyDef();
        leftWallBodyDef.type = BodyDef.BodyType.StaticBody;
        leftWallBodyDef.position.set(-99, viewportHeight/2);

        leftWallBody = world.createBody(leftWallBodyDef);

        leftWallShape = new PolygonShape();
        leftWallShape.setAsBox(viewportWidth/8, viewportHeight);

        FixtureDef leftWallFixtureDef = new FixtureDef();
        leftWallFixtureDef.shape = leftWallShape;
        leftWallFixtureDef.density = 0f;
        leftWallFixtureDef.friction = 0f;
        leftWallFixtureDef.restitution = 3.5f; // Make it bounce a little bit

        leftWallBody.createFixture(leftWallFixtureDef);

        BodyDef ceilingBodyDef = new BodyDef();
        ceilingBodyDef.type = BodyDef.BodyType.StaticBody;
        ceilingBodyDef.position.set(viewportWidth/2, viewportHeight+70);

        ceilingBody = world.createBody(ceilingBodyDef);

        ceilingShape = new PolygonShape();
        ceilingShape.setAsBox(viewportWidth, viewportHeight/8);


        FixtureDef ceilingFixtureDef = new FixtureDef();
        ceilingFixtureDef.shape = ceilingShape;
        ceilingFixtureDef.density = 0f;
        ceilingFixtureDef.friction = 0f;
        ceilingFixtureDef.restitution = 3.5f; // Make it bounce a little bit

        ceilingBody.createFixture(ceilingFixtureDef);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {


//        world.set


        //do physics here?
        paddleBody.setLinearVelocity(0, 0);

        if(!ball.isLaunched()) {
            circleBody.setLinearVelocity(0, 0);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            paddleBody.setLinearVelocity(-paddleMoveSpeed, 0.0f);
            if(!ball.isLaunched()) {
                circleBody.setLinearVelocity(-paddleMoveSpeed, 0.0f);
            }
//            paddle.x -= paddleMoveSpeed;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            paddleBody.setLinearVelocity(paddleMoveSpeed, 0.0f);
            if(!ball.isLaunched()) {
                circleBody.setLinearVelocity(paddleMoveSpeed, 0.0f);
            }
//            paddle.x += paddleMoveSpeed;
        }

        //TODO review this instant addition of velocity
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && !ball.isLaunched()) {
            ball.setLaunched(true);
            circleBody.applyLinearImpulse(circleBody.getLinearVelocity().x * 5, 600.0f, 0, 0, true);
        }

        //TODO apply force velocity in magnitude ever so slightly every tick
        if(ball.isLaunched() && circleBody.getLinearVelocity().y == 0) {
            System.out.println("Applying linear impulse!");
            circleBody.applyLinearImpulse(circleBody.getLinearVelocity().x, 200f, 0, 0, true);
        }




        paddle.setXPos(paddleBody.getPosition().x - ((paddleImage.getWidth()/4/2)));
        paddle.setYPos(paddleBody.getPosition().y - ((paddleImage.getHeight()/4/2)));

        ball.setXPos(circleBody.getPosition().x - ((ballImage.getWidth() / 4) / 2)); //this alone sets the image and body together

        ball.setYPos(circleBody.getPosition().y - ((ballImage.getHeight()/4/2)));

//TODO assign some impulse force in the opposite x direction when ball impacted
        //TODO forget the above: let physics do its work and collect all destroyable bodies
        //TODO in a list and then delete after collision events are done


        world.step(1 / 120f, 6, 2);

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
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        //We divide the height and width of the image by 4 since it is rather large. hence, all calculations needs to go that way as well

        //TODO review this positional code as well
        for (int i = 0; i < brickCoordinateArray.length; i++) {
            for (int j = 0; j < brickCoordinateArray[0].length; j++) {
                if (!brickCoordinateArray[i][j].isDestroyed()) {
//                    System.out.println(brickCoordinateArray[i][j].getXPos() + " , " + brickCoordinateArray[i][j].getYPos());
                    game.batch.draw(
                            brickCoordinateArray[i][j].getBrickImage(),
                            brickCoordinateArray[i][j].getXPos(),
                            brickCoordinateArray[i][j].getYPos(),
                            brickCoordinateArray[i][j].getBrickImage().getWidth() / 4,
                            brickCoordinateArray[i][j].getBrickImage().getHeight() / 4);
                }

            }
        }



        game.batch.draw(paddleImage, paddle.getXPos(), paddle.getYPos(), paddleImage.getWidth() / 4, paddleImage.getHeight() / 4);


        //have the ball follow the paddle if it is not launched yet
        if(!ball.isLaunched()) {
//            circleBody.getPosition().set(paddleBody.getPosition().x + ((ballImage.getHeight()/4/2)), circleBody.getPosition().y);



            game.batch.draw(ballImage, ball.getXPos(), ball.getYPos(), ballImage.getWidth() / 4, ballImage.getHeight() / 4);
//
        } else {

//


            game.batch.draw(ballImage, ball.getXPos(), ball.getYPos(), ballImage.getWidth() / 4, ballImage.getHeight() / 4);
        }

        //TODO if a block is destroyed, we need to update the array to 0 so it doesn't redraw it


        game.batch.end();


            debugRenderer.render(world, camera.combined);

    }


    private void setupBrickPhysics() {

          for(int i = 0; i < brickCoordinateArray.length; i++) {
              for(int j = 0; j < brickCoordinateArray[0].length; j++) {

                  if(brickCoordinateArray[i][j].getBrickImage() != null) {
                      BodyDef brickBodyDef = new BodyDef();
                      brickBodyDef.type = BodyDef.BodyType.StaticBody;
                      brickBodyDef.position.set(
                              brickCoordinateArray[i][j].getXPos() + ((brickCoordinateArray[i][j].getBrickImage().getWidth()/4)/2),
                              brickCoordinateArray[i][j].getYPos() + ((brickCoordinateArray[i][j].getBrickImage().getHeight()/4)/2));


                      Body brickBody = world.createBody(brickBodyDef);
                      brickCoordinateArray[i][j].setBody(brickBody);
                      brickBody.setUserData(brickCoordinateArray[i][j]);

                      PolygonShape brickShape = new PolygonShape();
                      brickCoordinateArray[i][j].setShape(brickShape);
                      brickShape.setAsBox(
                              brickCoordinateArray[i][j].getBrickImage().getWidth()/8,
                              brickCoordinateArray[i][j].getBrickImage().getHeight()/8);

                      FixtureDef fixtureDef = new FixtureDef();
                      fixtureDef.shape = brickShape;
                      fixtureDef.density = 20f;
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
        circleBodyDef.position.set(paddleBody.getPosition().x, paddleBody.getPosition().y + 16);

        // Create our body in the world using our body definition
        circleBody = world.createBody(circleBodyDef);
//        circleBody.setUserData();

        circle = new CircleShape();
        circle.setRadius(20f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 5f;

        // Create our fixture and attach it to the body
        circleBody.createFixture(fixtureDef);

        ball = new Ball(circleBody.getPosition().x, circleBody.getPosition().y, ballImage, false);
        circleBody.setUserData(ball);

    }

    private void setUpPaddle() {

        BodyDef paddleBodyDef = new BodyDef();
        paddleBodyDef.type = BodyDef.BodyType.KinematicBody;
        //TODO review this positional code
        paddleBodyDef.position.set(new Vector2(viewportWidth/2 - ((paddleImage.getWidth()/4)/2) , 20));

        paddleBody = world.createBody(paddleBodyDef);

        paddleShape = new PolygonShape();
        //Useful for linking movement between rendered sprite and attached physics component
        paddleShape.setAsBox(paddleImage.getWidth()/8, paddleImage.getHeight()/8);

        paddleBody.createFixture(paddleShape, 0f);

        paddle = new Paddle(paddleBody.getPosition().x, paddleBody.getPosition().y, paddleImage);
        paddleBody.setUserData(paddle);
    }

    private void setupBricks() {

        //Seems like it should be slice = desiredBrickRowCount+1.5
        float slice = 7f;
        float rowEdgeWidth = viewportWidth/slice;
        float rowAdvanceXSpace = 0;
        float rowAdvanceYSpace = viewportHeight/3;



        //TODO make brick null if not created?
        for(int i = 0; i < brickCoordinateArray.length; i++) {
            for(int j = 0; j < brickCoordinateArray[0].length; j++) {
                if((int)Math.round(Math.random()) == 1) {
                    brickCoordinateArray[i][j] = new Brick(rowEdgeWidth + rowAdvanceXSpace, rowAdvanceYSpace,
                            new Texture(Gdx.files.internal("blue_brick.png")), false);
                } else {
                    brickCoordinateArray[i][j] = new Brick(0,0, null, true);
                }
                rowAdvanceXSpace += blueBrickImage.getWidth() / 4;
            }
            rowAdvanceXSpace = 0;
            rowAdvanceYSpace += blueBrickImage.getHeight()/4;
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

        ballImage.dispose();
        paddleImage.dispose();
        blueBrickImage.dispose();

        //dispose of all images in brick coordinate array
        for(int i = 0; i < brickCoordinateArray.length; i++) {
            for(int j = 0; j < brickCoordinateArray[0].length; j++) {
                brickCoordinateArray[i][j].getBrickImage().dispose();
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

    }
}

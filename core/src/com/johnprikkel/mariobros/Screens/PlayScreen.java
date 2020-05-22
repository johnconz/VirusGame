package com.johnprikkel.mariobros.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.johnprikkel.mariobros.Sprites.Enemies.Enemy;
import com.johnprikkel.mariobros.MarioBros;
import com.johnprikkel.mariobros.Scenes.Hud;
import com.johnprikkel.mariobros.Sprites.Mario;
import com.johnprikkel.mariobros.Tools.B2WorldCreator;
import com.johnprikkel.mariobros.Tools.WorldContactListener;

public class PlayScreen implements Screen {
    private com.johnprikkel.mariobros.MarioBros game;
    private TextureAtlas atlas;
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private com.johnprikkel.mariobros.Scenes.Hud hud;
    private com.johnprikkel.mariobros.Sprites.Mario player;
    private Music music;

    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private World world;
    private Box2DDebugRenderer b2dr;
    private com.johnprikkel.mariobros.Tools.B2WorldCreator creator;

    public PlayScreen(com.johnprikkel.mariobros.MarioBros game) {
        atlas = new TextureAtlas("Mario_and_Enemies2.pack");
        this.game = game;
        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(com.johnprikkel.mariobros.MarioBros.V_WIDTH / com.johnprikkel.mariobros.MarioBros.PPM, com.johnprikkel.mariobros.MarioBros.V_HEIGHT / com.johnprikkel.mariobros.MarioBros.PPM, gamecam);
        hud = new Hud(game.batch);


        maploader = new TmxMapLoader();
        map = maploader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / com.johnprikkel.mariobros.MarioBros.PPM);
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();
        creator = new B2WorldCreator(this);
        player = new com.johnprikkel.mariobros.Sprites.Mario(this);

        world.setContactListener(new WorldContactListener());

        music = com.johnprikkel.mariobros.MarioBros.manager.get("audio/music/doom.ogg", Music.class);
        music.setLooping(true);
        music.play();

    }

    public TextureAtlas getAtlas() {
        return atlas;
    }
    @Override
    public void show() {

    }

    public void handleInput(float dt) {
        if (player.currentState != com.johnprikkel.mariobros.Sprites.Mario.State.DEAD || player.currentState != com.johnprikkel.mariobros.Sprites.Mario.State.WON) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
                player.b2dbody.applyLinearImpulse(new Vector2(0, 4f), player.b2dbody.getWorldCenter(), true);
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) && player.b2dbody.getLinearVelocity().x <= 2)
                player.b2dbody.applyLinearImpulse(new Vector2(2f, 0), player.b2dbody.getWorldCenter(), true);
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) && player.b2dbody.getLinearVelocity().x >= -2)
                player.b2dbody.applyLinearImpulse(new Vector2(-2f, 0), player.b2dbody.getWorldCenter(), true);
        }
    }

    public void update(float dt){
        handleInput(dt);

        world.step(1/60f, 6, 2);
        player.update(dt);
        for(Enemy enemy : creator.getGoombas()) {
            enemy.update(dt);
            if(enemy.getX() < player.getX() + 224 / MarioBros.PPM)
                enemy.b2dbody.setActive(true);
        }

        hud.update(dt);

        if(player.currentState != com.johnprikkel.mariobros.Sprites.Mario.State.DEAD) {
            gamecam.position.x = player.b2dbody.getPosition().x;
        }

        //gamecam.position.x = player.b2dbody.getPosition().x;
        gamecam.update();
        renderer.setView(gamecam);
    }
    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for(Enemy enemy : creator.getGoombas())
            enemy.draw(game.batch);
        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if(gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }

        if(winner()){
            game.setScreen(new VictoryScreen(game));
            dispose();
        }
    }


    public boolean winner(){
        if(player.currentState == Mario.State.WON) {
            return true;
        }
        return false;
    }

    public boolean gameOver(){
        if(player.currentState == Mario.State.DEAD && player.getStateTimer() > 3){
            return true;
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);

    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
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
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}


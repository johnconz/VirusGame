package com.brentaureli.mariobros.Sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.brentaureli.mariobros.MarioBros;
import com.brentaureli.mariobros.Scenes.Hud;
import com.brentaureli.mariobros.Screens.PlayScreen;
import com.brentaureli.mariobros.Sprites.Items.ItemDef;
import com.brentaureli.mariobros.Sprites.Items.Mushroom;

public class Coin extends InteractiveTileObject{
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;
    public Coin(PlayScreen screen, Rectangle bounds){
        super(screen, bounds);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.COIN_BIT);
    }

    @Override
    public void onHeadHit() {
        Gdx.app.log("Coin", "Collision");
        if(getCell().getTile().getId() == BLANK_COIN)
            MarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
        else {
            MarioBros.manager.get("audio/sounds/coin.wav", Sound.class).play();
            screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBros.PPM), Mushroom.class)); //check if errors
        }
        getCell().setTile(tileSet.getTile(BLANK_COIN));
        Hud.addScore(500);
    }
}
//import com.badlogic.gdx.audio.Sound;
//import com.badlogic.gdx.maps.MapObject;
//import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
//import com.badlogic.gdx.math.Rectangle;
//import com.badlogic.gdx.math.Vector2;
//import com.brentaureli.mariobros.MarioBros;
//import com.brentaureli.mariobros.Scenes.Hud;
//import com.brentaureli.mariobros.Screens.PlayScreen;
//import com.brentaureli.mariobros.Sprites.Items.ItemDef;
//import com.brentaureli.mariobros.Sprites.Items.Mushroom;
//import com.brentaureli.mariobros.Sprites.Mario;
//
///**
// * Created by brentaureli on 8/28/15.
// */
//public class Coin extends InteractiveTileObject {
//    private static TiledMapTileSet tileSet;
//    private final int BLANK_COIN = 28;
//
//    public Coin(PlayScreen screen, MapObject object){
//        super(screen, object);
//        tileSet = map.getTileSets().getTileSet("tileset_gutter");
//        fixture.setUserData(this);
//        setCategoryFilter(MarioBros.COIN_BIT);
//    }
//
//    @Override
//    public void onHeadHit(Mario mario) {
//        if(getCell().getTile().getId() == BLANK_COIN)
//            MarioBros.manager.get("audio/sounds/bump.wav", Sound.class).play();
//        else {
//            if(object.getProperties().containsKey("mushroom")) {
//                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBros.PPM),
//                        Mushroom.class));
//                MarioBros.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
//            }
//            else
//                MarioBros.manager.get("audio/sounds/coin.wav", Sound.class).play();
//            getCell().setTile(tileSet.getTile(BLANK_COIN));
//            Hud.addScore(100);
//        }
//    }
//}

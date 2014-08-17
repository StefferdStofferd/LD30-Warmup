package nl.stefferd.ld30warmup;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class LdGame extends Game {
	
	float pixelPerMeter;
	OrthographicCamera spriteCamera, boxLightCamera;
	World world;
	RayHandler rayHandler;
	PointLight spriteLight;
	Color ambientLight, lightColor;
	SpriteBatch batch;
	Texture img;
	
	@Override
	public void create () {
		spriteCamera = new OrthographicCamera();
		spriteCamera.setToOrtho(true, 800, 600);
		spriteCamera.update(true);
		
		// setup light colors
		ambientLight = new Color(0.2f, 0.2f, 0.2f, 1);
		lightColor = new Color(1, 1, 1, 1);
		
		// light camera 
		pixelPerMeter = 64f;
		boxLightCamera = new OrthographicCamera();
		float boxLightViewportWidth = spriteCamera.viewportWidth / pixelPerMeter;
		float boxLightViewportHeight = spriteCamera.viewportHeight / pixelPerMeter;
		boxLightCamera.setToOrtho(true, boxLightViewportWidth, boxLightViewportHeight);
		boxLightCamera.update(true); 
		
		// world and light setup
		world = new World(new Vector2(), true); 

		RayHandler.useDiffuseLight(true);
		rayHandler = new RayHandler(world);
		rayHandler.setCombinedMatrix(boxLightCamera.combined);
		rayHandler.setAmbientLight(ambientLight);

		spriteLight = new PointLight(rayHandler, 128, lightColor, 5, 4, 4);
		
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		
		createWorldScenery();
	}
	
	protected void createWorldScenery() {
		// build plan for wall bodies
		float halfBody = 64 / pixelPerMeter / 2;

		PolygonShape tileShape = new PolygonShape();
		tileShape.setAsBox(0.5f, 0.5f);

		BodyDef tileBodyDef = new BodyDef();
		tileBodyDef.type = BodyType.StaticBody;

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = tileShape;
		fixtureDef.filter.groupIndex = 0;
		
		float bodyX = 3 + halfBody;
		float bodyY = 2 + halfBody;
		tileBodyDef.position.set(bodyX, bodyY);
		Body tileBody = world.createBody(tileBodyDef);
		tileBody.createFixture(fixtureDef);

		tileShape.dispose();
	}

	@Override
	public void render () {
		spriteCamera.update();
		batch.setProjectionMatrix(spriteCamera.combined);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
		
		boxLightCamera.update();
		
		rayHandler.setCombinedMatrix(boxLightCamera.combined, 
		boxLightCamera.position.x, boxLightCamera.position.y,
		boxLightCamera.viewportWidth * boxLightCamera.zoom, 
		boxLightCamera.viewportHeight * boxLightCamera.zoom);
		
		rayHandler.updateAndRender();
	}
}

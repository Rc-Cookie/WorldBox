import com.github.rccookie.engine2d.Camera;
import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.Debug;
import com.github.rccookie.engine2d.GameObject;
import com.github.rccookie.engine2d.Input;
import com.github.rccookie.engine2d.impl.ILoader;
import com.github.rccookie.engine2d.physics.SimplePlayerController;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Console;
import com.github.rccookie.util.ModSpliterator;

public class Loader implements ILoader {

    @Override
    public void initialize() {
        Console.getFilter(ModSpliterator.class).setEnabled("info", false);
    }

    @Override
    public void load() {
        Camera camera = new Camera(600, 400);
        camera.setBackgroundColor(Color.DARK_GRAY.darker());
        World world = new World(new int2(200, 100).dived(2), World.semiRandom());//p -> float2.maxDist(p.toF(), new float2(10, 10)) <= 3 ? TerrainState.OCEAN.create(0) : TerrainState.DRY.create(0));
        GameUI ui = new GameUI(camera);

        GameObject cameraObject = new GameObject();
        cameraObject.setMap(world);
        cameraObject.location.set(world.size.scaled(0.5f * TerrainTile.SIZE).add(-0.5001f, -0.5001f));
        camera.setGameObject(cameraObject);
        new SimplePlayerController(cameraObject);

        Input.addKeyPressListener(() -> new PaintSession(ui, PaintBrush.CIRCLE, 100, TerrainState.SNOW::apply, () -> Console.info("Painting end")), "p");

        Input.addKeyPressListener(TerrainTile::toggleDebug, "#");
        Debug.bindOverlayToggle();
    }
}

import java.util.Arrays;
import java.util.function.BiFunction;

import com.github.rccookie.engine2d.Map;
import com.github.rccookie.engine2d.util.Num;
import com.github.rccookie.event.Event;
import com.github.rccookie.event.SimpleEvent;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Console;

public class World extends Map {

    public static BiFunction<World, int2, TerrainTile> RANDOM =
            (w,p) -> TerrainState.get(Num.randI(TerrainState.COUNT)).create();

    public final int2 size;
    public final Noise tempNoise = new Noise(10);

    final TerrainTile[][] terrainTiles;

    public final Event[] regularEvents = new Event[100];
    {
        Arrays.setAll(regularEvents, i -> new SimpleEvent());
    }
    private int eventIndex = 0;

    private boolean terrainDevelopmentEnabled = false;
    private float terrainDevelopmentSpeed = 1f;

    public World(int2 size, BiFunction<World, int2, TerrainTile> generator) {
        this.size = size.clone();
        terrainTiles = new TerrainTile[size.x][size.y];
        for(int2 pos=new int2(0,size.y-1); pos.x<size.x; pos.x++,pos.y=size.y-1) for(; pos.y>=0; pos.y--) {
            TerrainTile tile = generator.apply(this, pos);
            tile.setMap(this);
            tile.location.set(pos.scaled((float) TerrainTile.SIZE));
            terrainTiles[pos.x][pos.y] = tile;
        }
        objects(TerrainTile.class).forEach(TerrainTile::init);
    }

    @Override
    public void update() {
        super.update();
        invokeRegularUpdate();
    }

    public void invokeRegularUpdate() {
        regularEvents[eventIndex++].invoke();
        eventIndex %= regularEvents.length;
    }

    public void developAll(float delta) {
        Console.info("Developing all");
        objects(TerrainTile.class).forEach(t -> t.develop(delta));
        objects(TerrainTile.class).forEach(t -> t.updateTerrainState(false));
        Console.map("Total water", objects(TerrainTile.class).mapToDouble(TerrainTile::getWater).sum());
    }


    public float getTerrainDevelopmentSpeed() {
        return terrainDevelopmentEnabled ? terrainDevelopmentSpeed : 0;
    }

    public void setTerrainDevelopmentEnabled(boolean terrainDevelopmentEnabled) {
        this.terrainDevelopmentEnabled = terrainDevelopmentEnabled;
    }

    public void setTerrainDevelopmentSpeed(float terrainDevelopSpeed) {
        this.terrainDevelopmentSpeed = terrainDevelopSpeed;
    }

    public static BiFunction<World, int2, TerrainTile> semiRandom() {
        return new BiFunction<>() {
            private Noise heightNoise = null;//new OpenSimplexNoise(System.currentTimeMillis());
            private final Noise waterNoise = new Noise(10);//new OpenSimplexNoise(System.currentTimeMillis() ^ System.nanoTime());

            @Override
            public TerrainTile apply(World world, int2 p) {
                if(heightNoise == null)
                    heightNoise = new Noise(Num.average(world.size.x, world.size.y) * 0.25f);
                float h = Num.abs(heightNoise.get(p));
                float w = waterNoise.get(p);
                if(w < -0.5f) w = (w + 1) * 0.2f;
                else if(w > 0.8) w = (w * 0.5f);
                else w = (w * 0.2f) + 0.3f;
//            float t = 60 * Num.exp(-Num.abs(3.6f * p.y / (float) world.size.y - 1.8f)) - 15 + world.tempNoise.eval(p.scaled(0.1f)) * 5;
                float sun = Num.cos(360f * p.y / (world.size.y) + 180);
                float t = (sun + 0.5f) * 30 + world.tempNoise.get(p) * 5;
                if(h < 0.42f) return TerrainState.OCEAN.create(null, null, t);
                return new TerrainTile(h > 0.8f ? 1 : 0, w, t);
            }
        };
    }
}

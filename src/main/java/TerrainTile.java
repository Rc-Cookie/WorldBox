import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.GameObject;
import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.Time;
import com.github.rccookie.engine2d.util.Num;
import com.github.rccookie.event.action.Action;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Console;

import org.jetbrains.annotations.Range;

import static com.github.rccookie.engine2d.util.Num.sigmoid1;

public class TerrainTile extends GameObject {

    public static final int SIZE = 10;

    public static boolean debug = false;

    private TerrainTile[] adjacent;

    @Range(from = -1, to = 1)
    private int height;
    @Range(from = 0, to = 2)
    private float water;
    private float temperature;

    private TerrainState terrainState;

    private final int rand = Num.randI(100);
    private final Action developAction = this::develop;

    private float sun, targetTemp, rain;

    private String lastDebugWaterStr = null, lastDebugTempStr = null;
    private int lastHeight = -2;


    @SuppressWarnings("SuspiciousNameCombination")
    public TerrainTile(int height, float water, float temperature) {
        this.height = Num.clamp(height, -1, 1);
        this.water = Num.clamp(water, 0, 2);
        this.temperature = temperature;
        updateTerrainState(true);
        onMapChange.add((o,n) -> {
            if(o != null)
                ((World)o).regularEvents[rand % ((World)o).regularEvents.length].remove(developAction);
            ((World)n).regularEvents[rand % ((World)n).regularEvents.length].add(developAction);
        });
    }

    public TerrainTile() {
        this(0, 0.3f, 20);
    }



    public void develop() {
        develop(0.1f * Time.delta() * getMap(World.class).regularEvents.length * getMap(World.class).getTerrainDevelopmentSpeed());
    }

    public void develop(float delta) {
        if(location.isZero()) Console.map("Update", delta);
        if(delta <= 0) return;
        delta = Num.min(delta, 1);

        temperature += delta * sigmoid1(0.25f * (targetTemp - temperature));

        water -= delta * sigmoid1(0.007f * (sun * 0.5f + 0.5f) * Num.min(0.2f, water));
        water += delta * rain * 0.00008f;

        if(terrainState == TerrainState.OCEAN)
            water += 0.05f * delta;

        water = Num.clamp(water, 0, 2);

        for(TerrainTile t : adjacent) {
            temperature += delta * 100 * sigmoid1(0.005f * (t.temperature - temperature));
            if(height <= t.height) {
                float diff = delta * sigmoid1(0.5f * water * water * t.water * t.water * (t.water - water));
                if(t.water - diff > 0.45f)
                    diff = Math.min(0, t.water - 0.45f);
                water += diff;
                t.water -= diff;
            }
        }

        updateTerrainState(false);
    }

    public void updateTerrainState(boolean force) {
        switchTerrainState(TerrainState.calc(height, water, temperature), force);
    }

    private void switchTerrainState(TerrainState newState, boolean force) {
//        Console.map("New state", newState);
        if(!force) {
            if(this.terrainState == newState && !debug) return;
            if(newState == TerrainState.ICE && terrainState == TerrainState.OCEAN)
                water = Num.max(0, water / 1.5f);
            else if(newState == TerrainState.OCEAN && terrainState == TerrainState.ICE)
                water = Num.min(2, water * 1.5f);
        }
        this.terrainState = newState;
        updateImage();
    }

    private void updateImage() {
        if(!debug) {
            setImage(terrainState.getImage(height));
//            lastDebugWaterStr = lastDebugTempStr = null;
        }
        else {
            setImage(terrainState.getImage(height).clone());
            Image waterText = Image.text(String.format("%.2f", water), SIZE / 2, Color.BLACK);
            Image tempText = Image.text((int) temperature + "", SIZE / 2, Color.BLACK);
            getImage().drawImage(waterText, int2.ZERO);
            getImage().drawImage(tempText, new int2(0, SIZE / 2));
        }
//        lastHeight = height;
    }



    public void init() {
        if(adjacent != null) return;

        int2 pos = location.dived(SIZE).toI();
        TerrainTile[][] tiles = getMap(World.class).terrainTiles;

        int count = 8;
        if(pos.x == 0) count -= 3;
        else if(pos.x == tiles.length - 1) count -= 3;
        if(pos.y == 0) count -= 3;
        else if(pos.y == tiles[0].length - 1) count -= 3;
        if(count == 2) count = 3;

        adjacent = new TerrainTile[count];
        for(int index=0,x=-1; x<=1; x++) for(int y=-1; y<=1; y++) {
            if(x == 0 && y == 0) continue;
            int i = x + pos.x, j = y + pos.y;
            if(i < 0 || j < 0 || i >= tiles.length || j >= tiles[0].length) continue;
            adjacent[index++] = tiles[i][j];
        }
        shuffleArray(adjacent);

        sun = Num.cos(360 * location.y / (SIZE * getMap(World.class).size.y) + 180);
        temperature = targetTemp = (sun + 0.5f) * 30 + getMap(World.class).tempNoise.get(location.scaled(SIZE)) * 5;
        rain = Num.max(0, Num.sin(540 * location.y / (SIZE * getMap(World.class).size.y)) * 0.5f + 0.4f);

        updateImage();
    }



    public int getHeight() {
        return height;
    }

    public float getWater() {
        return water;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWater(float water) {
        this.water = water;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }



    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        if(TerrainTile.debug != debug)
            toggleDebug();
    }

    public static void toggleDebug() {
        debug = !debug;
        Console.map("Debug", debug);
//        Camera.getActive().getMap().objects(TerrainTile.class).forEach(TerrainTile::updateImage);
    }



    private static <T> void shuffleArray(T[] arr) {
        for (int i=arr.length-1; i>0; i--) {
            int index = Num.randI(i+1);//rnd.nextInt(i + 1);
            // Simple swap
            T a = arr[index];
            arr[index] = arr[i];
            arr[i] = a;
        }
    }
}

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.Image;
import com.github.rccookie.geometry.performance.int2;

public enum TerrainState {
    OCEAN (-1, 1.9f,  10),
    ICE   (-1, 0.7f,  -15),
    RIVER (0,  0.8f,  15),
    WET   (0,  0.45f, 30),
    HUMID (0,  0.27f,  20),
    SNOW  (0,  0.25f,  -5),
    DRY   (0,  0.1f,  30),
    DESERT(0,  0.01f, 35),
    ROCK  (1,  0,     10),
    LAVA  (0,  0,     500);

    private final Image low, leveled, high;

    private final int height;
    private final float water;
    private final float temperature;

    TerrainState(int height, float water, float temperature) {
        this.height = height;
        this.water = water;
        this.temperature = temperature;

        Image image = Image.load("images/terrain/" + name().toLowerCase() + ".png").scaled(new int2(TerrainTile.SIZE, TerrainTile.SIZE));

        Color LOW_COLOR = Color.BLACK.setAlpha(0.08f);
        Color HIGH_COLOR = Color.WHITE.setAlpha(0.08f);

        leveled = image.clone();
        low = image.clone();
        high = image.clone();
        low.fill(LOW_COLOR);
        high.fill(HIGH_COLOR);
    }

    public Image getImage(int height) {
        return height == 0 ? leveled : height < 0 ? low : high;
    }

    public TerrainTile create() {
        return new TerrainTile(height, water, temperature);
    }

    public TerrainTile create(int height) {
        return new TerrainTile(height, water, temperature);
    }

    public TerrainTile create(Integer height, Float water, Float temperature) {
        return new TerrainTile(
                height != null ? height : this.height,
                water != null ? water : this.water,
                temperature != null ? temperature : this.temperature
        );
    }

    public void apply(TerrainTile tile) {
        tile.setHeight(height);
        tile.setWater(water);
        tile.setTemperature(temperature);
        tile.updateTerrainState(false);
    }

    private static final float MAX_SNOW_TEMP = 0;

    public static TerrainState calc(int height, float water, float temperature) {
        if(water > 0.5f)
            return temperature < -10 ? ICE : height < 0 ? OCEAN : RIVER;
        if(water > 0.4f)
            return temperature < MAX_SNOW_TEMP ? SNOW : temperature < 30 ? HUMID : WET;
        if(water > 0.2f)
            return temperature < MAX_SNOW_TEMP ? SNOW : HUMID;
        if(temperature > 300) return LAVA;
        if(temperature < 10 || height > 0) return ROCK;
        return water > 0.05f ? DRY : DESERT;
    }

    private static final TerrainState[] VALUES = values();
    public static final int COUNT = VALUES.length;

    public static TerrainState get(int index) {
        return VALUES[index];
    }
}

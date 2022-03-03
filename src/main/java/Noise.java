import com.github.rccookie.geometry.performance.float2;
import com.github.rccookie.geometry.performance.int2;

public class Noise {

    private final float resolution;
    private final OpenSimplexNoise noise;

    public Noise(float resolution, long seed) {
        this.resolution = 1 / resolution;
        this.noise = new OpenSimplexNoise(seed);
    }

    public Noise(float resolution) {
        this(resolution, System.currentTimeMillis() ^ System.nanoTime());
    }

    public float get(float2 p) {
        return (float) noise.eval(p.x * resolution, p.y * resolution);
    }

    public float get(int2 p) {
        return (float) noise.eval(p.x * resolution, p.y * resolution);
    }
}

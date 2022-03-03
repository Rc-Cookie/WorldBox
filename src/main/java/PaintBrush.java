import java.util.stream.Stream;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.Image;
import com.github.rccookie.geometry.performance.float2;

public interface PaintBrush {

    PaintBrush CIRCLE = new PaintBrush() {
        @Override
        public Stream<TerrainTile> getPainted(float size, float2 pos, Stream<TerrainTile> tiles) {
            float maxSqrDist = size * size * 0.25f;
            return tiles.filter(t -> float2.sqrDist(pos, t.location) <= maxSqrDist);
        }

        @Override
        public Image getPreview(float size, Color color) {
            Image image = new Image((int)size, (int)size);
            image.fillOval(image.center, image.size, color);
            return image;
        }
    };

    PaintBrush SQUARE = new PaintBrush() {
        @Override
        public Stream<TerrainTile> getPainted(float size, float2 pos, Stream<TerrainTile> tiles) {
            return tiles.filter(t -> float2.maxDist(pos, t.location) <= size * 0.5f);
        }

        @Override
        public Image getPreview(float size, Color color) {
            return new Image((int) size, (int) size, color);
        }
    };

    Stream<TerrainTile> getPainted(float size, float2 pos, Stream<TerrainTile> tiles);

    Image getPreview(float size, Color color);
}

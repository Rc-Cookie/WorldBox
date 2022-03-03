import java.util.function.Consumer;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.Input;
import com.github.rccookie.engine2d.Mouse;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.ui.ImagePanel;
import com.github.rccookie.engine2d.ui.util.Alignment;
import com.github.rccookie.util.Console;

public class PaintSession extends ImagePanel {

    private final PaintBrush paintBrush;
    private final float size;
    private final Consumer<TerrainTile> paint;
    private final Runnable onRemove;

    public PaintSession(UIObject parent, PaintBrush paintBrush, float size, Consumer<TerrainTile> paint, Runnable onRemove) {
        super(parent, paintBrush.getPreview(size, Color.WHITE.setAlpha(0.1f)));

        this.paintBrush = paintBrush;
        this.size = size;
        this.paint = paint;
        this.onRemove = onRemove;

        relativeLoc.set(-1, -1);
        offset.set(Input.getMouse().pixel);
        setAlignment(Alignment.CENTER);

        input.addKeyPressListener(this::remove, "esc");
        input.mousePressed.add(m -> { if(m.button == 3) remove(); });
        update.add(this::update);
    }

    private void update() {
        Mouse mouse = Input.getMouse();
        offset.set(mouse.pixel);
        if(getUI().getObjectsAt(mouse.pixel, false).size() > 1) {
            setVisible(false);
            Console.map("Blocked by", getUI().getObjectsAt(mouse.pixel, false));
        }
        else {
            setVisible(true);
            if(mouse.pressed && mouse.button == 1)
                paintBrush.getPainted(size, getCamera().pixelToPoint(mouse.pixel), getMap(World.class).objects(TerrainTile.class)).forEach(paint);
        }
    }

    @Override
    public boolean remove() {
        onRemove.run();
        return super.remove();
    }
}

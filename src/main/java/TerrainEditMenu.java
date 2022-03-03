import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.ui.ColorPanel;
import com.github.rccookie.geometry.performance.int2;

public class TerrainEditMenu extends ColorPanel {

    public static final int BORDER = 5;

    public TerrainEditMenu(UIObject parent) {
        super(parent, int2.ONE, Color.DARK_GRAY);
        onParentSizeChange.add(s -> setSize(new int2(s.x / 4, s.y - ControlBar.HEIGHT - 2 * BORDER)));
        onEnable.add(s -> { if(s) onParentSizeChange.invoke(getParent().getSize()); });
    }
}

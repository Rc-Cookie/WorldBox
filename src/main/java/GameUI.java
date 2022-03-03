import com.github.rccookie.engine2d.Camera;
import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.UI;
import com.github.rccookie.engine2d.ui.IconToggle;
import com.github.rccookie.engine2d.ui.Toggle;
import com.github.rccookie.geometry.performance.int2;

public class GameUI extends UI {

    public static final Image OPTIONS_ICON = Image.load("images/icons/options.png").scaled(new int2(30, 30));

    public GameUI(Camera camera) {
        super(camera);

        ControlBar controlBar = new ControlBar(this);
        controlBar.relativeLoc.y = 1;

        TerrainEditMenu terrainEditMenu = new TerrainEditMenu(this);
        terrainEditMenu.setEnabled(false);
        terrainEditMenu.relativeLoc.set(1, -1);
        terrainEditMenu.offset.set(-TerrainEditMenu.BORDER, TerrainEditMenu.BORDER);

        Image optionsIconOff = new Image(OPTIONS_ICON.size.added(10, 10), getCamera().getBackgroundColor());
        optionsIconOff.drawImage(OPTIONS_ICON, new int2(5, 5));
        Image optionsIconOn = new Image(optionsIconOff.size);
        optionsIconOn.drawImage(OPTIONS_ICON, new int2(5, 5));
        Toggle optionsButton = new IconToggle(this, optionsIconOn, optionsIconOff);
        optionsButton.relativeLoc.set(1, -1);
        optionsButton.offset.set(new int2(-TerrainEditMenu.BORDER - 5, TerrainEditMenu.BORDER + 5));
        optionsButton.onToggle.add(terrainEditMenu::setEnabled);
    }
}

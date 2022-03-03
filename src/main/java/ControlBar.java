import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.ui.Button;
import com.github.rccookie.engine2d.ui.ColorPanel;
import com.github.rccookie.engine2d.ui.Dimension;
import com.github.rccookie.engine2d.ui.IconToggle;
import com.github.rccookie.engine2d.ui.Toggle;
import com.github.rccookie.geometry.performance.int2;

public class ControlBar extends Dimension {

    public static final int HEIGHT = 50;

    public ControlBar(UIObject parent) {
        super(parent, Integer.MAX_VALUE, HEIGHT);

        ColorPanel background = new ColorPanel(this, getSize(), Color.DARK_GRAY);
        background.onParentSizeChange.add(background::setSize);

        int2 playButtonSize = new int2(30, 30);
        Toggle playButton = new IconToggle(this, Image.load("images/icons/pause.png").scaled(playButtonSize), Image.load("images/icons/play.png").scaled(playButtonSize));
        playButton.relativeLoc.x = -1;
        playButton.offset.x = 15;
        playButton.onToggle.add(s -> getMap(World.class).setTerrainDevelopmentEnabled(s));
        playButton.input.addKeyPressListener(playButton.onClick::invoke, " ");

        Button fastForwardButton = new FastForwardButton(this);
        fastForwardButton.relativeLoc.x = -1;
        fastForwardButton.offset.x = playButton.offset.x + playButtonSize.x + 15;
    }
}

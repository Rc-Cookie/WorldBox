import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.ui.IconButton;

public class FastForwardButton extends IconButton {

    private static final int HEIGHT = 20;
    private static final float[] SPEEDS = { 1, 3, 10 };
    private static final Image[] SPEED_IMAGES;
    static {
        SPEED_IMAGES = new Image[SPEEDS.length];
        for(int i = 0; i< SPEED_IMAGES.length; i++)
            //noinspection SpellCheckingInspection
            SPEED_IMAGES[i] = Image.load("images/icons/fastforward" + (i+1) + ".png").scaledToHeight(HEIGHT);
    }

    private int speed = 0;

    public FastForwardButton(UIObject parent) {
        super(parent, SPEED_IMAGES[0]);
        onClick.add(() -> {
            speed = (speed + 1) % SPEEDS.length;
            getMap(World.class).setTerrainDevelopmentSpeed(SPEEDS[speed]);
            setIcon(SPEED_IMAGES[speed]);
        });
        for(int i=0; i<SPEEDS.length; i++) {
            int s = i;
            input.addKeyPressListener(() -> {
                speed = s;
                getMap(World.class).setTerrainDevelopmentSpeed(SPEEDS[s]);
                setIcon(SPEED_IMAGES[s]);
            }, s+1+"");
        }
    }
}

import com.github.rccookie.engine2d.impl.awt.AWTApplicationLoader;
import com.github.rccookie.engine2d.impl.awt.AWTStartupPrefs;

public class AWTLoader extends AWTApplicationLoader {

    public AWTLoader() {
        super(new Loader(), new AWTStartupPrefs());
    }

    public static void main(String[] args) {
        new AWTLoader();
    }
}

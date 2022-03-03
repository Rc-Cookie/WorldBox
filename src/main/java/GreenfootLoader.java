import com.github.rccookie.engine2d.impl.greenfoot.GreenfootApplicationLoader;
import com.github.rccookie.engine2d.impl.greenfoot.GreenfootStartupPrefs;

public class GreenfootLoader extends GreenfootApplicationLoader {

    public GreenfootLoader() {
        super(new Loader(), new GreenfootStartupPrefs());
    }
}

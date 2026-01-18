import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Chris Chun, Ayush
 */
public class Menu {

    /**
     *
     */
    private final String title;

    /**
     * A map of the menu. key = menu type, value = menu item
     */
    private final Map<String, MenuItem> items = new LinkedHashMap<>();

    public Menu(String title) {
        this.title = title;
    }

    public Menu add(String key, String label, Runnable action) {
        items.put(key, new ActionItem(label, action));
        return this;
    }

    public Menu addSubMenu(String key, String label, Menu subMenu) {
        items.put(key, new SubMenuItem(label, subMenu));
        return this;
    }

    public void display() {
        System.out.println("\n== " + title + " ==");
        items.forEach((k, v) ->
                System.out.println(k + ") " + v.label())
        );
    }

    public MenuItem get(String key) {
        return items.get(key);
    }

    interface MenuItem {
        String label();
    }

    public record ActionItem(String label, Runnable action) implements MenuItem {}

    public record SubMenuItem(String label, Menu menu) implements MenuItem {}

}

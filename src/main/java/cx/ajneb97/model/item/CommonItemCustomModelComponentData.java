package cx.ajneb97.model.item;

import java.util.ArrayList;
import java.util.List;

public class CommonItemCustomModelComponentData {
    private List<String> flags;
    private List<String> colors;
    private List<String> floats;
    private List<String> strings;

    public CommonItemCustomModelComponentData(List<String> flags, List<String> colors, List<String> floats, List<String> strings) {
        this.flags = flags;
        this.colors = colors;
        this.floats = floats;
        this.strings = strings;
    }

    public List<String> getFlags() {
        return flags;
    }

    public void setFlags(List<String> flags) {
        this.flags = flags;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public List<String> getFloats() {
        return floats;
    }

    public void setFloats(List<String> floats) {
        this.floats = floats;
    }

    public List<String> getStrings() {
        return strings;
    }

    public void setStrings(List<String> strings) {
        this.strings = strings;
    }

    public CommonItemCustomModelComponentData clone(){
        return new CommonItemCustomModelComponentData(new ArrayList<>(flags),new ArrayList<>(colors),new ArrayList<>(floats),new ArrayList<>(strings));
    }
}

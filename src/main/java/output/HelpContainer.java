package output;

/**
 * Created by Morthanion on 15.11.2014.
 */
public class HelpContainer {
    private String name;
    private String description;

    public HelpContainer(String name, String description)
    {
        this.name = name;
        this.description = description;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

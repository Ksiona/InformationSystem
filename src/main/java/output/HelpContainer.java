package output;

/**
 * Created by Morthanion on 15.11.2014.
 */

/**
 * Контайнер для справки о команде
 */
public class HelpContainer {
    private String name;
    private String description;

    /**
     * Создает новый экземпляр объекта
     * @param name имя команды
     * @param description описание команды
     */
    public HelpContainer(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    /**
     * Возвращает имя команды
     * @return имя команды
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает описание команды
     * @return описание команды
     */
    public String getDescription() {
        return description;
    }

}

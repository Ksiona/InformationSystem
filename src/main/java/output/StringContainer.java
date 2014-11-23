package output;
/**
 * @author Ksiona
 * Контейнер для сообщения, после которого не должно быть перехода на новую строку
 */
public class StringContainer {
	 private String line;
	 
	 public StringContainer(String line) {
		 this.line = line;
	}

	public String getLine() {
		return line;
	}
}

package interfaces;

/**
 * Интерфейс записи в одном из списков бибилиотеки.
 */
public interface Record {
    /**
     * Возвращает название жанра у трека
     * @return строку с названием жанра
     */
	public String getGenre();
    /**
     * Устанавливает название жанра у данного трека.
     * Если передать как параметр "null" или "-",
     * устанавливает жанр "Unsorted"
     * @param genre новое название жанра
     */
	public void setGenre(String genre);
    /**
     * Возвращает название трека
     * @return название трека
     */
	public String getTrackTitle();
    /**
     *  Устанавливает новое название трека.
     * @param trackTitle новое название.
     * @exception java.lang.IllegalArgumentException в случае передачи null как названия.
     */
	public void setTrackTitle(String trackTitle);
    /**
     * Возвращает название исполнителя.щ
     * @return назваие исполнителя.
     */
	public String getSinger();
    /**
     * Устанавливает новое название исполнителя.
     * Если параметр равен "null" или "-",
     * устанавливает название "unknown".
     * @param singer новое название исполнителя.
     */
	public void setSinger(String singer);
    /**
     * Возвращает название альбома.
     * @return название альбома.
     */
	public String getAlbum();
    /**
     * Устанавливает новое название альбома
     * Если параметр равен "null" или "-",
     * устанавливает название "unknown".
     * @param album новое название альбома.
     */
	public void setAlbum(String album);
    /**
     * Возвращает длинн трека
     * @return длинна трека
     */
	public String getRecordLength();
    /**
     * Устанавливает новую длинну трека.
     * Если параметр равен "null" или "-",
     * устанавливает длинну "unknown".
     * @param recordLength
     */
	public void setRecordLength(String recordLength);

    /**
     * Проверяет определннное поле объкта на соответствие маске для сравнения.
     * @param keyField Название ключевого поля (Albium/Title/Genre/Singer/Length).
     * @param mask Маска для сравнения - последовательность символов, которая должна встречаться в лючевом поле где
     *             <p>    "*" - ни одного или более любых символов.<br>
     *                    "?" - один любой символ.</p>
     * @return true, если маска встречается в поле, flase - иначе.
     */
    public boolean fitsMask(String keyField, String mask);
}

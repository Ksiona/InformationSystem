package interfaces;

/**
 * Created by Morthanion on 04.11.2014.
 */

/**
 * Реализует функционал объекта-слушателя
 */
public interface Listener {
    /**
     * Позволяет оповещать слушателя о событии и передавать ему информацию
     * @param arg информация для передачи
     */
    public void doEvent(Object arg);
}

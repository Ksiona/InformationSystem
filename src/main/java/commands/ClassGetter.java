package commands;

import interfaces.Command;

import java.lang.reflect.ParameterizedType;

abstract class ClassGetter<T> {
    public final Class<T> get() {
        final ParameterizedType superclass = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>)superclass.getActualTypeArguments()[0];
    }
}

class Commands<T extends Command> {
    T getInstance() throws InstantiationException, IllegalAccessException {
    	T instance = instantiate();
		return instance;
    }

    public static <T> Class<T> getGenericClass() {
    	return new ClassGetter<T>() {}.get();
    }

	public static final <T> T instantiate() {
	    final Class<T> clazz = getGenericClass();
	    try {
	        return clazz.getConstructor((Class[])null).newInstance(null);
	    } catch (Exception e) {
	        return null;
	    }
	}
	
}

class kkk{
	public static void main(String[] args) {
		System.out.println(new Commands<TrackCommand>().instantiate().getClass());
		;
	}
}
package output;

import interfaces.Record;

import java.util.Collection;

/**
 * Created by Morthanion on 14.11.2014.
 */
public class ListContainer {
    private Collection<Record> collection;
    public Collection<Record> getCollection() {
        return collection;
    }
    public void setCollection(Collection<Record> collection) {
        this.collection = collection;
    }
    public ListContainer(Collection<Record> collection){
        this.collection = collection;
    }
}

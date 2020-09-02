package mx.com.pendulum.olintareas.dto;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionErrors<T> extends ArrayList<TransactionError<T>> {

    private List<T> elements = new ArrayList<T>();
    private List<Throwable> errors=new ArrayList<Throwable>();

    public TransactionErrors() { super(); }

    @Override
    public boolean add(TransactionError<T> e) {
        elements.add(e.getElement());
        errors.add(e.getError());
        return super.add(e);
    }

    /**
     * @return the elements
     */
    public List<T> getElements() {
        return Collections.unmodifiableList(elements);
    }

    /**
     * @return the errors
     */
    public List<Throwable> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}

package mx.com.pendulum.olintareas.dto;

public class TransactionError <T> {

    private T element;
    private Throwable error;

    public TransactionError(T element, Throwable error) {
        this.element = element;
        this.error = error;
    }

    /**
     * @return the element
     */
    public T getElement() {
        return element;
    }

    /**
     * @param element the element to set
     */
    public void setElement(T element) {
        this.element = element;
    }

    /**
     * @return the error
     */
    public Throwable getError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(Throwable error) {
        this.error = error;
    }
}
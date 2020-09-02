package mx.com.pendulum.olintareas.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionResult<T, K> {

    private List<K> successful;
    private List<K> failed;
    private String response;

    private TransactionErrors<T> errors = new TransactionErrors<T>();

    public TransactionResult() {
        successful = new ArrayList<K>();
        failed = new ArrayList<K>();
    }

    public TransactionResult(List<K> successful, List<K> failed) {
        this.successful = successful;
        this.failed = failed;

    }

    public List<K> getSuccessful() {
        return Collections.unmodifiableList(successful);
    }

    public long getSuccessfulCount() {
        return successful.size();
    }

    public void addSuccessfulId(K id) {
        successful.add(id);
    }

    public long getFailedCount() {
        return failed.size();
    }

    public void addFailedId(K id) {
        failed.add(id);
    }

    public List<K> getFailed() {
        return Collections.unmodifiableList(failed);
    }

    /**
     * @return the errors
     */
    @JsonIgnore
    public TransactionErrors<T> transactionErrors() {
        return errors;
    }

    /**
     *
     */
    public void addError(TransactionError<T> error) {
        this.errors.add(error);
    }

    public void addError(T element, Throwable error) {
        this.errors.add(new TransactionError<T>(element, error));
    }

    public void addError(K id, T element, Throwable error) {
        this.failed.add(id);
        this.errors.add(new TransactionError<T>(element, error));
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }


    public TransactionErrors<T> getErrors() {
        return errors;
    }

    public void setErrors(TransactionErrors<T> errors) {
        this.errors = errors;
    }
}

package mx.com.pendulum.olintareas.interfaces;

public class Interfaces {
    public interface OnResponseDownload<T> {
        void onResponse(int handlerCode, T t, String error);
    }


    public interface OnResponse<T> {
        void onResponse(int handlerCode, T t);
    }
}

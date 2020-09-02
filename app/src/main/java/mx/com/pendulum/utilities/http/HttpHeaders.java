package mx.com.pendulum.utilities.http;

import java.util.Enumeration;
import java.util.Hashtable;

public class HttpHeaders {
    public final static String HEADER_CONTENT_TYPE = "Content-Type";
    public final static String CONTENT_TYPE_APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    Hashtable<String, String> m_Hashtable;

    public HttpHeaders(){
        m_Hashtable = new Hashtable<String, String>();
    }

    public String getPropertyKey(int index) {
        String _key = null;
        Enumeration<String> e = m_Hashtable.keys();
        for (int i=0; i <= index; i++) {
            _key = (e.nextElement());
        }
        return _key;
    }

    public String getPropertyValue(int index) {
        String returnContenido = null;
        Enumeration<String> e = m_Hashtable.keys();
        for (int i=0; i <= index; i++) {
            String _key = (e.nextElement());
            returnContenido =  m_Hashtable.get(_key);
        }
        return returnContenido;
    }

    public String getPropertyValue(final String key){
        return m_Hashtable.get(key);
    }

    public void addProperty(String key, String value) {
        m_Hashtable.put(key, value);
    }

    public void removeProperty(int index) {
        String _key = null;
        Enumeration<String> e = m_Hashtable.keys();
        for (int i=0; i <= index; i++) {
            _key = (e.nextElement());
        }
        m_Hashtable.remove(_key);
    }

    public int size() {
        return m_Hashtable.size();
    }
}

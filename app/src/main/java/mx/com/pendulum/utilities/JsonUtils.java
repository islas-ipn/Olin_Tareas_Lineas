package mx.com.pendulum.utilities;

import android.util.Log;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mx.com.pendulum.olintareas.Properties;

public class JsonUtils {
    private static final String TAG = JsonUtils.class.getSimpleName();

    public static JSONObject toJsonObject(Object object) {
        JSONObject json = null;
        try {
            if (object != null)
                json = new JSONObject(new ObjectMapper().writeValueAsString(object));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return json;
    }

    public static JSONArray toJsonArray(List<?> objects) {
        JSONArray json = null;
        try {
            if (objects.size() > 0)
                json = new JSONArray(new ObjectMapper().writeValueAsString(objects));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return json;
    }

    public static class JsonDateSerializer extends JsonSerializer<Date> {
        public JsonDateSerializer() {
            super();
        }

        @Override
        public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
            SimpleDateFormat format = new SimpleDateFormat(Properties.DATE_TIME_FORMAT_DB);//.replace(' ', 'T')
            jsonGenerator.writeString(format.format(date));
        }
    }

    public static class JsonDateTzSerializer extends JsonSerializer<Date> {
        public JsonDateTzSerializer() {
            super();
        }

        @Override
        public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
            SimpleDateFormat format = new SimpleDateFormat(Properties.DATE_TIME_FORMAT_DB + "Z");//.replace(' ', 'T')
            jsonGenerator.writeString(format.format(date));
        }
    }


    public static String path_apk() {
        File f = new File("/sdcard" + "/apk/");
        if (f.mkdir()) {
            Log.i("LOG_", "creo el directorio");
        } else {
            Log.i("LOG_", "ya exite el directorio");
        }

        return f.getAbsolutePath() + "/olin_legal.apk";
    }

    public static String path_file() {
        File f = new File("/sdcard" + "/file_/");
        if (f.mkdir()) {
            Log.i("LOG_", "creado  file");
        } else {
            Log.i("LOG_", "Directotior file existe");
        }

        return f.getAbsolutePath() + "/";
    }
}

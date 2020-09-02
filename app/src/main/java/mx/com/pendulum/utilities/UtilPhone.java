package mx.com.pendulum.utilities;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.util.Patterns;

import com.google.android.gms.common.AccountPicker;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.regex.Pattern;

import mx.com.pendulum.olintareas.config.util.ContextApplication;

public class UtilPhone {
    private static final String ACCOUNT = "pendulum.com.mx";

    public static String getAcountGmail() {
        String cuenta = "";

        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
                if (ActivityCompat.checkSelfPermission(ContextApplication.getAppContext(), Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {

                }
                Account[] accounts = AccountManager.get(ContextApplication.getAppContext()).getAccounts();
                for (Account account : accounts) {
                    if (emailPattern.matcher(account.name).matches()) {
                        String possibleEmail = account.name;
                        if (possibleEmail.contains(ACCOUNT)) {
                            cuenta = possibleEmail;
                            Log.i("cuentas", "" + possibleEmail);
                            break;
                        }
                    }
                }


            }else  if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                AccountManager a  = AccountManager.get(ContextApplication.getAppContext());
                Intent i  =AccountPicker.newChooseAccountIntent(null, null,new String[]{"com.google"},false,null,null,null,null);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ContextApplication.getAppContext().startActivity(i);

                String v = "Hola demos";
            }
            else {
                File f = new File("/data/data/com.google.android.gm/shared_prefs/Gmail.xml");
                if (f.exists()){
                    String gmailXML = Tools.sudoForResult("cat /data/data/com.google.android.gm/shared_prefs/Gmail.xml");
                    HashMap<String, String> map = parseXML(gmailXML);

                    String mails = map.get("cache-google-accounts-synced");


                    if (mails == null) {
                        return "";
                    }

                    if (mails.isEmpty()) {
                        return "";
                    }

                    String[] arrayMail = mails.split(" ");

                    for (String possibleEmail : arrayMail) {
                        if (possibleEmail.contains(ACCOUNT)) {
                            cuenta = possibleEmail;
                            Log.i("cuentas", "" + possibleEmail);
                            break;
                        }
                    }
                }

            }
            cuenta = cuenta.replace("@pendulum.com.mx", "");
        } catch (Exception e) {
            e.printStackTrace();
            cuenta = null;
        }
        return cuenta;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static HashMap<String, String> parseXML(String xml) {
        HashMap<String, String> map = new HashMap<>();

        try {
            InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));


            XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(stream, null);

            int eventType = parser.getEventType();


            String attr = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {


                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:

                        String type = parser.getName();
                        attr = parser.getAttributeValue("", "name");

                        switch (type) {
                            case "string":
                                break;
                            case "boolean":
                            case "int":
                                String value = parser.getAttributeValue("", "value");
                                map.put(attr, value);
                                attr = null;
                                break;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (attr == null) break;
                        if (attr.equals("")) break;
                        String value = parser.getText();
                        map.put(attr, value);
                        break;
                    case XmlPullParser.END_TAG:
                        attr = null;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {

        }
        return map;
    }
}

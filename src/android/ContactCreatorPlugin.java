package nl.afas.cordova.plugin.contactcreator;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Intents;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ContactCreatorPlugin extends CordovaPlugin {

  private Context context;
  private CallbackContext callbackContext;
  private static final int CHOOSE_CONTACT = 1;
  private static final int INSERT_CONTACT = 2;

  @Override
  public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
    this.callbackContext = callbackContext;
    this.context = cordova.getActivity().getApplicationContext();

    if (action.equals("addContact")) {
      int phone_count = 0;
      int email_count = 0;
      Intent intent = new Intent(Intent.ACTION_INSERT);
      intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
      try {
        JSONObject contact = data.getJSONObject(0);
        if (contact != null) {

          intent.putExtra(ContactsContract.Intents.Insert.NAME, contact.optString("displayName"));


          if (!contact.getString("phoneHome").equals("")) {
            intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, contact.getString("phoneHome"));
            phone_count++;
          }

          if (!contact.getString("phoneWork").equals("")) {
            if (phone_count == 0) {
              intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
              intent.putExtra(ContactsContract.Intents.Insert.PHONE, contact.getString("phoneWork"));
              phone_count++;
            } else if (phone_count == 1) {
              intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
              intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, contact.getString("phoneWork"));
              phone_count++;
            }
          }

          if (!contact.getString("mobileHome").equals("")) {
            if (phone_count == 0) {
              intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
              intent.putExtra(ContactsContract.Intents.Insert.PHONE, contact.getString("mobileHome"));
            } else if (phone_count == 1) {
              intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
              intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, contact.getString("mobileHome"));
            } else if (phone_count == 2) {
              intent.putExtra(ContactsContract.Intents.Insert.TERTIARY_PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
              intent.putExtra(ContactsContract.Intents.Insert.TERTIARY_PHONE, contact.getString("mobileHome"));
            }
          }

          if (!contact.getString("emailHome").equals("")) {
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_HOME);
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL, contact.getString("emailHome"));
            email_count++;
          }

          if (!contact.getString("emailWork").equals("")) {
            if (email_count == 0) {
              intent.putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
              intent.putExtra(ContactsContract.Intents.Insert.EMAIL, contact.getString("emailWork"));
            } else if (email_count == 1) {
              intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
              intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_EMAIL, contact.getString("emailWork"));
            }
          }
        }

        
        intent.putExtra("finishActivityOnSaveCompleted", true);
        cordova.startActivityForResult(this, intent, INSERT_CONTACT);

        PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
        r.setKeepCallback(true);
        callbackContext.sendPluginResult(r);
      } catch (Exception ex) {
        PluginResult r = new PluginResult(PluginResult.Status.ERROR);
        callbackContext.sendPluginResult(r);
      }
      return true;
    }
    return false;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != Activity.RESULT_OK) {
      callbackContext.error("Parsing contact failed: " + resultCode);
    } else {
      callbackContext.success("saved");
    }
  }
}

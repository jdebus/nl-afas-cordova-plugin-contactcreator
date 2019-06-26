package nl.afas.cordova.plugin.contactcreator;

import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class ContactCreatorPlugin extends CordovaPlugin {

  private Context context;
  private CallbackContext callbackContext;

  private static JSONObject contact;

  @Override
  public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
    this.callbackContext = callbackContext;
    this.context = cordova.getActivity().getApplicationContext();


    if (action.equals("addContact")) {
      Intent intent = new Intent(Intent.ACTION_INSERT);
      intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

      try {
        contact = data.getJSONObject(0);
        if (contact != null) {

          Intent pickIntent = AccountManager.newChooseAccountIntent(null, null, new String[] {"com.microsoft.office.outlook.USER_ACCOUNT", "com.google"}, true, null, null, null, null);
          cordova.startActivityForResult(this, pickIntent, 1);

        }
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

    if(resultCode == RESULT_OK){
      try {

        String accountType = data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
        String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);


        String displayName = contact.optString("displayName");
        String phoneWork = contact.has("phoneWork") ? contact.getString("phoneWork") : null;
        String mobileWork = contact.has("mobileWork") ? contact.getString("mobileWork") : null;
        String emailWork = contact.has("emailWork") ? contact.getString("emailWork") : null;
        String street = contact.has("street") ? contact.getString("street") : null;
        String postCode = contact.has("postCode") ? contact.getString("postCode") : null;
        String city = contact.has("city") ? contact.getString("city") : null;
        String organization = contact.has("organization") ? contact.getString("organization") : null;
        String jobTitle = contact.has("jobTitle") ? contact.getString("jobTitle") : null;

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        int rawContactInsertIndex = ops.size();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, accountType)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, accountName)
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, displayName)
                .build());

        if(mobileWork != null){
          ops.add(ContentProviderOperation.
                  newInsert(ContactsContract.Data.CONTENT_URI)
                  .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                  .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                  .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobileWork)
                  .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,  ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                  .build());
        }

        if (phoneWork != null) {
          ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                  .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                  .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                  .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneWork)
                  .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                  .build());
        }

        if (emailWork != null) {
          ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                  .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                  .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                  .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailWork)
                  .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                  .build());
        }

        if (organization != null ) {
          ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                  .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                  .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                  .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, organization)
                  .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                  .build());
        }

        if (jobTitle != null ) {
          ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                  .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                  .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                  .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
                  .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                  .build());
        }

        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)

                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, street)

                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY, city)

                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, postCode)

                .build());

        context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        callbackContext.success("saved");
      }
      catch(Exception ex){
        callbackContext.error("failed");
      }
    }
  }
}

package nl.afas.cordova.plugin.contactcreator;

import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.util.Base64;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class ContactCreatorPlugin extends CordovaPlugin {

  private Context context;
  private CallbackContext callbackContext;

  private static JSONObject contact;

  private static String _accountName = null;
  private static String _accountType = null;

  @Override
  public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
    this.callbackContext = callbackContext;
    this.context = cordova.getActivity().getApplicationContext();
    try {

      if (action.equals("addContact")) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        contact = data.getJSONObject(0);

        if (contact != null) {
          _accountName = contact.has("accountName") ? contact.getString("accountName") : null;
          _accountType = contact.has("accountType") ? contact.getString("accountType") : null;

          if(_accountName == null || _accountType == null){
            Intent pickIntent = AccountManager.newChooseAccountIntent(null, null, new String[] {"com.microsoft.office.outlook.USER_ACCOUNT", "com.google"}, true, null, null, null, null);
            cordova.startActivityForResult(this, pickIntent, 1);
          }
          else{
            if(SaveContact(_accountName, _accountType))
            callbackContext.success("saved");
          else
            callbackContext.error("failed");  
          }
          
        }
        return true;
      }
      else if(action.equals("getAccount")) {
        Intent pickIntent = AccountManager.newChooseAccountIntent(null, null, new String[] {"com.microsoft.office.outlook.USER_ACCOUNT", "com.google"}, true, null, null, null, null);
        cordova.startActivityForResult(this, pickIntent, 2);
        return true;
      } 
  
    } catch (Exception ex) {
      PluginResult r = new PluginResult(PluginResult.Status.ERROR);
      callbackContext.sendPluginResult(r);
    }
    return false;
  }

  private Bitmap base64ToBitmap(String b64) {
    byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
    return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
  }
  

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {

    if(resultCode == RESULT_OK){
      try {

        String accountType = data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
        String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

        if(requestCode == 2) {
          callbackContext.success(accountType + "|" + accountName);
          return;
        }
        if(SaveContact(accountName, accountType))
          callbackContext.success("saved");
      }
      catch(Exception ex){

      }

      callbackContext.error("failed");
    }
  }

  private boolean SaveContact(String accountName, String accountType) {
    try {
      String displayName = contact.optString("name");
      String phoneWork = contact.has("phoneWork") ? contact.getString("phoneWork") : null;
      String mobileWork = contact.has("phoneMobile") ? contact.getString("phoneMobile") : null;
      String emailWork = contact.has("email") ? contact.getString("email") : null;
      String street = contact.has("street") ? contact.getString("street") : null;
      String postCode = contact.has("zipCode") ? contact.getString("zipCode") : null;
      String city = contact.has("city") ? contact.getString("city") : null;
      String organization = contact.has("companyName") ? contact.getString("companyName") : null;
      String jobTitle = contact.has("jobTitle") ? contact.getString("jobTitle") : null;
      String image = contact.has("imageData") ? contact.getString("imageData") : null;

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

      if(image != null) {
        image = image.substring(image.indexOf(",") + 1);
          byte[] imageAsBytes = Base64.decode(image.getBytes(), Base64.DEFAULT);
          Bitmap mBitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
          ByteArrayOutputStream stream = new ByteArrayOutputStream();
          if(mBitmap!=null){    // If an image is selected successfully
            mBitmap.compress(Bitmap.CompressFormat.PNG , 75, stream);

            // Adding insert operation to operations list
            // to insert Photo in the table ContactsContract.Data
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO,stream.toByteArray())
                    .build());

            try {
                stream.flush();
            }catch (Exception e) {
                
            }
          }
      }

      context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
    } catch(Exception ex)
    {
        return false;
    }

    return true;
  } 
}

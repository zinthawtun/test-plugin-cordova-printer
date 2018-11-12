package android_printer;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.util.PrintUtil;

/**
 * This class echoes a string called from JavaScript.
 */
public class CAPrinter extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("fastPrint")) {
            String message = args.getString(0);
            this.fastPrint(message, callbackContext);
            webView.loadUrl("javascript:alert('hello this is clicked');");
            return true;
        }
        return false;
    }

    private void fastPrintd(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
}
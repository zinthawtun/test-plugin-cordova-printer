package de.appplant.cordova.plugin.c_printer.CAPrinter;


import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.appplant.cordova.plugin.c_printer.util.PrintUtil;

/**
 * This class echoes a string called from JavaScript.
 */
public class CAPrinter extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("fastPrint")) {
            String message = args.getString(0);
            PrintUtil.printTest();
            this.checkPrint(message, callbackContext);
            webView.loadUrl("javascript:window.alert('hello this is clicked');");
            return true;
        }
        return false;
    }

    private void checkPrint(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
}

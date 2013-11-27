/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package com.appersonlabs.gigya;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollObject;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;

import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;
import com.gigya.socialize.GSResponseListener;
import com.gigya.socialize.android.GSAPI;
import com.gigya.socialize.android.event.GSLoginUIListener;

@Kroll.module(name = "Gigya", id = "com.appersonlabs.gigya")
public class GigyaModule extends KrollModule {

    
    class ResponseListener implements GSResponseListener {
        private KrollObject thisObject;
        private KrollFunction failure;
        private KrollFunction success;

        public ResponseListener(KrollObject thisObject, KrollDict dict) {
            this.thisObject = thisObject;
            this.success = (KrollFunction) dict.get("success");
            this.failure = (KrollFunction) dict.get("failure");
        }

        @Override
        public void onGSResponse(String method, GSResponse response, Object context) {
            if (response.getErrorCode() == 0 && success != null) {
                success.call(thisObject, GSObjectConverter.fromGSObject(response.getData()));
            }
            if (response.getErrorCode() != 0 && failure != null) {
                failure.call(thisObject, GSObjectConverter.fromGSObject(response.getData()));
            }
        }
        
    }
    
    
    class LoginListener implements GSLoginUIListener {
        private KrollObject thisObject;
        private KrollFunction failure;
        private KrollFunction success;

        public LoginListener(KrollObject thisObject, KrollDict dict) {
            this.thisObject = thisObject;
            this.success = (KrollFunction) dict.get("success");
            this.failure = (KrollFunction) dict.get("failure");
        }

        @Override
        public void onClose(boolean cancelled, Object context) {
            if (failure != null && cancelled) {
                KrollDict params = new KrollDict();
                params.put("code", -1);
                params.put("error", "User cancelled login");
                failure.call(thisObject, params);
            }
        }

        @Override
        public void onError(GSResponse response, Object context) {
            if (failure != null) {
                KrollDict params = new KrollDict();
                params.put("code", response.getErrorCode());
                params.put("error", response.getErrorMessage());
                failure.call(thisObject, params);
            }
        }

        @Override
        public void onLoad(Object context) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLogin(String provider, GSObject user, Object context) {
            if (success != null) {
                KrollDict eventParams = new KrollDict();
                eventParams.put("user", GSObjectConverter.fromGSObject(user));
                success.call(thisObject, eventParams);
            }
        }

    }

    // Standard Debugging variables
    private static final String TAG = "GigyaModule";

    @Kroll.onAppCreate
    public static void onAppCreate(TiApplication app) {
    }

    private GSAPI api;

    public GigyaModule() {
        super();
    }

    @Kroll.setProperty(name = "APIKey")
    public void setAPIKey(String apikey) {
        if (api == null) {
            api = new GSAPI(apikey, this.getActivity());
        }
    }

    @Kroll.method(name = "showLoginProvidersDialog", runOnUiThread = true)
    public void showLoginProvidersDialog(KrollDict dict) {
        api.showLoginUI(GSObjectConverter.toGSObject(dict), new LoginListener(getKrollObject(), dict), null);
    }
    
    @Kroll.method(name="loginToProvider", runOnUiThread=true)
    public void loginToProvider(KrollDict dict) throws Exception {
        String provider = dict.getString("name");
        if (provider == null) {
            Log.e(TAG, "missing provider name");
            return;
        }
        dict.put("provider", provider);
        api.login(GSObjectConverter.toGSObject(dict), new ResponseListener(getKrollObject(), dict), null);
    }

    @Override
    public String toString() {
        return "[GigyaModule]";
    }

}
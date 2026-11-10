/**
 * 
 */
package mx.bazaldua.guaruramovil.free.activity;

import java.util.Timer;
import java.util.TimerTask;

import mx.bazaldua.guaruramovil.free.R;
import mx.bazaldua.guaruramovil.free.util.Constants;
import mx.bazaldua.guaruramovil.free.util.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author Iván Bazaldúa Ramírez
 *
 */
public class MainActivity extends Activity {
//    private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkpEe1Yzd8kuuSNmS7euHjwHFfP8IPUwzS+/79PoEoyF52Nre9ch3fAqIRDgTw/7c1Vsh+6V26pUHV2OqqXVPqcGiAc5d4xwRyX2UNqLnfUwg8rEa8NrOtJwNfkHB4DOfbDgKPaLdJ/9ezBNEt2JmXobRia+lQHNewbDAQIkk2eIINjw/a7bjQYCLE6vyV1OP/YjXuEuM0A+MH6hy/tz/D/pHzt+UZuv+eLjm021h1JWJmz+YSXO7iISxtDByQzXj5vtqRKrsqsuziLiMfuKRQsxdm/sumLJ6lwZZ9IO0rntSNMO2TrLRN/fJ6eLVosOjeprdpWxbrPr+bWOa7GqyJwIDAQAB";

    // Generate your own 20 random bytes, and put them here.
//    private static final byte[] SALT = new byte[] {
//        -46, 65, 30, -128, -103, -57, 74, -64, 51, 88, -95, -45, 77, -117, -36, -113, -11, 32, -64,
//        89
//    };
//    private LicenseCheckerCallback mLicenseCheckerCallback;
//    private LicenseChecker mChecker;
    // A handler on the UI thread.
    private Handler mHandler;
    private ProgressDialog dialog;
    private SharedPreferences pref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		pref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        mHandler = new Handler();
        
        Log.d(Constants.LOG_APP, "LicenceMain["+pref.getBoolean(Constants.EXTRA_PARAM_LICENCE, false)+"] Contexto["+this.getApplication()+"]");
        displayResult(null);
//        if ( !pref.getBoolean(Constants.EXTRA_PARAM_LICENCE, false) ) {
//        
//	        // Try to use more data here. ANDROID_ID is a single point of attack.
//	        String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
//	
//	        // Library calls this when it's done.
//	        mLicenseCheckerCallback = new MyLicenseCheckerCallback();
//	        // Construct the LicenseChecker with a policy.
//	        mChecker = new LicenseChecker(
//	            this, new ServerManagedPolicy(this,
//	                new AESObfuscator(SALT, getPackageName(), deviceId)),
//	            BASE64_PUBLIC_KEY);
//	        try {
//	        	doCheck();
//	        }catch (Exception e) {
//	        	Log.e(Constants.LOG_APP, e.getMessage(), e);
//			}
//        } else {
//        	displayResult(null);
//        }
	}
	
	@Override
	protected void onDestroy() {
        super.onDestroy();
//        if (mChecker != null) mChecker.onDestroy();
		Util.unbindDrawables(findViewById(R.id.mainLayout));
		System.gc();
	}
	
	
    private void doCheck() {
        setProgressBarIndeterminateVisibility(true);
        
		dialog = new ProgressDialog(this); 
				
        dialog.setIndeterminate(true);
        dialog.setMessage( getString(R.string.checking_license) );
        dialog.setCancelable(false);
        dialog.show();
//       	mChecker.checkAccess(mLicenseCheckerCallback);
    }
    

    private void displayResult(final String result) {
        mHandler.post(new Runnable() {
            public void run() {
                setProgressBarIndeterminateVisibility(false);
                if ( dialog != null ) dialog.dismiss();
                Editor edit = pref.edit();
                edit.putBoolean(Constants.EXTRA_PARAM_LICENCE, true );
                edit.commit();
                edit = null;
                
        		Timer timer = new Timer();
        		timer.schedule(new TimerTask() {
        			@Override
        			public void run() {
        				startActivity(new Intent(MainActivity.this, EstadisticasAndroidActivity.class));
        				overridePendingTransition(R.anim.fade, R.anim.hold);
        				finish();
        			}
        		}, 1000);
        		
            }
        });
    }
    
//    private void displayDialog(final boolean showRetry) {
//        mHandler.post(new Runnable() {
//            public void run() {
//                setProgressBarIndeterminateVisibility(false);
//                showDialog(showRetry ? 1 : 0);
//            }
//        });
//    }
    
    protected Dialog onCreateDialog(int id) {
        final boolean bRetry = id == 1;
        return new AlertDialog.Builder(this)
            .setTitle(R.string.unlicensed_dialog_title)
            .setMessage(bRetry ? R.string.unlicensed_dialog_retry_body : R.string.unlicensed_dialog_body)
            .setPositiveButton(bRetry ? R.string.retry_button : R.string.buy_button, new DialogInterface.OnClickListener() {
                boolean mRetry = bRetry;
                public void onClick(DialogInterface dialog, int which) {
                    if ( mRetry ) {
                        doCheck();
                    } else {
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                "http://market.android.com/details?id=" + getPackageName()));
                            startActivity(marketIntent);                        
                    }
                }
            })
            .setNegativeButton(R.string.quit_button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).create();
    }

//    private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
//        public void allow(int policyReason) {
//            if (isFinishing()) {
//                // Don't update UI if Activity is finishing.
//                return;
//            }
//            // Should allow user access.
//            displayResult(getString(R.string.allow));
//        }
//
//        public void dontAllow(int policyReason) {
//            if (isFinishing()) {
//                // Don't update UI if Activity is finishing.
//                return;
//            }
////            displayResult(getString(R.string.dont_allow));
//            // Should not allow access. In most cases, the app should assume
//            // the user has access unless it encounters this. If it does,
//            // the app should inform the user of their unlicensed ways
//            // and then either shut down the app or limit the user to a
//            // restricted set of features.
//            // In this example, we show a dialog that takes the user to Market.
//            // If the reason for the lack of license is that the service is
//            // unavailable or there is another problem, we display a
//            // retry button on the dialog and a different message.
//            displayDialog(policyReason == Policy.RETRY);
//        }
//
//        public void applicationError(int errorCode) {
//            if (isFinishing()) {
//                // Don't update UI if Activity is finishing.
//                return;
//            }
//            // This is a polite way of saying the developer made a mistake
//            // while setting up or calling the license checker library.
//            // Please examine the error code and fix the error.
////            String result = String.format(getString(R.string.application_error), errorCode);
////            displayResult(result);
//            displayDialog(true);
//        }
//    }

}

package paswdbkp.tsl.com.articalslibrary.modasta.Utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by developer on 13/12/17.
 */

public class LaunchModastaUtils {

    private static final String isExternalLaunch = "isExternalLaunch";

    public static void launchVideoConsultationView(Context mContext, String videoSpltyText, String videoSply) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.modasta.android", "com.modasta.android.SplashActivity"));
        intent.putExtra(isExternalLaunch, true);
        intent.putExtra("launch_mode", "VIDEO");
        if (videoSpltyText == null || videoSpltyText.isEmpty() || videoSply == null || videoSply.isEmpty()) {
            intent.putExtra("external_app", "{}");
        } else {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("rel_splty_text", videoSpltyText);
                jsonObject.put("rel_splt", videoSply);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            intent.putExtra("external_app", jsonObject.toString());
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (mContext.getPackageManager().resolveActivity(intent, 0) != null) {
            mContext.startActivity(intent);
        } else {
            launchPlayStore(mContext);
        }
    }

    public static void launchLabView(Context mContext) {
        Intent intent = new Intent();
        intent.putExtra("launch_mode", "LAB");
        intent.putExtra(isExternalLaunch, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(new ComponentName("com.modasta.android", "com.modasta.android.SplashActivity"));

        if (mContext.getPackageManager().resolveActivity(intent, 0) != null) {
            mContext.startActivity(intent);
        } else {
            launchPlayStore(mContext);
        }
    }

    public static void launchTextQuery(Context mContext) {
        Intent intent = new Intent();
        intent.putExtra("launch_mode", "TEXT_QUERY");
        intent.putExtra(isExternalLaunch, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(new ComponentName("com.modasta.android", "com.modasta.android.SplashActivity"));

        if (mContext.getPackageManager().resolveActivity(intent, 0) != null) {
            mContext.startActivity(intent);
        } else {
            launchPlayStore(mContext);
        }
    }

    private static void launchPlayStore(Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.modasta.android")));
        } catch (android.content.ActivityNotFoundException anfe) {

        }
    }
}

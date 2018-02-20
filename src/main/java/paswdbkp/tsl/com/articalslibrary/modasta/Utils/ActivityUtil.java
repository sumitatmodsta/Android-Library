package paswdbkp.tsl.com.articalslibrary.modasta.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by developer on 02/01/18.
 */

public class ActivityUtil {

    static ActivityUtil mActivityUtil;

    private ActivityUtil() {
    }

    public static synchronized ActivityUtil getActivityUtilInstance() {
        if (mActivityUtil == null) {
            mActivityUtil = new ActivityUtil();
        }
        return mActivityUtil;
    }

    public static int screen_count;
    public JSONArray details;
    public static ArrayList<ScreenDetailModel> mDetailsList = new ArrayList<>();
    public String pincode;
    public String timestamp;
    public String app_version;
    public String phone_model;
    public String os_version;
    private static String pref_language;
    public String total_session_time;
    public String utm_source;
    public String utm_campaign;
    public String utm_medium;
    public String session_id;
    private static ScreenDetailModel tempDetail;

    public static void createActivityDetails(String screenName, long startTime, String action) {
        tempDetail = new ScreenDetailModel(screenName, startTime, action);
        screen_count++;
    }

    public static void addActivityDetails(String screenName, long endTime, String action) {
        if (tempDetail.screen_name.equals(screenName)) {
            tempDetail.time_spent = endTime - tempDetail.time_spent;
            mDetailsList.add(tempDetail);
            tempDetail = null;
        }
    }

    public static void setLanguagePref(String lang) {
        pref_language = lang;
    }

    public JSONObject getFinalData() {
        String versionName = ModastsLibConfig.VERSION_NAME;
        String PhoneModel = android.os.Build.MODEL;
        int os_version = android.os.Build.VERSION.SDK_INT;

        String pincode = "";

        JSONObject apsalarEvent = new JSONObject();
        try {
            apsalarEvent.put("screen_count", mDetailsList.size());
            apsalarEvent.put("details", getActivityData());
            apsalarEvent.put("pincode", pincode);
            apsalarEvent.put("timestamp", "" + new Date().getTime());
            apsalarEvent.put("app_version", "" + versionName);
            apsalarEvent.put("phone_model", "" + PhoneModel);
            apsalarEvent.put("os_version", "" + os_version);
            apsalarEvent.put("pref_language", pref_language);


            String utm_source = "";
            String utm_campaign = "";
            String utm_medium = "";

            apsalarEvent.put("utm_source", utm_source);
            apsalarEvent.put("utm_campaign", utm_campaign);
            apsalarEvent.put("utm_medium", utm_medium);

            apsalarEvent.put("company_name", ModastsLibConfig.COMPANY_NAME);
            apsalarEvent.put("email_id", utm_medium);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apsalarEvent;
    }

    private JSONArray getActivityData() {
        JSONArray screen_detail_array = new JSONArray();
        for (int i = 0; mDetailsList != null && i < mDetailsList.size(); i++) {
            JSONObject obj = new JSONObject();

            String screen_name = mDetailsList.get(i).screen_name;
            String screen_action = mDetailsList.get(i).action;

            if (screen_name.equalsIgnoreCase("CategoryListActivityModasta")) {
                screen_name = "CategoryList";
            } else if (screen_name.equalsIgnoreCase("ModastaArticleListActivity")) {
                screen_name = "ArticleList";
            } else if (screen_name.equalsIgnoreCase("ModastaArticleDetailActivity")) {
                screen_name = "ArticleDetail";
            }

            try {
                obj.put("screen_name", "" + screen_name);
                obj.put("time_spent", "" + mDetailsList.get(i).time_spent);
                obj.put("action", "" + screen_action);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            screen_detail_array.put(obj);
        }
        return screen_detail_array;
    }
}

class ScreenDetailModel {
    public String screen_name;
    public Long time_spent;
    public String action;

    private ScreenDetailModel() {
    }

    public ScreenDetailModel(String name, long time, String action) {
        this.screen_name = name;
        this.time_spent = time;
        this.action = action;
    }
}

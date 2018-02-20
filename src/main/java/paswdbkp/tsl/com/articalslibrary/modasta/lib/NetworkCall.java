package paswdbkp.tsl.com.articalslibrary.modasta.lib;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import paswdbkp.tsl.com.articalslibrary.R;
import paswdbkp.tsl.com.articalslibrary.modasta.CategoryListActivityModasta;
import paswdbkp.tsl.com.articalslibrary.modasta.ModastaArticleDetailActivity;
import paswdbkp.tsl.com.articalslibrary.modasta.ModastaArticleListActivity;
import paswdbkp.tsl.com.articalslibrary.modasta.UrlUtils;
import paswdbkp.tsl.com.articalslibrary.modasta.Utils.ActivityUtil;
import paswdbkp.tsl.com.articalslibrary.modasta.model.ArticleDetailModel;
import paswdbkp.tsl.com.articalslibrary.modasta.model.ArticleModel;
import paswdbkp.tsl.com.articalslibrary.modasta.model.CategoryModel;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by developer on 27/12/17.
 */

public class NetworkCall {

    private static Context mContext;

    public static void getStringResult(String url, Context context) {
        try {
            URL connectionUrl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) connectionUrl.openConnection();

            // add request type
            httpURLConnection.setRequestMethod("GET");

            // response code
            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode == HTTP_OK) {
                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();
                int tmp = inputStream.read();
                while (tmp != -1) {
                    stringBuffer.append((char) tmp);
                    tmp = inputStream.read();
                }
                inputStream.close();
                parseResult(stringBuffer.toString(), context);
                //return stringBuffer.toString();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void parseResult(String result, final Context context) {
        Object resultObject = null;
        if (context instanceof CategoryListActivityModasta) {
            resultObject = parseResultForCategoryListActivity(result);
            final Object finalResultObject = resultObject;
            ((CategoryListActivityModasta) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((CategoryListActivityModasta) context).showResultList(finalResultObject);
                }
            });
        } else if (context instanceof ModastaArticleListActivity) {
            resultObject = parseModastaArticleListActivity(result, (ModastaArticleListActivity) context);
            final Object finalResultObject1 = resultObject;
            ((ModastaArticleListActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ModastaArticleListActivity) context).showResultList(finalResultObject1);
                }
            });
        } else if (context instanceof ModastaArticleDetailActivity) {
            resultObject = parseArticleDetailListActivity(result, (ModastaArticleDetailActivity) context);
            final Object finalResultObject1 = resultObject;
            ((ModastaArticleDetailActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ModastaArticleDetailActivity) context).showResultList(finalResultObject1);
                }
            });
        }
    }

    private static Object parseResultForCategoryListActivity(String result) {
        ArrayList<CategoryModel> response = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(result);

            for (int i = 0; i < jsonArray.length(); i++) {
                CategoryModel categoryModel = new CategoryModel();
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                categoryModel.mName = jsonObject.optString(UrlUtils.getCategoryNameKey());
                categoryModel.mName = categoryModel.mName.replace("&amp;", "&");
                categoryModel.mSlug = jsonObject.optString(UrlUtils.getCategorySlugKey());
                String slugNameWithoutLang;
                if (!UrlUtils.mSelectedLanguage.equals("en")) {
                    slugNameWithoutLang = categoryModel.mSlug.substring(0, categoryModel.mSlug.lastIndexOf("-"));
                } else {
                    slugNameWithoutLang = categoryModel.mSlug;
                }
                categoryModel.mIconId = getIconIdBySlugName(slugNameWithoutLang);
                response.add(categoryModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private static int getIconIdBySlugName(String slugName) {
        switch (slugName) {
            case "eye-care":
                return R.drawable.eyecare;
            case "yoga":
                return R.drawable.yoga;
            case "healthyliving":
                return R.drawable.healthy_living;
            case "mens-health":
                return R.drawable.mens_health;
            case "pregnancy":
                return R.drawable.pregnancy;
            case "psychology":
                return R.drawable.psychology;
            case "sexual-health":
                return R.drawable.sexualhealth;
            case "skin-health":
                return R.drawable.skin_health;
            case "teen-health":
                return R.drawable.teen_health;
            case "uncategorised":
                return R.drawable.uncategorised;
            case "womens-health":
                return R.drawable.womenshealth;
            case "it-related-conditions":
                return R.drawable.itrelated;
            case "diseases-and-conditions":
                return R.drawable.diseases;
            case "uncategorized":
                return R.drawable.uncategorised;
            default:
                return R.drawable.uncategorised;
        }
    }

    private static Object parseModastaArticleListActivity(String result, ModastaArticleListActivity activity) {
        JSONObject response = null;
        try {
            response = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<ArticleModel> articleModelList = new ArrayList<>();
        if (activity.mTotalPage == -1) {
            activity.mTotalPage = response.optInt("totalpages", -1);
            if (activity.mTotalPage == -1) {
                //sendErrorReportToServer("false");
                return null;
            }
        }

        JSONArray articalJsonArray = response.optJSONArray("data");
        for (int i = 0; i < articalJsonArray.length(); i++) {
            try {
                ArticleModel articleModel = new ArticleModel();
                JSONObject object = articalJsonArray.getJSONObject(i);
                articleModel.mArticleId = object.optString("ID", null);
                articleModel.mArticleLabel = object.optString("title", null);
                String CONTENT = "excerpt";
                String CONTENT_DETAIL = "content";
                String content_raw;
                if (object.getString(CONTENT) == null || object.getString(CONTENT).equals("") || object.getString(CONTENT).equals("null")) {
                    content_raw = object.getString(CONTENT_DETAIL);
                } else {
                    content_raw = object.getString(CONTENT);
                }
                articleModel.mArticleDescription = content_raw;
                //JSONObject featuredImage = object.optJSONObject("featured_image");
                //articleModel.mThumbNailUrl = featuredImage.optString("guid", null);

                /*************************************************
                 *  Thumbnail for Article.
                 ************************************************/
                JSONObject mFeaturedImageObj = object.optJSONObject("featured_image");

                if (mFeaturedImageObj != null && mFeaturedImageObj != JSONObject.NULL) {
                    if (mFeaturedImageObj.has("errors") || mFeaturedImageObj.has("error_data")) {
                        articleModel.mThumbNailUrl = "https://www.modasta.com/wp-content/uploads/2016/06/placeholderimg_xxxdp.png";
                    } else if (mFeaturedImageObj.has("attachment_meta")) {
                        JSONObject att_meta_obj = new JSONObject(mFeaturedImageObj.getString("attachment_meta"));
                        JSONObject sizes_obj = new JSONObject(att_meta_obj.getString("sizes"));

                        if (getFeaturedImage(sizes_obj, "large") != null) {
                            articleModel.mThumbNailUrl = getFeaturedImage(sizes_obj, "large");
                        } else if (getFeaturedImage(sizes_obj, "slider") != null) {
                            articleModel.mThumbNailUrl = getFeaturedImage(sizes_obj, "slider");
                        } else if (getFeaturedImage(sizes_obj, "featured") != null) {
                            articleModel.mThumbNailUrl = getFeaturedImage(sizes_obj, "featured");
                        } else {
                            articleModel.mThumbNailUrl = mFeaturedImageObj.getString("guid");
                        }
                    } else {
                        articleModel.mThumbNailUrl = mFeaturedImageObj.getString("guid");
                    }
                } else {
                    articleModel.mThumbNailUrl = "https://www.modasta.com/wp-content/uploads/2016/06/placeholderimg_xxxdp.png";
                }
                articleModelList.add(articleModel);
            } catch (JSONException e) {
                //sendErrorReportToServer(e.getMessage());
                e.printStackTrace();
            }
        }
        return articleModelList;
    }

    private static String getFeaturedImage(JSONObject mObject, String key) {
        try {
            if (mObject.has(key)) {
                JSONObject featured_img_obj = new JSONObject(mObject.getString(key));
                return featured_img_obj.getString("url");
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Object parseArticleDetailListActivity(String result, ModastaArticleDetailActivity activity) {
        ArticleDetailModel articleDetailModel = parseArticleDetailJson(result, activity);
        return articleDetailModel;
    }

    private static ArticleDetailModel parseArticleDetailJson(String response, ModastaArticleDetailActivity context) {
        ArticleDetailModel articleDetailModel = null;
        if (!response.isEmpty()) {
            JSONObject articleJson = null;
            try {
                articleJson = new JSONObject(response);
                articleDetailModel = new ArticleDetailModel();
                articleDetailModel.mDate = articleJson.optString("date");
                articleDetailModel.mArticleLabel = articleJson.getString("title");
                articleDetailModel.mArticleDescription = articleJson.optString("content");
                articleDetailModel.mRelatedArticles = articleJson.optString("relatedArticle");
                setRelatedArticleMap(articleDetailModel.mRelatedArticles, context);
                articleDetailModel.mReferances = articleJson.optString("refference");
                articleDetailModel.rel_splty_text = articleJson.optString("rel_splty_text");
                articleDetailModel.rel_splty_text = articleDetailModel.rel_splty_text.replace("&amp;", "&");
                articleDetailModel.rel_splt = articleJson.optString("rel_splt");
            } catch (JSONException e) {
                articleDetailModel = null;
            }
        }
        return articleDetailModel;
    }

    private static void setRelatedArticleMap(String html, ModastaArticleDetailActivity activity) {
        if (activity.mPostIdUrlMap == null) {
            activity.mPostIdUrlMap = new HashMap<>();
        } else {
            activity.mPostIdUrlMap.clear();
        }
        String[] splitHtml = html.split("<article");
        for (int i = 1; i < splitHtml.length; i++) {
            String s = splitHtml[i];
            int startIndex = s.indexOf("postid=") + "postid=".length() + 1;
            int endIndex = s.indexOf("class") - 2;
            String id = s.substring(startIndex, endIndex);
            String[] temp = s.split("<a href=");
            String urlhtml = temp[1];
            endIndex = urlhtml.indexOf("rel=") - 2;
            urlhtml = urlhtml.substring(1, endIndex);
            activity.mPostIdUrlMap.put(urlhtml, id);
        }
    }

    public static void sendModastaStats() {
        ActivityUtil activityUtil = ActivityUtil.getActivityUtilInstance();
        JSONObject jsonObject = activityUtil.getFinalData();
        try {
            URL url = new URL(UrlUtils.getModastaStatsUrl());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-length", String.valueOf(jsonObject.toString().length()));
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setRequestProperty("user_data", jsonObject.toString());
            httpURLConnection.setRequestProperty("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1MTQ4NzEwNzMsImlzcyI6Imh0dHBzOlwvXC93d3cubW9kYXN0YS5jb20iLCJleHAiOjE1NDU5NzUwNzMsImRhdGEiOnsidXNlciI6eyJpZCI6IjI1MTYxIn19fQ.xkb3q2TqaOmdrkDqmtwI8-1cVmkksGr0BHdSrWivQAc");
            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.writeBytes(jsonObject.toString());
            dataOutputStream.close();

            int response = httpURLConnection.getResponseCode();

            InputStream inputStream = httpURLConnection.getInputStream();
            int temp = inputStream.read();
            StringBuffer stringBuffer = new StringBuffer();
            while (temp != -1) {
                stringBuffer.append((char) temp);
                temp = inputStream.read();
            }

            stringBuffer.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

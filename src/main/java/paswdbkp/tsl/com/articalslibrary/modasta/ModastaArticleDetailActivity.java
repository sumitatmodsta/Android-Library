package paswdbkp.tsl.com.articalslibrary.modasta;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import paswdbkp.tsl.com.articalslibrary.R;
import paswdbkp.tsl.com.articalslibrary.modasta.Utils.LaunchModastaUtils;
import paswdbkp.tsl.com.articalslibrary.modasta.Utils.NetworkUtils;
import paswdbkp.tsl.com.articalslibrary.modasta.callback.BaseActivity;
import paswdbkp.tsl.com.articalslibrary.modasta.lib.NetworkCall;
import paswdbkp.tsl.com.articalslibrary.modasta.model.ArticleDetailModel;

/**
 * Created by developer on 26/11/17.
 */

public class ModastaArticleDetailActivity extends BaseActivity {
    private TextView mDate, mTitle;
    private WebView mContent, mRelatedContent;
    private RelativeLayout mReferancesView;
    private TextView mReferances;
    private ArticleDetailModel mArticleDetailModel;
    private ContentLoadingProgressBar mProgressBar;

    private String mPostId;

    private ArticleDetailLoader mArticleDetailLoader;

    public HashMap<String, String> mPostIdUrlMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadView();
        mPostId = getIntent().getStringExtra("articleID");
        loadArticleDetailById(mPostId);
    }

    private void loadView() {
        setContentView(R.layout.article_detail_view);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.nestedScrollview);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

                if (diff <= 4000) {
                    ModastaArticleDetailActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showConsultOurDoctor();
                        }
                    });
                }
            }
        });
        mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);
        mDate = (TextView) findViewById(R.id.article_date);
        mTitle = (TextView) findViewById(R.id.article_title);
        mContent = (WebView) findViewById(R.id.article_content);

        mRelatedContent = (WebView) findViewById(R.id.related_article);
        mRelatedContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                WebView.HitTestResult hitTestResult = ((WebView) v).getHitTestResult();
                Log.e("sns web click", "getExtra = " + hitTestResult.getExtra() + "\t\t Type=" + hitTestResult.getType());
                return false;
            }
        });
        mRelatedContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (mPostIdUrlMap.containsKey(url)) {
                    String id = mPostIdUrlMap.get(url);
                    ModastaArticleDetailActivity.this.setResult(RESULT_OK, new Intent().putExtra("postId", id));
                    finish();
                }
                return false;
            }
        });

        mReferancesView = (RelativeLayout) findViewById(R.id.reference_detail_card);
        mReferancesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mReferances.getVisibility() == View.VISIBLE) {
                    mReferances.setVisibility(View.GONE);
                } else {
                    mReferances.setVisibility(View.VISIBLE);
                }
            }
        });
        mReferances = (TextView) findViewById(R.id.reference_detail_content);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    private void loadArticleDetailById(String articleId) {
        mArticleDetailLoader = new ArticleDetailLoader();
        mArticleDetailLoader.execute(UrlUtils.getArticleUrlById(articleId));
    }

    private void setViewContent(ArticleDetailModel response) {
        String mServerTime = convertToLocalTime(response.mDate);
        try {
            JSONObject mDateObj = new JSONObject(mServerTime);
            String date = mDateObj.getString("DATE");
            String time = mDateObj.getString("TIME");
            mDate.setText(date + " " + time);
            mTitle.setText(Html.fromHtml(response.mArticleLabel));
            mContent.loadData(prepareHtmlContent(response.mArticleDescription), "text/html", "UTF-8");
            mRelatedContent.loadData(prepareHtmlContent(response.mRelatedArticles), "text/html", "UTF-8");
            String ref = response.mReferances;
            if (ref.isEmpty()) {
                mReferancesView.setVisibility(View.GONE);
            } else {
                mReferances.setText(Html.fromHtml(ref));
                mReferancesView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
        }
    }

    private String prepareHtmlContent(String content) {
        String pish = "<html><head><style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/aparaj.ttf\")}body {font-family: MyFont;font-size: medium;text-align: justify;}</style></head><body>";
        String pas = "</body></html>";
        String myHtmlString = pish + content + pas;
        return myHtmlString;
    }

    boolean isConsultDialogVisible = false;

    private void showConsultOurDoctor() {
        if (isConsultDialogVisible) {
            return;
        }

        final Dialog dialog = new Dialog(ModastaArticleDetailActivity.this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.consult_our_doctor);
        dialog.setCancelable(false);

        TextView tv_consult_doctor = (TextView) dialog.findViewById(R.id.tv_consult_doctor);
        if (mArticleDetailModel.rel_splty_text == null || mArticleDetailModel.rel_splty_text.isEmpty()) {
            tv_consult_doctor.setText(ModastaArticleDetailActivity.this.getResources().getText(R.string.title_consult_doctor));
        } else {
            tv_consult_doctor.setText(ModastaArticleDetailActivity.this.getResources().getText(R.string.specialist_doctor) + " " + mArticleDetailModel.rel_splty_text);
        }

        Button bt_yes = (Button) dialog.findViewById(R.id.bt_yes);
        Button bt_later = (Button) dialog.findViewById(R.id.bt_later);

        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                LaunchModastaUtils.launchVideoConsultationView(ModastaArticleDetailActivity.this, mArticleDetailModel.rel_splty_text, mArticleDetailModel.rel_splt);
            }
        });

        bt_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        isConsultDialogVisible = true;
        dialog.show();
    }

    private static String convertToLocalTime(String servertime) {
        try {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date parsedDateFormat = inputFormat.parse(servertime);

            DateFormat timeFormat = new SimpleDateFormat("hh:mma");
            String convertedTime = timeFormat.format(parsedDateFormat);

            DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
            String convertedDate = dateFormat.format(parsedDateFormat);

            DateFormat dateFormatDetail = new SimpleDateFormat("MMM dd, yyyy");
            String convertedDateDetail = dateFormatDetail.format(parsedDateFormat);

            long daysDifference = daysBetween(parsedDateFormat, new Date());
            Log.e("DATE_CONVERTION_", "" + convertedDate + " " + convertedTime + " " + daysDifference);

            JSONObject dateInfo = new JSONObject();
            dateInfo.put("TIME", convertedTime.toLowerCase());
            dateInfo.put("DAYDIFFERENCE", daysDifference);
            dateInfo.put("DATE", convertedDate);
            dateInfo.put("DATE_DETAIL", convertedDateDetail);

            long diff = new Date().getTime() - parsedDateFormat.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            dateInfo.put("HOURDIFFERENCE", hours);
            dateInfo.put("MINUTEDIFFERENCE", minutes);
            dateInfo.put("SECONDSDIFFERENCE", seconds);

            //showLog("DATEOBJECT", dateInfo.toString());

            return dateInfo.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return servertime;
        }
    }

    private static long daysBetween(Date startDate, Date endDate) {
        Calendar sDate = getDatePart(startDate);
        Calendar eDate = getDatePart(endDate);

        long daysBetween = 0;
        while (sDate.before(eDate)) {
            sDate.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }

    private static Calendar getDatePart(Date date) {
        Calendar cal = Calendar.getInstance();       // get calendar instance
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
        cal.set(Calendar.MINUTE, 0);                 // set minute in hour
        cal.set(Calendar.SECOND, 0);                 // set second in minute
        cal.set(Calendar.MILLISECOND, 0);            // set millisecond in second

        return cal;                                  // return the date part
    }

    private void showNoNetworkError() {
        mArticleDetailLoader.cancel(true);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ModastaArticleDetailActivity.this);
        builder.setTitle("No Network");
        builder.setMessage("You are not connected to network.\n Please connect and try again");
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.create().show();
    }

    @Override
    public void showResultList(Object result) {
        mArticleDetailModel = (ArticleDetailModel) result;
        setViewContent(mArticleDetailModel);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public String getAction() {
        return "ArticleId!#!" + mPostId;
    }

    private class ArticleDetailLoader extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            if (!NetworkUtils.isNetworkAvailable(ModastaArticleDetailActivity.this)) {
                showNoNetworkError();
            }
        }

        @Override
        protected Void doInBackground(String... url) {
            try {
                NetworkCall.getStringResult(url[0], ModastaArticleDetailActivity.this);
                //return parseArticleDetailJson(result);
                /*URL uri = new URL(url[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) uri.openConnection();
                InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
                StringBuilder stringBuilder = new StringBuilder();
                int i = -1;
                do {
                    i = inputStreamReader.read();
                    stringBuilder.append((char) i);
                } while (i != -1);
                return parseArticleDetailJson(stringBuilder.toString());*/
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private ArticleDetailModel parseArticleDetailJson(String response) {
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
                    setRelatedArticleMap(articleDetailModel.mRelatedArticles);
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

        private void setRelatedArticleMap(String html) {
            if (mPostIdUrlMap == null) {
                mPostIdUrlMap = new HashMap<>();
            } else {
                mPostIdUrlMap.clear();
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
                mPostIdUrlMap.put(urlhtml, id);
            }
        }

        @Override
        protected void onPostExecute(Void articleDetailModel) {
            /*mArticleDetailModel = articleDetailModel;
            setViewContent(mArticleDetailModel);
            mProgressBar.setVisibility(View.GONE);*/
        }
    }

    @Override
    protected void onDestroy() {
        mRelatedContent.clearCache(true);
        mContent.clearCache(true);
        mArticleDetailModel = null;
        if (mPostIdUrlMap != null) {
            mPostIdUrlMap.clear();
        }
        super.onDestroy();
    }
}

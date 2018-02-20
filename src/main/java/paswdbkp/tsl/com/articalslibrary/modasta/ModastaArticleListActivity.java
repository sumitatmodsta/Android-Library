package paswdbkp.tsl.com.articalslibrary.modasta;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import paswdbkp.tsl.com.articalslibrary.R;
import paswdbkp.tsl.com.articalslibrary.modasta.Utils.NetworkUtils;
import paswdbkp.tsl.com.articalslibrary.modasta.adapter.ModastaArticleListAdapter;
import paswdbkp.tsl.com.articalslibrary.modasta.callback.BaseActivity;
import paswdbkp.tsl.com.articalslibrary.modasta.lib.NetworkCall;
import paswdbkp.tsl.com.articalslibrary.modasta.model.ArticleModel;

/**
 * Created by developer on 22/11/17.
 */

public class ModastaArticleListActivity extends BaseActivity {

    private final int ARTICLE_DETAIL_RESULT_CODE = 1;

    private ListView mArticalList;
    private ModastaArticleListAdapter mArticalListAdapter;
    private ArrayList<ArticleModel> mArticleModelList;
    private ContentLoadingProgressBar mLoadingProgressBar;

    private LoadArticle mLoadArticle;

    public int mTotalPage = -1;
    private int mRequestCount = 0;
    private String mSlug;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadView();
        mSlug = getIntent().getStringExtra(CategoryListActivityModasta.INTENT_KEY_CATEGORY_SLUG);
        loadArticle();
    }

    private void loadView() {
        setContentView(R.layout.article_list_view);
        mArticalList = (ListView) findViewById(R.id.artical_list_view);
        mArticalListAdapter = new ModastaArticleListAdapter(ModastaArticleListActivity.this);
        mArticalList.setAdapter(mArticalListAdapter);
        mArticalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchArticleDetaileView(mArticleModelList.get(position).mArticleId);
            }
        });
        mLoadingProgressBar = (ContentLoadingProgressBar) findViewById(R.id.progress_loading);
        mLoadingProgressBar.setVisibility(View.VISIBLE);
    }

    public void loadArticle() {
        if (mTotalPage == -1) {
            mRequestCount = 2;
            String url = UrlUtils.getArticalListUrlByLanguageAndPageIndex(mSlug, 1);
            mLoadArticle = new LoadArticle();
            mLoadArticle.execute(url);
            mLoadingProgressBar.setVisibility(View.VISIBLE);
        } else if (mRequestCount <= mTotalPage) {
            String url = UrlUtils.getArticalListUrlByLanguageAndPageIndex(mSlug, mRequestCount++);
            mLoadArticle = new LoadArticle();
            mLoadArticle.execute(url);
            mLoadingProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ARTICLE_DETAIL_RESULT_CODE && data != null) {
            String id = data.getStringExtra("postId");
            if (id != null) {
                launchArticleDetaileView(id);
            }
        }
    }

    private void launchArticleDetaileView(String articleId) {
        Intent intent = new Intent(ModastaArticleListActivity.this, ModastaArticleDetailActivity.class);
        intent.putExtra("articleID", articleId);
        startActivityForResult(intent, ARTICLE_DETAIL_RESULT_CODE);
    }

    private ArrayList<ArticleModel> parseArticleListResponse(JSONObject response) {
        ArrayList<ArticleModel> articleModelList = new ArrayList<>();
        if (mTotalPage == -1) {
            mTotalPage = response.optInt("totalpages", -1);
            if (mTotalPage == -1) {
                sendErrorReportToServer("false");
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
                sendErrorReportToServer(e.getMessage());
                e.printStackTrace();
            }
        }
        return articleModelList;
    }

    private String getFeaturedImage(JSONObject mObject, String key) {
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

    private void sendErrorReportToServer(String message) {
    }

    private void showNoNetworkError() {
        mLoadArticle.cancel(true);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ModastaArticleListActivity.this);
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
        ArrayList<ArticleModel> mTempList = (ArrayList<ArticleModel>) result;
        if (mArticleModelList == null) {
            mArticleModelList = mTempList;
        } else {
            for (int i = 0; i < mTempList.size(); i++) {
                mArticleModelList.add(mTempList.get(i));
            }
        }
        mArticalListAdapter.UpdateArticalList(mArticleModelList);
        mLoadingProgressBar.setVisibility(View.GONE);
    }

    @Override
    public String getAction() {
        return "userAction!#!" + mSlug;
    }

    private class LoadArticle extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            if (!NetworkUtils.isNetworkAvailable(ModastaArticleListActivity.this)) {
                showNoNetworkError();
            }
        }

        @Override
        protected Void doInBackground(String... voids) {
            try {
                NetworkCall.getStringResult(voids[0], ModastaArticleListActivity.this);
                /*URL articleUrl = new URL(voids[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) articleUrl.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();
                StringBuffer stringBuffer = new StringBuffer();
                while (data != -1) {
                    char c = (char) data;
                    stringBuffer.append(c);
                    data = inputStreamReader.read();
                }
                if (stringBuffer.length() > 0) {
                    JSONObject jsonObject = new JSONObject(stringBuffer.toString());
                    return jsonObject;
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            /*ArrayList<ArticleModel> mTempList = parseArticleListResponse(response);
            if (mArticleModelList == null) {
                mArticleModelList = parseArticleListResponse(response);
            } else {
                for (int i = 0; i < mTempList.size(); i++) {
                    mArticleModelList.add(mTempList.get(i));
                }
            }*/

        }
    }
}

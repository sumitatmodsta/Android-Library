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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import paswdbkp.tsl.com.articalslibrary.R;
import paswdbkp.tsl.com.articalslibrary.modasta.Utils.ActivityUtil;
import paswdbkp.tsl.com.articalslibrary.modasta.Utils.NetworkUtils;
import paswdbkp.tsl.com.articalslibrary.modasta.adapter.CategoryListAdapter;
import paswdbkp.tsl.com.articalslibrary.modasta.callback.BaseActivity;
import paswdbkp.tsl.com.articalslibrary.modasta.lib.NetworkCall;
import paswdbkp.tsl.com.articalslibrary.modasta.model.CategoryModel;

/**
 * Created by developer on 24/11/17.
 */

public class CategoryListActivityModasta extends BaseActivity {

    public static final String INTENT_KEY_CATEGORY_SLUG = "slug";

    private Spinner mLanguageSpinner;
    private ListView mListView;
    private ContentLoadingProgressBar mProgressBar;
    private CategoryListAdapter mCategoryListAdapter;
    private ArrayList<String> mLanguagesList;

    private String mSelectedLanguage;

    private LoadCategory mLoadCategory;

    private ArrayList<CategoryModel> mCategoryModelList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadView();
    }

    private ArrayList loadLanguage() {
        //String[] countryNames = {"Select Language", "English", "हिंदी (Hindi)", "ಕನ್ನಡ (Kannada)", "తెలుగు (Telugu)", "தமிழ் (Tamil)", "മലയാളം (Malayāḷam)"};

        //int flags[] = {R.drawable.language_ico, R.drawable.english_normal, R.drawable.hindi_normal, R.drawable.kannada_normal, R.drawable.telugu_normal, R.drawable.tamil_normal, R.drawable.malayalam_normal};
        mLanguagesList = new ArrayList<String>();
        mLanguagesList.add("Select Language");
        mLanguagesList.add("English");
        mLanguagesList.add("हिंदी (Hindi)");
        mLanguagesList.add("ಕನ್ನಡ (Kannada)");
        mLanguagesList.add("తెలుగు (Telugu)");
        mLanguagesList.add("தமிழ் (Tamil)");
        mLanguagesList.add("മലയാളം (Malayāḷam)");
        return mLanguagesList;
    }

    private void loadView() {
        setContentView(R.layout.category_list_view);

        mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.modasta_category_progress);
        mProgressBar.setVisibility(View.VISIBLE);

        mLanguageSpinner = (Spinner) findViewById(R.id.language_spinner);
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(CategoryListActivityModasta.this, android.R.layout.simple_spinner_dropdown_item, loadLanguage());
            mLanguageSpinner.setAdapter(adapter);
            mLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String lang = "en";
                    switch (position) {
                        case 0:
                            lang = "en";
                            break;
                        case 1:
                            lang = "en";
                            break;
                        case 2:
                            lang = "hi";
                            break;
                        case 3:
                            lang = "kn";
                            break;
                        case 4:
                            lang = "te";
                            break;
                        case 5:
                            lang = "ta";
                            break;
                        case 6:
                            lang = "ml";
                            break;
                        default:
                            lang = "en";
                            break;
                    }
                    getArticalCategory(lang);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    getArticalCategory("en");
                }
            });
        } catch (Exception e) {
            e.getMessage();
        }

        mListView = (ListView) findViewById(R.id.category_list_view);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showArticalListFromCategory(position);
            }
        });

        mCategoryListAdapter = new CategoryListAdapter(CategoryListActivityModasta.this);
        mListView.setAdapter(mCategoryListAdapter);
    }


    private void getArticalCategory(String lang) {
        mSelectedLanguage = lang;
        if (mLoadCategory != null) {
            mLoadCategory.cancel(true);
        }
        mLoadCategory = new LoadCategory();
        mLoadCategory.execute();
        ActivityUtil.setLanguagePref(lang);
    }

    /*private ArrayList<CategoryModel> parseCategoryResponse(JSONArray jsonArray) {
        ArrayList<CategoryModel> response = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            CategoryModel categoryModel = new CategoryModel();
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            categoryModel.mName = jsonObject.optString(UrlUtils.getCategoryNameKey());
            categoryModel.mName = categoryModel.mName.replace("&amp;", "&");
            categoryModel.mSlug = jsonObject.optString(UrlUtils.getCategorySlugKey());
            String slugNameWithoutLang;
            if (!mSelectedLanguage.equals("en")) {
                slugNameWithoutLang = categoryModel.mSlug.substring(0, categoryModel.mSlug.lastIndexOf("-"));
            } else {
                slugNameWithoutLang = categoryModel.mSlug;
            }
            response.add(categoryModel);
            categoryModel.mIconId = getIconIdBySlugName(slugNameWithoutLang);
        }
        return response;
    }

    private int getIconIdBySlugName(String slugName) {
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

    private void showCategoryList(ArrayList<CategoryModel> categoryModels) {
        if (categoryModels.size() > 0) {
            mListView.setVisibility(View.VISIBLE);
            mCategoryListAdapter.UpdateCategoryList(categoryModels);
            mCategoryListAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.GONE);
        } else {
            finish();
        }
    }*/

    private void showArticalListFromCategory(int position) {
        Intent intent = new Intent(CategoryListActivityModasta.this, ModastaArticleListActivity.class);
        intent.putExtra(INTENT_KEY_CATEGORY_SLUG, mCategoryModelList.get(position).mSlug);
        startActivity(intent);
    }

    private void showNoNetworkError() {
        mLoadCategory.cancel(true);
        final AlertDialog.Builder builder = new AlertDialog.Builder(CategoryListActivityModasta.this);
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
        ArrayList<CategoryModel> categoryModels = (ArrayList<CategoryModel>) result;
        mCategoryModelList = categoryModels;
        if (categoryModels.size() > 0) {
            mListView.setVisibility(View.VISIBLE);
            mCategoryListAdapter.UpdateCategoryList(categoryModels);
            mCategoryListAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.GONE);
        } else {
            finish();
        }
    }

    @Override
    public String getAction() {
        return "userAction!#!" + "health-a-z";
    }

    private void sendModastaStats() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkCall.sendModastaStats();
            }
        }).start();
        finish();
    }

    @Override
    protected void onPause() {
        sendModastaStats();
        super.onPause();
    }

    private class LoadCategory extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            if (!NetworkUtils.isNetworkAvailable(CategoryListActivityModasta.this)) {
                showNoNetworkError();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (mSelectedLanguage == null || mSelectedLanguage.isEmpty()) {
                    mSelectedLanguage = "en";
                }
                /*URL url = new URL(UrlUtils.getCategoryUrlByLanguage(mSelectedLanguage));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                int data = inputStreamReader.read();
                StringBuffer stringBuffer = new StringBuffer();
                while (data != -1) {
                    char current = (char) data;
                    stringBuffer.append(current);
                    data = inputStreamReader.read();
                }*/
                NetworkCall.getStringResult(UrlUtils.getCategoryUrlByLanguage(mSelectedLanguage), CategoryListActivityModasta.this);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

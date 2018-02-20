package paswdbkp.tsl.com.articalslibrary.modasta;

/**
 * Created by developer on 24/11/17.
 */

public class UrlUtils {
    private static final String CATEGORY_NAME = "$CATEGORY_NAME";
    private static final String CATEGORY_LANGUAGE = "$CATEGORY_LANGUAGE";
    private static final String PAGE_INDEX = "$PAGE_INDEX";

    private static final String ARTICLE_ID = "$ARTICLE_ID";

    private static final String DEFAULT_LANG = "en";
    private static final String DEFAULT_PAGE = "1";

    private static final String mCategoryUrl = "https://www.modasta.com/wp-json/taxonomies/category/terms?lang=";
    private static final String mArticlesUrl = "https://www.modasta.com/wp-json/posts?filter[posts_per_page]=10&filter[category_name]=" + CATEGORY_NAME + "&[order]=DESC&type[]=health-a-z&lang=" + CATEGORY_LANGUAGE + "&page=" + PAGE_INDEX;

    private static final String mArticleDetailsUrl = "https://www.modasta.com/wp-json/posts/" + ARTICLE_ID;

    private static final String NameKey = "name";
    private static final String SlugKey = "slug";

    private static final String mModastaStatsUrl = "https://dashboard.modasta.com/api/v1/useractivity/send";

    public static String mSelectedLanguage = DEFAULT_LANG;

    public static String getCategoryUrlByLanguage(String lang) {
        if (lang.isEmpty()) {
            lang = DEFAULT_LANG;
        }
        mSelectedLanguage = lang;
        return mCategoryUrl + lang;
    }

    public static String getCategoryNameKey() {
        return NameKey;
    }

    public static String getCategorySlugKey() {
        return SlugKey;
    }

    public static String getArticalListUrlByLanguageAndPageIndex(String category, int pageIndex) {
        String pageIndexString;
        String categoryListUrl = mArticlesUrl;

        if (category.isEmpty()) {
            return "false";
        } else {
            categoryListUrl = categoryListUrl.replace(CATEGORY_NAME, category);
        }

        if (pageIndex < 1) {
            pageIndexString = DEFAULT_PAGE;
        } else {
            pageIndexString = "" + pageIndex;
        }

        categoryListUrl = categoryListUrl.replace(CATEGORY_LANGUAGE, mSelectedLanguage);
        categoryListUrl = categoryListUrl.replace(PAGE_INDEX, pageIndexString);

        return categoryListUrl;
    }

    public static String getArticleUrlById(String articleId) {
        String url = mArticleDetailsUrl.replace(ARTICLE_ID, articleId);
        if (mSelectedLanguage != null || mSelectedLanguage.isEmpty()) {
            url = url + "?lang=" + mSelectedLanguage;
        }
        return url;
    }

    public static String getModastaStatsUrl() {
        return mModastaStatsUrl;
    }
}

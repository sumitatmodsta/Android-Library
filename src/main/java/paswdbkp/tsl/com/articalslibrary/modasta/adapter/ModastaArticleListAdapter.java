package paswdbkp.tsl.com.articalslibrary.modasta.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.HashMap;

import paswdbkp.tsl.com.articalslibrary.R;
import paswdbkp.tsl.com.articalslibrary.modasta.ModastaArticleListActivity;
import paswdbkp.tsl.com.articalslibrary.modasta.model.ArticleModel;

/**
 * Created by developer on 26/11/17.
 */

public class ModastaArticleListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ArticleModel> mArticalList;
    public HashMap<String, Bitmap> mImageMapper;

    public ModastaArticleListAdapter(Context context) {
        this.mContext = context;
        if (mImageMapper == null) {
            mImageMapper = new HashMap<>();
        }
    }

    public void UpdateArticalList(ArrayList articalList) {
        this.mArticalList = articalList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mArticalList == null ? 0 : mArticalList.size();
    }

    @Override
    public Object getItem(int position) {
        Log.e("sns", "" + position);
        return mArticalList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == mArticalList.size() - 1) {
            ((ModastaArticleListActivity) mContext).loadArticle();
        }
        Log.e("sns", "" + position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.artical_list_item_view, parent, false);
            viewHolder.mArticleThumbnail = (ImageView) convertView.findViewById(R.id.article_thumbnail);
            viewHolder.mArticleLabel = (TextView) convertView.findViewById(R.id.article_label);
            viewHolder.mArticleDescribtion = (TextView) convertView.findViewById(R.id.article_describtion);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ArticleModel articleModel = mArticalList.get(position);

        Glide.with(mContext)
                .load(articleModel.mThumbNailUrl)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .fitCenter()
                .error(R.drawable.pregnancy)
                .into(viewHolder.mArticleThumbnail);
        viewHolder.mArticleThumbnail.setScaleType(ImageView.ScaleType.FIT_XY);

        viewHolder.mArticleLabel.setText(Html.fromHtml(articleModel.mArticleLabel));
        Spanned spanned = Html.fromHtml(articleModel.mArticleDescription);
        viewHolder.mArticleDescribtion.setText(spanned.toString());
        String finalHtmlAsSpanned = articleModel.mArticleDescription;
        if (finalHtmlAsSpanned.length() > 55) {
            finalHtmlAsSpanned = finalHtmlAsSpanned.substring(0, 55) + "...";
            viewHolder.mArticleDescribtion.setText(Html.fromHtml(finalHtmlAsSpanned + "<font color='red'> <u>Read More</u></font>"));
        } else {
            viewHolder.mArticleDescribtion.setText(finalHtmlAsSpanned);
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView mArticleThumbnail;
        TextView mArticleLabel;
        TextView mArticleDescribtion;
    }
}

package paswdbkp.tsl.com.articalslibrary.modasta.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import paswdbkp.tsl.com.articalslibrary.R;
import paswdbkp.tsl.com.articalslibrary.modasta.model.CategoryModel;

/**
 * Created by developer on 24/11/17.
 */

public class CategoryListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<CategoryModel> mCategoryList;

    public CategoryListAdapter(Context context) {
        this.mContext = context;
    }

    public void UpdateCategoryList(ArrayList<CategoryModel> modelArrayList) {
        this.mCategoryList = modelArrayList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mCategoryList != null ? mCategoryList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mCategoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.category_list_item_view, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mIcon = (ImageView) convertView.findViewById(R.id.category_icon);
            viewHolder.mCategoryName = (TextView) convertView.findViewById(R.id.category_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CategoryModel model = mCategoryList.get(position);
        viewHolder.mIcon.setImageResource(model.mIconId);
        viewHolder.mCategoryName.setText(model.mName);
        return convertView;
    }

    private static class ViewHolder {
        ImageView mIcon;
        TextView mCategoryName;
    }
}

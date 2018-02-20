package paswdbkp.tsl.com.articalslibrary.modasta;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import paswdbkp.tsl.com.articalslibrary.R;
import paswdbkp.tsl.com.articalslibrary.modasta.Utils.ActivityUtil;
import paswdbkp.tsl.com.articalslibrary.modasta.Utils.LaunchModastaUtils;

/**
 * Created by developer on 22/11/17.
 */

public class ModastaTileView extends RelativeLayout {

    private Context mContext;

    public ModastaTileView(Context context) {
        super(context);
        initView(context);
    }

    public ModastaTileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ModastaTileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(final Context context) {
        ActivityUtil.getActivityUtilInstance();
        mContext = context;
        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.modasta_tile_view, null, false);

        relativeLayout.findViewById(R.id.home_view_health_information_label).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showModastaOptionView();
            }
        });

        if (this.getChildCount() > 1) {
            ModastaTileView.this.removeAllViews();
        } else {
            ModastaTileView.this.addView(relativeLayout);
        }
    }

    private void showModastaOptionView() {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.modasta_option_select_view);

        dialog.findViewById(R.id.view_group_health).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                launchArticalsListView();
            }
        });

        dialog.findViewById(R.id.view_group_video).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                LaunchModastaUtils.launchVideoConsultationView(mContext, null, null);
            }
        });

        dialog.findViewById(R.id.view_group_lab).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                LaunchModastaUtils.launchLabView(mContext);
            }
        });

        dialog.findViewById(R.id.view_group_text_query).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                LaunchModastaUtils.launchLabView(mContext);
            }
        });

        dialog.show();
    }

    private void launchArticalsListView() {
        Intent intent = new Intent(mContext, CategoryListActivityModasta.class);
        mContext.startActivity(intent);
    }

}

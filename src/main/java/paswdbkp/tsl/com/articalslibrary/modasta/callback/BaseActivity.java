package paswdbkp.tsl.com.articalslibrary.modasta.callback;

import android.support.v7.app.AppCompatActivity;

import java.util.Date;

import paswdbkp.tsl.com.articalslibrary.modasta.Utils.ActivityUtil;

/**
 * Created by developer on 28/12/17.
 */

public abstract class BaseActivity extends AppCompatActivity {

    public abstract void showResultList(Object result);

    public abstract String getAction();

    @Override
    protected void onResume() {
        super.onResume();
        long start_log = new Date().getTime();
        ActivityUtil.createActivityDetails(this.getClass().getSimpleName(), start_log, getAction());
    }

    @Override
    protected void onPause() {
        super.onPause();
        long end_log = new Date().getTime();
        ActivityUtil.addActivityDetails(this.getClass().getSimpleName(), end_log, getAction());
    }
}

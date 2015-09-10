package devapp.pro.vk.lists;

import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.androidquery.AQuery;

import devapp.pro.vk.R;

/**
 * Adapter for friends list
 */
public class FriendsList implements SimpleAdapter.ViewBinder {
    @Override
    public boolean setViewValue(View view, Object data, String s) {
        switch (view.getId()){
            case R.id.avatar:
                AQuery aq = new AQuery(view);
                aq.id(R.id.avatar).image((String) data, false, true, 0, 0);
                return true;
            case R.id.name:
                ((TextView)view).setText((String)data);
                return true;
        }
        return false;
    }
}

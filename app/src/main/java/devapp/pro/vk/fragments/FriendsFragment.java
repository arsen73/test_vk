package devapp.pro.vk.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import devapp.pro.vk.R;
import devapp.pro.vk.lists.FriendsList;

/**
 * Fragment for friends list
 */
public class FriendsFragment extends Fragment {

    ArrayList<Map<String , Object>> data;
    private SimpleAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = new ArrayList<>();
        String from[] = {
                "name",
                "avatar",
        };
        int to[] = {
                R.id.name,
                R.id.avatar,
        };
        adapter = new SimpleAdapter(getActivity(), data, R.layout.friends_item, from, to);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.friends_fragment, container, false);

        adapter.setViewBinder(new FriendsList());
        ListView listView = (ListView) v.findViewById(R.id.friends_list);
        listView.setAdapter(adapter);

        final Fragment self = this;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
                toolbar.setTitle((String) data.get(i).get("name"));

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                MessageFragment fragment = new MessageFragment();
                fragment.user_id = Integer.parseInt((String)data.get(i).get("id"));
                fragmentManager.beginTransaction()
                        .remove(self)
                        .addToBackStack("list")
                        .add(R.id.container, fragment)
                        .commit();
            }
        });

        VKParameters params = new VKParameters();
        params.put(VKApiConst.FIELDS, "nickname, photo_100");
        VKRequest request = VKApi.friends().get(params);

        request.setRequestListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                JSONArray jsonArray;
                try {
                    JSONObject jsObj = response.json.getJSONObject("response");
                    if(jsObj.getInt("count") < 1){
                        Toast t = Toast.makeText(getContext(), getString(R.string.error_empty_friends_list), Toast.LENGTH_LONG);
                        t.show();
                        return;
                    }
                    jsonArray = jsObj.getJSONArray("items");
                    int i = jsonArray.length()-1;
                    for(; i >= 0 ; i--) {
                        JSONObject JsonStr = jsonArray.getJSONObject(i);
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("id", JsonStr.getString("id"));
                        map.put("name", JsonStr.getString("first_name") +" "+JsonStr.getString("last_name"));
                        map.put("avatar", JsonStr.getString("photo_100"));
                        data.add(map);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        request.start();

        return v;
    }
}

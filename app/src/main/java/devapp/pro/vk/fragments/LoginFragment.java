package devapp.pro.vk.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

import devapp.pro.vk.R;

/**
 * Created by arseniy on 10/09/15.
 */
public class LoginFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.login_fragment, container, false);

        final Fragment fragment = this;

        Button loginButton = (Button) v.findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            private final String[] sVkontakteScopes = new String[]{
                    VKScope.FRIENDS,
                    VKScope.PHOTOS,
                    VKScope.NOHTTPS,
                    VKScope.MESSAGES,
            };
            @Override
            public void onClick(View view) {
                VKSdk.login(fragment.getActivity(), sVkontakteScopes);
            }
        });

        return v;
    }

}

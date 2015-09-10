package devapp.pro.vk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import devapp.pro.vk.fragments.FriendsFragment;
import devapp.pro.vk.fragments.LoginFragment;
import devapp.pro.vk.services.OpenUrlService;


public class MainActivity extends FragmentActivity {

    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        // Login
        FragmentManager fragmentManager = getSupportFragmentManager();
        LoginFragment fragment = new LoginFragment();
        fragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // Friends list
                FragmentManager fragmentManager = getSupportFragmentManager();
                FriendsFragment fragment = new FriendsFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();
            }

            @Override
            public void onError(VKError error) {
                // Error login
                Toast t = Toast.makeText(getApplicationContext(), getString(R.string.error_login), Toast.LENGTH_LONG);
                t.show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onStop(){
        super.onStop();
        Intent intent = new Intent(this, OpenUrlService.class);
        startService(intent);
    }
}



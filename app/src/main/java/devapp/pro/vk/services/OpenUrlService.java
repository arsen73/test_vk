package devapp.pro.vk.services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Service open web page
 */
public class OpenUrlService extends Service {

    public int open_count = 1;

    public final int max_open_count = 3;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        final Service self = this;

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://growapp.biz"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                open_count++;
                if(open_count > max_open_count){
                    timer.cancel();
                    timer.purge();
                    self.onDestroy();
                }
            }
        }, 0, 60000);
        return 0;
    }


}

package salima.agenda;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import salima.agenda.R;
import com.google.gson.Gson;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class JobSchedulerService extends JobService {
        private boolean categoryFlag = false;
        private ArrayList<Evento> events = new ArrayList<>();
        private String[] stringsssss;
        private HashMap<String, Boolean> flags = new HashMap<>();

        public JobSchedulerService() {
            super();
        }

        /* GET SCHEDULER CATEGORY */
        public class GetCategoriesSchedulerTask extends AsyncTask<String, Void, String[]> {
            String res;
            String uri;

            public GetCategoriesSchedulerTask(String uri) {
                super();
                this.uri = uri;
            }

            @Override
            protected String[] doInBackground(String... params) {
                ClientResource clientResource = new ClientResource(uri);
                Log.i("getcategory", "clientresrou");
                Gson gson = new Gson();
                String[] c;
                try {
                    res = clientResource.get().getText();
                    Log.i("getcategory", "fa il get");
                } catch (ResourceException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("getcategory", "gson");
                c = gson.fromJson(res, String[].class);
                Log.i("getcategory", "sono");
                categoryFlag = true;
                Log.i("getcategory", "sono arrivato nel post");

                stringsssss = c;
                return c;
            }
        }


        /* GET SCHEDULER EVENT */
        public class GetEventsSchedulerTask extends AsyncTask<String, Void, Evento[]> {
            String res;
            String uri;

            public GetEventsSchedulerTask(String uri) {
                super();
                this.uri = uri;
            }

            @Override
            protected Evento[] doInBackground(String... params) {
                Gson gson = new Gson();
                ClientResource cr = new ClientResource(uri);

                try {
                    res = cr.get().getText();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Evento[] event = gson.fromJson(res, Evento[].class);

                for (int i = 0; i < event.length; i++) {
                    //flags.put(event[i].getCategory(), true);
                    events.add(event[i]);
                }
                return event;

            }
        }


        @Override
        public boolean onStartJob(JobParameters params) {
            mJobHandler.sendMessage(Message.obtain(mJobHandler, 1, params));
            return true;
        }

        @Override
        public boolean onStopJob(JobParameters params) {
            mJobHandler.removeMessages(1);
            return false;
        }


        private Handler mJobHandler = new Handler(new Handler.Callback() {
            private final long HOUR_IN_MILLIS = 3600000;

            @Override
            public boolean handleMessage(Message msg) {
                Log.i("JOBService", "service active");
                Log.i("getcategory", "http://10.0.2.2:8182/agenda/allcategories/" + MainActivity.getCode() + "/" + MainActivity.getUsername());
                new GetCategoriesSchedulerTask("http://10.0.2.2:8182/agenda/allcategories/" + MainActivity.getCode() + "/" + MainActivity.getUsername()).execute();
                while(!categoryFlag)Log.i("JOBService", "while");
                for (String category : stringsssss) {
                    flags.put(category,false);
                    new GetEventsSchedulerTask("http://10.0.2.2:8182/agenda/allevents/" + MainActivity.getCode() + "/" + MainActivity.getUsername() + "/" + category).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                boolean flag=true;
                do{
                    for(Boolean b: flags.values())
                        if(!b)
                            flag=b;

                } while(flag);


                long timemillis = new Date().getTime();
                Log.i("JOBService", "numero eventi:"+events.size());

                    for (Evento e : events) {
                        long difference = 0;
                        try {
                            difference = new SimpleDateFormat("dd/MM/yyyy").parse(e.getData()).getTime() - timemillis;
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        Log.i("JOBService", "time " + difference);
                        if (difference > 0 && difference < HOUR_IN_MILLIS)
                            sendNotification(e, MainActivity.getUsername());
                    }


                jobFinished((JobParameters) msg.obj, false);
                return true;
            }
        });


        ///Gestisce le notifiche da inviare
        public void sendNotification(Evento e, String user) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.ico)
                            .setContentTitle("Agend : event expires in an hour")
                            .setContentText(user + ", your event is going to expire.\n" + e.getDescrizione() + " " + e.getData());

            Intent resultIntent = new Intent(getApplicationContext(), Login.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
            stackBuilder.addParentStack(Login.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            //Costruisco la notifica
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(1, mBuilder.build());
        }
    }
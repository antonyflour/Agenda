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

import com.google.gson.Gson;

import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class JobSchedulerService extends JobService {
        private boolean categoryFlag = false;
        private ArrayList<Evento> eventiTotali = new ArrayList<>();
        private String[] categories;
        private HashMap<String, Boolean> flags = new HashMap<>();


        private boolean eventiRecuperati = false;

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
                Log.i("getcategory", "clientresource");
                Gson gson = new Gson();
                String[] c;
                try {
                    clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, MainActivity.getUsername(), MainActivity.getPassword());
                    res = clientResource.get().getText();
                    Log.i("getcategory", "fa il get");
                } catch (ResourceException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Evento[] eventi = gson.fromJson(res, Evento[].class);
                List<String> categorie = new ArrayList<String>();
                for(Evento e : eventi){
                    if(!categorie.contains(e.getCategoria())){
                        categorie.add(e.getCategoria());
                    }
                }
                categories = new String[categorie.size()];
                categories = categorie.toArray(categories);

                for(String s : categories)
                    Log.i("**CATEGORIE: ", s);

                categoryFlag = true;
                return categories;
            }
        }


        /* GET SCHEDULER EVENT */
        public class GetEventsSchedulerTask extends AsyncTask<String, Void, Evento[]> {
            String res;
            String uri;
            String category;

            public GetEventsSchedulerTask(String uri, String category) {
                super();
                this.uri = uri;
                this.category = category;
            }

            @Override
            protected Evento[] doInBackground(String... params) {
                ClientResource clientResource = new ClientResource(uri);
                Gson gson = new Gson();
                Evento[] events;

                try {
                    clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, MainActivity.getUsername(), MainActivity.getPassword());
                    res = clientResource.get().getText();
                } catch (ResourceException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                events = gson.fromJson(res, Evento[].class);
                ArrayList<Evento> eventi = new ArrayList<Evento>();
                for(Evento e : events){
                    if(e.getCategoria().equalsIgnoreCase(category)){
                        eventi.add(e);
                    }
                }
                eventiTotali = eventi;

                events = eventi.toArray(events);

                return events;

            }
        }

    public class GetAllEventsSchedulerTask extends AsyncTask<String, Void, Evento[]> {
        String res;
        String uri;

        public GetAllEventsSchedulerTask(String uri) {
            super();
            this.uri = uri;
        }

        @Override
        protected Evento[] doInBackground(String... params) {
            ClientResource clientResource = new ClientResource(uri);
            Gson gson = new Gson();
            Evento[] events = new Evento[0];

            try {
                clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, MainActivity.getUsername(), MainActivity.getPassword());
                res = clientResource.get().getText();
                if(clientResource.getStatus().getCode() == 200){
                    events = gson.fromJson(res, Evento[].class);
                    eventiTotali.addAll(Arrays.asList(events));
                    eventiRecuperati = true;
                }
                else{
                    eventiTotali.clear();
                    eventiRecuperati = true;
                }
            } catch (ResourceException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return events;

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

                new GetAllEventsSchedulerTask("http://10.0.2.2:8182/events").execute();
                while(!eventiRecuperati);

                Log.i("***AGENDA", "Eventi recuperati: "+eventiTotali.size());

                for (Evento e : eventiTotali) {
                        long difference = 0;
                        try {
                            long actualMillis = new Date().getTime();

                            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(e.getData());
                            Date time = new SimpleDateFormat("HH:mm").parse(e.getOra());
                            Calendar calTime =Calendar.getInstance();
                            calTime.setTime(time);

                            Calendar complete =Calendar.getInstance();
                            complete.setTime(date);
                            complete.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY));
                            complete.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE));

                            long timeInMillis =complete.getTimeInMillis();

                            difference = timeInMillis - actualMillis;

                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }

                        Log.i("JOBService", "time " + difference);
                        if (difference > 0 && difference < HOUR_IN_MILLIS)
                            sendNotification(e, MainActivity.getUsername());
                    }

                jobFinished((JobParameters) msg.obj, false);

                return true;

                //VECCHIO CODICE
//                Log.i("JOBService", "service active");
//                Log.i("getcategory", "http://10.0.2.2:8182/events" + "   **** GET CATEGORIES");
//                new GetCategoriesSchedulerTask("http://10.0.2.2:8182/events").execute();
//
//                //attendo il recupero delle informazioni
//                while(!categoryFlag);
//
//                if(categories == null){
//                    Log.i("**AGENDA", "categories è nullo");
//                }
//                for (String category : categories) {
//                    flags.put(category,false);
//                    new GetEventsSchedulerTask("http://10.0.2.2:8182/events", category).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                }
//
//                boolean flag=true;
//                do{
//                    for(Boolean b: flags.values())
//                        if(!b)
//                            flag=b;
//
//                } while(flag);
//
//
//                long timemillis = new Date().getTime();
//                Log.i("JOBService", "numero eventi:"+events.size());
//
//                    for (Evento e : events) {
//                        long difference = 0;
//                        try {
//                            difference = new SimpleDateFormat("dd/MM/yyyy").parse(e.getData()).getTime() - timemillis;
//                        } catch (ParseException e1) {
//                            e1.printStackTrace();
//                        }
//                        Log.i("JOBService", "time " + difference);
//                        if (difference > 0 && difference < HOUR_IN_MILLIS)
//                            sendNotification(e, MainActivity.getUsername());
//                    }
//
//
//                jobFinished((JobParameters) msg.obj, false);
//                return true;
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
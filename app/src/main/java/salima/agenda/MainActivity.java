package salima.agenda;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import salima.agenda.R;
import com.google.gson.Gson;

import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        EventFragment.ListSelectionListener, FragmentAddEvent.NoticeDialogListner,
        TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener, FragmentAddCategory.NoticeDialogListner2 {


    private final String BASE_URI = "http://10.0.2.2:8182/";
    public static ArrayList<Evento> currentEvents;
    private String currentCategory;
    private static String password;
    private static String username;

    private NavigationView navigationView;
    private String dateDialog, timeDialog;
    protected static Evento eventToSend;

    public static final String CATEGORY = "currentCategory";
    private JobScheduler jobScheduler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentEvents = new ArrayList<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentAddEvent fad = new FragmentAddEvent();
                fad.show(getFragmentManager(), "x");
            }
        });


        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.List_event, new EventFragment());
        fragmentTransaction.commit();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Intent i = getIntent();
        password = i.getStringExtra(Login.PASSWORD);
        username = i.getStringExtra(Login.USERNAME);

        TextView t = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textViewNavHeader);
        t.setText(username);
        Button button = (Button) navigationView.getHeaderView(0).findViewById(R.id.logoutButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCredentials();
                jobScheduler.cancel(1);
            }
        });


        new GetCategoriesTask(BASE_URI + "events").execute();


        jobScheduler = (JobScheduler) getSystemService( Context.JOB_SCHEDULER_SERVICE );
        JobInfo.Builder builder = new JobInfo.Builder(1,new ComponentName(getPackageName(), JobSchedulerService.class.getName()));
        builder.setPeriodic(10000);
        jobScheduler.schedule(builder.build());

    }

    private void deleteCredentials(){
        SharedPreferences sharedPreferences = getSharedPreferences(Login.SH, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("username");
        editor.remove("password");
        editor.commit();
        Toast.makeText(getApplicationContext(), "You'have been logout", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), Login.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    //Tre puntini sopra
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.info) {
            Toast.makeText(this, "developed by Salima", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Vista categorie
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        for (int i = 0; i < navigationView.getMenu().size(); i++)
            navigationView.getMenu().getItem(i).setChecked(false);
        currentCategory = item.getTitle().toString();
        getSupportActionBar().setTitle(currentCategory);
        item.setChecked(true);

        new GetEventTask(BASE_URI+"events", currentCategory).execute();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void updateCategories(String[] categories) {
        Menu menu = navigationView.getMenu();
        menu.clear();

        for (String c : categories) {
            menu.add(c).setIcon(R.drawable.ic_menu_gallery);
        }

        if (menu.size() > 0) {
            menu.getItem(0).setChecked(true);
            currentCategory = menu.getItem(0).getTitle().toString();
            getSupportActionBar().setTitle(currentCategory);
            new GetEventTask(BASE_URI+"events", currentCategory).execute();
        }

    }

    @Override
    public void onListSelection(int index) {
        eventToSend = currentEvents.get(index);
        Intent intent = new Intent(getApplicationContext(), ModifyEvent.class);
        intent.putExtra(Login.PASSWORD, password);
        intent.putExtra(Login.USERNAME, username);
        intent.putExtra(CATEGORY, currentCategory);
        startActivity(intent);
    }

    public static Evento sendEvent() {
        return eventToSend;
    }


    // creazione evento
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) throws ParseException {
        EditText editDescrizione = (EditText) dialog.getDialog().findViewById(R.id.textDescrizione);
        EditText editCategoria = (EditText) dialog.getDialog().findViewById(R.id.textCategoria);
        EditText editLuogo = (EditText) dialog.getDialog().findViewById(R.id.textLuogo);
        EditText editNote = (EditText) dialog.getDialog().findViewById(R.id.textNote);
        EditText editDurata = (EditText) dialog.getDialog().findViewById(R.id.textDurata);
        EditText editDate = (EditText) dialog.getDialog().findViewById(R.id.textDate);
        EditText editTime = (EditText) dialog.getDialog().findViewById(R.id.textTime);

        String descrizione = editDescrizione.getText().toString();
        String categoria = editCategoria.getText().toString();

        String luogo = editLuogo.getText().toString();
        String note = editNote.getText().toString();
        String durata = editDurata.getText().toString();

        if (categoria.equals("")) {
            Toast.makeText(getApplicationContext(), "Please insert a category for your event", Toast.LENGTH_SHORT).show();
            return;
        }
        if (descrizione.equals("")) {
            Toast.makeText(getApplicationContext(), "Please insert a description for your event", Toast.LENGTH_SHORT).show();
            return;
        }
        if (editDate.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please insert a date for your event", Toast.LENGTH_SHORT).show();
            return;
        }
        if (editTime.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please insert a time for your event", Toast.LENGTH_SHORT).show();
            return;
        }

        //creo un nuovo evento
        Log.i("**CREO EVENTO: ", dateDialog);
        Evento event = new Evento(descrizione, luogo, dateDialog, timeDialog, durata, note, categoria);

        //effettuo il post per aggiungere l'evento
        new PostEventTask(BASE_URI + "events").execute(event);
    }

    private int getMinutes(String toFormat) {
        if (toFormat.contains(":")) {
            String h = toFormat.substring(0, toFormat.indexOf(":"));
            String m = toFormat.substring((toFormat.indexOf(":") + 1), toFormat.length());
            return (Integer.parseInt(h) * 60 + Integer.parseInt(m));
        } else
            return Integer.parseInt(toFormat) * 60;

    }


    public void addCategory(View v) {
        new FragmentAddCategory().show(getFragmentManager(), "category");
    }



    @Override
    public void onDialogNegativeClick(DialogFragment x) {
        //non faccio nulla
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        dateDialog = dayOfMonth + "/" + (monthOfYear+1) + "/" + year;
        FragmentAddEvent.updateDate(dateDialog);

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        timeDialog = hourOfDay + ":" + minute;
        FragmentAddEvent.updateTime(timeDialog);
    }

    @Override
    public void onDialog2PositiveClick(DialogFragment x) throws ParseException {

    }

    @Override
    public void onDialog2NegativeClick(DialogFragment x) {
        //niente
    }


    /* GET CATEGORY */
    public class GetCategoriesTask extends AsyncTask<String, Void, String[]> {
        String res;
        String uri;

        public GetCategoriesTask(String uri) {
            super();
            this.uri = uri;
        }

        @Override
        protected String[] doInBackground(String... params) {
            ClientResource clientResource = new ClientResource(uri);
            Gson gson = new Gson();
            String[] c = null;
            try {
                clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, username, password);
                res = clientResource.get().getText();
            } catch (ResourceException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            switch (clientResource.getStatus().getCode()) {
                case ErrorCodes.UTENTE_INESISTENTE_EXC_NUMBER:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), ErrorCodes.UTENTE_INESISTENTE_EXC_NUMBER, Toast.LENGTH_SHORT).show();
                        }
                    });
                    deleteCredentials();
                    break;

                case ErrorCodes.EVENTI_INESISTENTI_EXC_NUMBER:
                    c = new String[0];
                    break;
                default:
                    Evento[] eventi = gson.fromJson(res, Evento[].class);
                    List<String> categorie = new ArrayList<String>();
                    for(Evento e : eventi){
                        if(!categorie.contains(e.getCategoria())){
                            categorie.add(e.getCategoria());
                        }
                    }
                    c = new String[categorie.size()];
                    c = categorie.toArray(c);
            }
            return c;
        }

        @Override
        protected void onPostExecute(String[] category) {
            updateCategories(category);
        }
    }


    /* GET  EVENT BY CATEGORY */
    public class GetEventTask extends AsyncTask<Void, Void, Evento[]> {
        String res;
        String uri;
        String categoria;

        public GetEventTask(String uri, String categoria) {
            this.uri = uri;
            this.categoria = categoria;
        }

        @Override
        protected Evento[] doInBackground(Void... params) {
            ClientResource clientResource = new ClientResource(uri);
            Gson gson = new Gson();
            Evento[] events;

            try {
                clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, username, password);
                res = clientResource.get().getText();
            } catch (ResourceException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            events = gson.fromJson(res, Evento[].class);
            List<Evento> eventi = new ArrayList<Evento>();
            for(Evento e : events){
                if(e.getCategoria().equalsIgnoreCase(categoria)){
                    eventi.add(e);
                }
            }
            events = new Evento[eventi.size()];
            events = eventi.toArray(events);

            return events;
        }

        @Override
        protected void onPostExecute(Evento[] events) {
            super.onPostExecute(events);
            currentEvents.clear();
            currentEvents.addAll(Arrays.asList(events));
            EventFragment.updateView();
        }
    }


    /* POST  EVENT */
    public class PostEventTask extends AsyncTask<Evento, Void, Void> {
        private String uri;

        public PostEventTask(String uri) {
            this.uri = uri;
        }

        @Override
        protected Void doInBackground(Evento... params) {
            ClientResource cr = new ClientResource(uri);
            Gson gson = new Gson();

            cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, username, password);
            cr.post(gson.toJson(params[0], Evento.class));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new GetCategoriesTask(BASE_URI + "events").execute();
        }
    }


    public void datePic(View v) {
        new DatePickerDialogFragment().show(getFragmentManager(), "date");
    }

    public void timePic(View v) {
        new TimePickerDialogFragment().show(getFragmentManager(), "time");
    }

    public static String getUsername(){
        return username;
    }

    public static String getPassword(){
        return password;
    }



}






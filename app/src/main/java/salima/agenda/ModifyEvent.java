package salima.agenda;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.gson.Gson;

import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ModifyEvent extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private final String BASE_URI = "http://10.0.2.2:8182/";
    private String username;
    private String password;
    private String category;
    private Evento event;
    private  EditText editDate, editTime;
    private boolean toggleExec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_event);


        final EditText editDescrizione = (EditText) findViewById(R.id.textDescrizione);
        final EditText editLuogo = (EditText) findViewById(R.id.textLuogo);
        final EditText editNote = (EditText)findViewById(R.id.textNote);
        final EditText editDurata = (EditText) findViewById(R.id.textDurata);
        editDate = (EditText) findViewById(R.id.textDate);
        editTime = (EditText) findViewById(R.id.textTime);

        Button delButton = (Button) findViewById(R.id.deleteButton);


        ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setDisplayHomeAsUpEnabled(true);

        event=MainActivity.sendEvent();

        //implementare il delete con un altro bottone


        editDescrizione.setText(event.getDescrizione());
        editLuogo.setText(event.getLuogo());
        editNote.setText(event.getNoteAggiuntive());
        editDurata.setText(event.getTempoStimato());
        editDate.setText(event.getData());
        editTime.setText(event.getOra());

        Intent i= getIntent();
        password =i.getStringExtra(Login.PASSWORD);
        username= i.getStringExtra(Login.USERNAME);
        category= i.getStringExtra(MainActivity.CATEGORY);


        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleExec = true;
                event.setDescrizione(editDescrizione.getText().toString());
                event.setLuogo(editLuogo.getText().toString());
                event.setNoteAggiuntive(editNote.getText().toString());
                event.setTempStimato(editDurata.getText().toString());
                event.setData(editDate.getText().toString());

                new DeleteEventTask(BASE_URI + "events/"+event.getId()).execute();
            }
        });


    }

    private int getMinutes(String toFormat) {
        if(toFormat.contains(":")) {
            String h = toFormat.substring(0, toFormat.indexOf(":"));
            String m = toFormat.substring((toFormat.indexOf(":")+1),toFormat.length());
            return (Integer.parseInt(h)*60 + Integer.parseInt(m));
        }
        else
            return Integer.parseInt(toFormat)*60;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return false;
    }

    private String conversionMinute(int length){
        length -=60;
        int i;
        for (i =0;length >0; i++)
            length -=60;
        if((length+60)<10)
            return i+":0"+ (length+60);
        else return i+":"+ (length+60);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        editDate.setText( dayOfMonth+"/"+monthOfYear+"/"+year);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        editTime.setText(hourOfDay+":"+minute);
    }


    /* POST  EVENT */
//    public class PostEventTask extends AsyncTask<Evento, Void,Void> {
//        private String uri;
//
//        public PostEventTask(String uri){
//            this.uri= uri;
//        }
//
//        @Override
//        protected Void doInBackground(Evento... params) {
//            ClientResource cr = new ClientResource(uri);
//            Gson gson = new Gson();
//
//            cr.post(gson.toJson(params[0], Evento.class));
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//           /* Toast.makeText(getApplicationContext(), "Evento saved", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            intent.putExtra(Login.NICK, username);
//            intent.putExtra(Login.CODE, password);
//            startActivity(intent);*/
//        }
//    }

    public class DeleteEventTask extends  AsyncTask<Void,Void,Void>{
        private String uri;

        public DeleteEventTask(String uri){
            this.uri=uri;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ClientResource cr = new ClientResource(uri);
            cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, username, password);

            cr.delete();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(Login.USERNAME, username);
            intent.putExtra(Login.PASSWORD, password);
            startActivity(intent);

        }
    }

    public void datePic(View v){
        new DatePickerDialogFragment().show(getFragmentManager(), "editDate");
    }

    public void timePic(View v){
        new TimePickerDialogFragment().show(getFragmentManager(),"editTime");
    }




}

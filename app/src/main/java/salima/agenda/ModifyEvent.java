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
import android.widget.Toast;

import salima.agenda.R;
import com.google.gson.Gson;

import org.restlet.resource.ClientResource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ModifyEvent extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private final String BASE_URI = "http://10.0.2.2:8182/agenda/";
    private String username;
    private String code;
    private String category;
    private Evento event;
    private  EditText date, time;
    private boolean toggleExec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       /* super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_event);

        final EditText name = (EditText) findViewById(R.id.textName);
        final EditText place = (EditText) findViewById(R.id.textPlace);
        final EditText note = (EditText)findViewById(R.id.textNote);
        final EditText length = (EditText) findViewById(R.id.textLength);
        date = (EditText) findViewById(R.id.textDate);
        time = (EditText) findViewById(R.id.textTime);
        Button button = (Button) findViewById(R.id.saveButton) ;
        Button delButton = (Button) findViewById(R.id.deleteButton);


        ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setDisplayHomeAsUpEnabled(true);

         event=MainActivity.sendEvent();

        //implementare il delete con un altro bottone


        name.setText(event.getName());
        place.setText(event.getPlace());
        note.setText(event.getNote());
        length.setText(conversionMinute(event.getLength()));
        Date date_time= event.getDate();
        String data= new SimpleDateFormat("dd/MM/yyyy").format(date_time);
        date.setText(data);
        String tempo = new SimpleDateFormat("HH:mm").format(date_time);
        time.setText(tempo);

        Intent i= getIntent();
        code =i.getStringExtra(MainActivity.CODE);
        username= i.getStringExtra(MainActivity.NICK);
        category= i.getStringExtra(MainActivity.CATEGORY);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleExec = true;
                event.setName(name.getText().toString());
                event.setPlace(place.getText().toString());
                event.setNote(note.getText().toString());
                event.setLength(getMinutes(length.getText().toString()));

                String data_time= date.getText()+" "+ time.getText();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                try {
                    event.setDate(sdf.parse(data_time.toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                new DeleteEventTask(BASE_URI + "handleevents/"+ code +"/" +username+"/"+category+"/"+ event.getId()).execute();
            }
        });


        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleExec=false;
                new DeleteEventTask(BASE_URI + "handleevents/"+ code +"/" +username+"/"+category+"/"+ event.getId()).execute();            }
        });
*/

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
        date.setText( dayOfMonth+"/"+monthOfYear+"/"+year);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
         time.setText(hourOfDay+":"+minute);
    }


    /* POST  EVENT */
    public class PostEventTask extends AsyncTask<Evento, Void,Void> {
        private String uri;

        public PostEventTask(String uri){
            this.uri= uri;
        }

        @Override
        protected Void doInBackground(Evento... params) {
            ClientResource cr = new ClientResource(uri);
            Gson gson = new Gson();

            cr.post(gson.toJson(params[0], Evento.class));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
           /* Toast.makeText(getApplicationContext(), "Evento saved", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(Login.NICK, username);
            intent.putExtra(Login.CODE, code);
            startActivity(intent);*/
        }
    }

    public class DeleteEventTask extends  AsyncTask<Void,Void,Void>{
        private String uri;

        public DeleteEventTask(String uri){
            this.uri=uri;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ClientResource cr = new ClientResource(uri);

            cr.delete();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
           /* if(toggleExec)
                new PostEventTask(BASE_URI + "handleevents/"+ code +"/" +username+"/"+category+"/"+ event.getId()).execute(event);
            else {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(Login.NICK, username);
                intent.putExtra(Login.CODE, code);
                startActivity(intent);
            }*/
        }
    }

    public void datePic(View v){
        new DatePickerDialogFragment().show(getFragmentManager(), "date");
    }

    public void timePic(View v){
        new TimePickerDialogFragment().show(getFragmentManager(),"time");
    }




}

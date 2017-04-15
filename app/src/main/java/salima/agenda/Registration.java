package salima.agenda;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import salima.agenda.R;
import com.google.gson.Gson;

import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.IOException;

public class Registration extends AppCompatActivity {
    private final String BASE_URI = "http://10.0.2.2:8182/users";
    private String nickname;
    private String password;
    private String nome;
    private String cognome;
    private Button register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        register = (Button) findViewById(R.id.buttonRegister);
        final EditText editUsername = (EditText) findViewById(R.id.editUsername);
        final EditText editPassword = (EditText) findViewById(R.id.editPassword);
        final EditText editNome = (EditText) findViewById(R.id.editNome);
        final EditText editCognome = (EditText) findViewById(R.id.editCognome);

        editUsername.setHint("Username");
        editPassword.setHint("Password");
        editNome.setHint("Nome");
        editCognome.setHint("Cognome");

        editUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUsername.setText("");
                editPassword.setText("");
                editNome.setText("");
                editCognome.setText("");
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickname = editUsername.getText().toString();
                password = editPassword.getText().toString();
                nome = editNome.getText().toString();
                cognome = editCognome.getText().toString();

                new PostRestTask(BASE_URI).execute(nickname, nome, cognome, password);


            }
        });

//Freccia per tornare indietro
        ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        return false;
    }


    /* POST */
    public class PostRestTask extends AsyncTask<String, Void, String> {

        String res;
        String uri;

        public PostRestTask(String uri) {
            super();
            this.uri = uri;
        }


        @Override
        protected String doInBackground(String... params) {

            final ClientResource clientResource = new ClientResource(uri);
            Gson gson = new Gson();
            String code = null;

            String username = params[0];
            String nome = params[1];
            String cognome = params[2];
            String password = params[3];

            Utente u = new Utente(username, nome, cognome, password);

            try {

                res = clientResource.post(gson.toJson(u, Utente.class)).getText();

            } catch (ResourceException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(clientResource.getStatus().getCode() == 200){

                return "Registrazione completata";
            }
            else{
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), String.valueOf(clientResource.getStatus().getCode()), Toast.LENGTH_SHORT).show();

                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(final String str) {
            if(str != null) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();

                    }
                });

                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }

        }
    }

}




package salima.agenda;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import salima.agenda.R;
import com.google.gson.Gson;

import org.restlet.data.ChallengeScheme;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import java.io.IOException;


public class Login extends AppCompatActivity {
    private final String BASE_URI = "http://10.0.2.2:8182/users";
    private String nickname;
    private String password;
    private Button register;
    private CheckBox checkBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSharedPreferences(SH,MODE_PRIVATE).getString("username",null) == null) {

            setContentView(R.layout.activity_login);

            register = (Button) findViewById(R.id.register);
            final EditText nick = (EditText) findViewById(R.id.editLog);
            final EditText passw = (EditText) findViewById(R.id.editPassword);
            final TextView textView = (TextView) findViewById(R.id.textView_Register);
            checkBox = (CheckBox) findViewById(R.id.checkBox);


            nick.setHint("Username");
            passw.setHint("Password");
            textView.setLinkTextColor(Color.BLUE);

            nick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nick.setText("");
                    passw.setText("");
                }
            });

            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nickname = nick.getText().toString();
                    password = passw.getText().toString();
                    if (checkBox.isChecked())
                        save(nickname, password);
                    else
                        save(null, null);
                    new GETRestTask(BASE_URI).execute(nickname, password);


                }
            });


            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Registration.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });


        }
        else {
            SharedPreferences s = getSharedPreferences(SH, MODE_PRIVATE);

            new GETRestTask(BASE_URI).execute(s.getString("username",null), s.getString("password",null));
        }

    }

    private void save(String nickname, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences(SH, MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("username",nickname);
        editor.putString("password", password);
        editor.commit();


    }


    /* GET */
    public class GETRestTask extends AsyncTask<String, Void, String > {

        String res = null;
        String uri;
        String username;

        public GETRestTask(String uri){
            super();
            this.uri=uri;
        }



        @Override
        protected String doInBackground(String... params) {

            username = params[0];
            password = params[1];

            final ClientResource clientResource = new ClientResource(uri+"/"+username);
            Gson gson = new Gson();
            String code=null;



            try {

                clientResource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, username, password);

                res= clientResource.get().getText();

            } catch (ResourceException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(clientResource.getStatus().getCode() == 200){
                Utente u = gson.fromJson(res,Utente.class);
                return u.getUsername();
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
        protected void onPostExecute(String username) {
            if(username != null){
                intentMain(username, password);
            }

        }
    }

    private void intentMain(String username, String password) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(PASSWORD , password);
        intent.putExtra(USERNAME, username);
        startActivity(intent);
    }


    public static final String PASSWORD="password";
    public static final String USERNAME="username";
    public static final String SH="sh";
}

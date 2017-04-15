package salima.agenda;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import salima.agenda.R;

import java.text.ParseException;

public class FragmentAddEvent extends DialogFragment {
    @Override
    public void onAttach(Activity activity) {
        listner = (NoticeDialogListner) activity;
        super.onAttach(activity);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v= getActivity().getLayoutInflater().inflate(R.layout.dialog_items, null);
        dateET = (EditText) v.findViewById(R.id.textDate);
        timeET = (EditText) v.findViewById(R.id.textTime);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    listner.onDialogPositiveClick(FragmentAddEvent.this);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listner.onDialogNegativeClick(FragmentAddEvent.this);
                    }
                })
                .setTitle("Evento")
                .setMessage("Create your own event")
                .setIcon(R.drawable.ico)
                .setView(v);
        return builder.create();
    }


    public interface NoticeDialogListner{
        void onDialogPositiveClick(DialogFragment x) throws ParseException;
        void onDialogNegativeClick(DialogFragment x);
    }

    public static void updateDate(String newDate) {
        dateET.setText(newDate);
    }

    public static void updateTime(String newTime){
        timeET.setText(newTime);
    }

    private static EditText dateET, timeET;
    private NoticeDialogListner listner;
}

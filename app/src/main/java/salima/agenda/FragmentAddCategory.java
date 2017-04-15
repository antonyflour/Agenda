package salima.agenda;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import salima.agenda.R;

import java.text.ParseException;


public class FragmentAddCategory extends DialogFragment {
    @Override
    public void onAttach(Activity activity) {
        listner = (NoticeDialogListner2) activity;
        super.onAttach(activity);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v= getActivity().getLayoutInflater().inflate(R.layout.dialog2_items, null);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    listner.onDialog2PositiveClick(FragmentAddCategory.this);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listner.onDialog2NegativeClick(FragmentAddCategory.this);
                    }
                })
                .setTitle("Category")
                .setMessage("Create your own category")
                .setIcon(R.drawable.ico)
                .setView(v);
        return builder.create();
    }


    public interface NoticeDialogListner2{
        void onDialog2PositiveClick(DialogFragment x) throws ParseException;
        void onDialog2NegativeClick(DialogFragment x);
    }

    private NoticeDialogListner2 listner;
}

package salima.agenda;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import salima.agenda.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class EventFragment extends ListFragment {
    private ListSelectionListener mListener = null;
    private static final String TAG = "TitlesFragment";
    private static CustomAdpter customAdpter;


    public interface ListSelectionListener {
        void onListSelection(int index);
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {

        // Indicates the selected item has been checked
        getListView().setItemChecked(pos, true);

        // Inform the QuoteViewerActivity that the item in position pos has been selected
        mListener.onListSelection(pos);
    }

    @Override
    public void onAttach(Activity activity) {
        Log.i(TAG, getClass().getSimpleName() + ":entered onAttach()");
        super.onAttach(activity);

        try {
            mListener = (ListSelectionListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, getClass().getSimpleName() + ":entered onCreate()");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, getClass().getSimpleName() + ":entered onCreate()");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        Log.i(TAG, getClass().getSimpleName() + ":entered onActivityCreated()");
        super.onActivityCreated(savedState);

        // Set the list choice mode to allow only one selection at a time
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        customAdpter= new CustomAdpter(getActivity(), R.layout.event_item, MainActivity.currentEvents);

        setListAdapter(customAdpter);

    }

    public static void updateView(){
        customAdpter.notifyDataSetChanged();
    }



        public class CustomAdpter extends ArrayAdapter<Evento>{
            public CustomAdpter(Context context, int resource, List<Evento> objects) {
                super(context, resource, objects);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView =inflater.inflate(R.layout.event_item,null);
                TextView textView1 = (TextView) convertView.findViewById(R.id.textview1);
                TextView textView2 = (TextView) convertView.findViewById(R.id.textview2);
                textView1.setText(getItem(position).getDescrizione());
                textView2.setText(getItem(position).getData());
                return convertView;
            }




        }

}

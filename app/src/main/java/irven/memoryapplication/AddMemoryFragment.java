package irven.memoryapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.plus.PlusOneButton;

/**
 * A fragment
 * Activities that contain this fragment must implement the
 * {@link AddMemoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class AddMemoryFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    public AddMemoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_memory, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        if(view != null) {
            Button add = view.findViewById(R.id.completed_add_memory_button);
            add.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onAddButtonPressed();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onAddButtonPressed() {
        if (mListener != null) {
            EditText mnemonic = getView().findViewById(R.id.mnemonic);
            EditText content = getView().findViewById(R.id.content);
            mListener.onAddMemory(mnemonic.getText().toString(), content.getText().toString());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onAddMemory(String mnemonic, String content);
    }

}

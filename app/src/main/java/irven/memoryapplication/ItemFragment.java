package irven.memoryapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    public static ItemFragment newInstance(int columnCount) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    public void updateFragment(int itemId) {
        List<Memory> memories = new ArrayList<>();
        MyDBHandler memoryDB = MyDBHandler.getInstance();
        switch (itemId) {
            case R.id.navigation_repeat:
                memories = memoryDB.loadMemoriesToRepeat();
                break;
            case R.id.navigation_learning:
                memories = memoryDB.loadLearningMemories();
                break;
            case R.id.navigation_learned:
                memories = memoryDB.loadLearnedMemories();
                break;
            default:
                memories = memoryDB.loadAllMemories();
        }
        mRecyclerView.setAdapter(new MyItemRecyclerViewAdapter(memories, mListener));
        MyItemRecyclerViewAdapter adapter = (MyItemRecyclerViewAdapter) mRecyclerView.getAdapter();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.memory_item_list, container, false);

        Bundle bundle = getArguments();
        // default
        int itemId = R.id.navigation_learning;
        if (bundle != null && bundle.containsKey("Item")) {
            itemId = bundle.getInt("Item");
        }
        List<Memory> memories = new ArrayList<>();
        MyDBHandler memoryDB = MyDBHandler.getInstance();
        switch (itemId) {
            case R.id.navigation_repeat:
                memories = memoryDB.loadMemoriesToRepeat();
                break;
            case R.id.navigation_learning:
                memories = memoryDB.loadLearningMemories();
                break;
            case R.id.navigation_learned:
                memories = memoryDB.loadLearnedMemories();
                break;
            default:
                memories = memoryDB.loadAllMemories();
        }

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mRecyclerView.setAdapter(new MyItemRecyclerViewAdapter(memories, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Memory item);
    }
}

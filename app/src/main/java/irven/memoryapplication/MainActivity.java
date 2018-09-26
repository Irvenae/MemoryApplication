package irven.memoryapplication;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;

import irven.memoryapplication.dummy.DummyContent;

public class MainActivity  extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener, AddMemoryFragment.OnFragmentInteractionListener
        {
    private BottomNavigationView mNavigationBottom;
    private MyDBHandler memoryDB;
    private Handler mHandler;
    private HandlerThread mHandlerThread = null;

    private String tagItemFragment = "ITEM";
    private String tagAddItemFragment = "ADDITEM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        memoryDB = new MyDBHandler(getBaseContext());

        InitBottomViewAndLoadFragment();

        FloatingActionButton addButton = findViewById(R.id.start_add_memory_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMemoryFragment addFragment = new AddMemoryFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, addFragment, tagAddItemFragment).addToBackStack(null).commit();
                mNavigationBottom.getMenu().setGroupCheckable(0, false, true);
            }
        });
        //startHandlerThread();
        //mHandler.postDelayed(periodicUpdate, 3600*1000);
    }

    private void InitBottomViewAndLoadFragment() {
        mNavigationBottom = findViewById(R.id.navigation);
        mNavigationBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (f.getTag().equals(tagItemFragment)) {
                    ItemFragment itemFragment = (ItemFragment) f;
                    itemFragment.updateFragment(item.getItemId());
                } else {
                    ItemFragment itemFragment = new ItemFragment();
                    Bundle bundl = new Bundle();
                    bundl.putInt("Item", item.getItemId());
                    itemFragment.setArguments(bundl);
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, itemFragment, tagItemFragment).addToBackStack(null).commit();
                    mNavigationBottom.getMenu().setGroupCheckable(0, true, true);
                }


                switch (item.getItemId()) {
                    case R.id.navigation_new:
                        setToolbar(getString(R.string.title_home), false);
                        return true;
                    case R.id.navigation_learning:
                        setToolbar(getString(R.string.title_dashboard), false);
                        return true;
                    case R.id.navigation_learned:
                        setToolbar(getString(R.string.title_notifications), false);
                        return true;
                }
                return false;
            }
        });

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setToolbar(getString(R.string.title_home), false);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        ItemFragment fragment_home = new ItemFragment();
        fragmentTransaction.add(R.id.fragment_container, fragment_home, tagItemFragment).addToBackStack(null).commit();
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        if (navigation.getMenu().getItem(0).isChecked() ) {
            Toast toast = Toast.makeText(getBaseContext(), item.content, Toast.LENGTH_SHORT);
            toast.show();
            Vector<Memory> memories = memoryDB.loadAllMemories();
            for (int ind = 0; ind < memories.size(); ++ind) {
                Log.e("TEST", memories.get(ind).toString());
            }
        }
    }

    @Override
    public void onBackPressed() {
        final Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (f.getTag().equals(tagItemFragment)) {
            BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
            MenuItem item = bottomNavigationView.getMenu().findItem(bottomNavigationView.getSelectedItemId());
            if (item.getItemId() != R.id.navigation_new) {
                ItemFragment itemFragment = (ItemFragment) f;
                itemFragment.updateFragment(R.id.navigation_new);
                setToolbar(getString(R.string.title_home), false);
                mNavigationBottom.setSelectedItemId(R.id.navigation_new);
            }
        }
        if (f.getTag().equals(tagAddItemFragment)) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            ItemFragment fragment_home = new ItemFragment();
            fragmentTransaction.replace(R.id.fragment_container, fragment_home, tagItemFragment).addToBackStack(null).commit();
            mNavigationBottom.getMenu().setGroupCheckable(0, true, true);
        }
    }

    private Runnable periodicUpdate = new Runnable () {
        public void run() {
            // scheduled another events to be in 10 seconds later
            mHandler.postDelayed(periodicUpdate, 3600*1000);
            // check if we need to send notification
        }
    };

    public void startHandlerThread(){
        mHandlerThread = new HandlerThread("HandlerThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    public void setToolbar(String title, boolean back_navigation) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(back_navigation);

        TextView titleTextview = (TextView) findViewById(R.id.toolbar_title);
        titleTextview.setText(title);
    }

    @Override
    public void onAddMemory(String mnemonic, String content) {
        // add to database go to main screen
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        ItemFragment fragment_home = new ItemFragment();
        fragmentTransaction.replace(R.id.fragment_container, fragment_home, tagItemFragment).addToBackStack(null).commit();
        mNavigationBottom.getMenu().setGroupCheckable(0, true, true);

        Memory memory = new Memory(mnemonic, content);
        memory = memoryDB.addMemory(memory);
        Log.e("TEST", memory.toString());
    }
        }

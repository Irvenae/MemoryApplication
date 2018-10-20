package irven.memoryapplication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity  extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener, AddMemoryFragment.OnFragmentInteractionListener {
    private BottomNavigationView mNavigationBottom;
    private Handler mHandler;
    private HandlerThread mHandlerThread = null;
    private NotificationHandler mNotificationHandler;

    private String tagItemFragment = "ITEM";
    private String tagAddItemFragment = "ADDITEM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize db
        MyDBHandler memoryDB = new MyDBHandler(getBaseContext());

        InitBottomViewAndLoadFragment();

        mNotificationHandler = new NotificationHandler(getBaseContext());

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
                handleViewChange(item.getItemId(), false);
                if (item.getItemId() == R.id.navigation_repeat) {
                    mNotificationHandler.Remove(mNavigationBottom);
                }
                return true;
            }
        });

//        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
//        setSupportActionBar(mToolbar);
//        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        setToolbar(getString(R.string.title_home), false);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        ItemFragment itemFragment = new ItemFragment();
        Bundle bundl = new Bundle();
        bundl.putInt("Item", R.id.navigation_learning);
        itemFragment.setArguments(bundl);
        fragmentTransaction.add(R.id.fragment_container, itemFragment, tagItemFragment).addToBackStack(null).commit();
        mNavigationBottom.getMenu().getItem(1).setChecked(true);
    }

    @Override
    public void onListFragmentInteraction(Memory memory) {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        onClickMemory(memory, navigation.getSelectedItemId());
    }

    @Override
    public void onBackPressed() {
        handleViewChange(R.id.navigation_learning, true);
    }

    @Override
    public void onResume() {
        // check notifications
        mNotificationHandler.update(mNavigationBottom);
        super.onResume();
    }

    private void handleViewChange(int newItem, boolean back_navigation) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (f.getTag().equals(tagItemFragment)) {
            ItemFragment itemFragment = (ItemFragment) f;
            itemFragment.updateFragment(newItem);
            switch (newItem) {
                case R.id.navigation_repeat:
                    setToolbar(getString(R.string.title_repeat), false);
                    break;
                case R.id.navigation_learning:
                    setToolbar(getString(R.string.title_learning), false);
                    break;
                case R.id.navigation_learned:
                    setToolbar(getString(R.string.title_learned), false);
                    break;
            }
            if (back_navigation) {
                mNavigationBottom.setSelectedItemId(newItem);
            }
        } else {
            ItemFragment itemFragment = new ItemFragment();
            Bundle bundl = new Bundle();
            bundl.putInt("Item", newItem);
            itemFragment.setArguments(bundl);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, itemFragment, tagItemFragment).addToBackStack(null).commit();
            mNavigationBottom.getMenu().setGroupCheckable(0, true, true);
            int checkable_item = 0;
            switch (newItem) {
                case R.id.navigation_repeat:
                    checkable_item = 0;
                    break;
                case R.id.navigation_learning:
                    checkable_item = 1;
                    break;
                case R.id.navigation_learned:
                    checkable_item = 2;
                    break;
            }
            mNavigationBottom.getMenu().getItem(checkable_item).setChecked(true);
            setToolbar(getString(newItem), false);
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
//        getSupportActionBar().setDisplayHomeAsUpEnabled(back_navigation);
//
//        TextView titleTextview = (TextView) findViewById(R.id.toolbar_title);
//        titleTextview.setText(title);
    }

    @Override
    public void onAddMemory(String mnemonic, String content) {
        // add to database go to main screen
        handleViewChange(R.id.navigation_learning, false);

        Memory memory = new Memory(mnemonic, content);
        MyDBHandler.getInstance().addMemory(memory);
    }

    public void onClickMemory(final Memory memory, int itemId) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.myDialog));
        LayoutInflater inflater = this.getLayoutInflater();

        if (itemId == R.id.navigation_repeat) {
            alertDialog.setView(inflater.inflate(R.layout.dialog_click_memory, null))
                    // Add action buttons
                    .setPositiveButton(R.string.dialog_remembered, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // increment to remember
                            memory.onRememberedWell();
                            MyDBHandler.getInstance().updateMemory(memory);
                        }
                    })
                    .setNegativeButton(R.string.dialog_forgot, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // same repeat
                            memory.onForgot();
                            MyDBHandler.getInstance().updateMemory(memory);
                        }
                    });
        } else {
            alertDialog.setView(inflater.inflate(R.layout.dialog_click_memory, null));
        }

        final Dialog dialog = alertDialog.create();
        dialog.show();

        ImageView close = (ImageView) dialog.findViewById(R.id.dialog_close);
        // if button is clicked, close the custom dialog
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView mnemonic = (TextView) dialog.findViewById(R.id.dialog_mnemonic);
        mnemonic.setText(memory.mnemonic);
        TextView content = (TextView) dialog.findViewById(R.id.dialog_content);
        content.setText(memory.content);
    }



    }

package app.compsci702g6.Activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tmall.ultraviewpager.UltraViewPager;
import com.tmall.ultraviewpager.transformer.UltraDepthScaleTransformer;

import app.compsci702g6.R;
import app.compsci702g6.Services.CalculateService;
import app.compsci702g6.Utilities.Encryptor;

public class UIActivity extends AppCompatActivity {

    private Context mContext = this;
    private Handler repeatUpdateHandler = new Handler();
    private boolean mAutoIncrement = false;
    private boolean mAutoDecrement = false;
    private boolean mBounded;
    private boolean mKeyPressed;
    private final static int[] mSportsImages = {R.drawable.pedestrian_walking, R.drawable.running, R.drawable.runer_silhouette_running_fast, R.drawable.swimming_figure, R.drawable.tennis_raquet_and_ball,
            R.drawable.basketball_ball_with_line, R.drawable.dumbbell};

    private int buttonIndex;
    private int mCaloriesBurned;
    private int mWeight;
    private int mMins;
    private int mSelectedSport;
    CalculateService mServer;
    View mSportView;
    View mApiView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_exercise:
                    mSportView.setVisibility(View.VISIBLE);
                    mApiView.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_food:
                    mSportView.setVisibility(View.GONE);
                    mApiView.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }

    };
    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
        updateUI();
        //lock = false;
    }
    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(UIActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui);
        mApiView = findViewById(R.id.apiRequest);
        mSportView =findViewById(R.id.sportView);
        setupUI(mSportView);
        mCaloriesBurned = 0;
        mMins = 0;
        mWeight = 70;
        mKeyPressed =false;

        mApiView.setVisibility(View.GONE);
        getSupportActionBar().setTitle("Calories Calculator");
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        final UltraViewPager ultraViewPager = (UltraViewPager) findViewById(R.id.ultra_viewpager);
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
        ultraViewPager.setPageTransformer(false, new UltraDepthScaleTransformer());
//initialize UltraPagerAdapter，and add child view to UltraViewPager
        PagerAdapter adapter = new UltraPagerAdapter();
        ultraViewPager.setAdapter(adapter);
        ultraViewPager.setMultiScreen(0.32f);
        ultraViewPager.setItemRatio(0.8);

//set an infinite loop
        ultraViewPager.setInfiniteLoop(true);

        ultraViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position ) {
                mSelectedSport = ultraViewPager.getCurrentItem();
                mCaloriesBurned = (int)mServer.calculateCalories(mWeight,mMins,ultraViewPager.getCurrentItem());
                updateUI();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mSelectedSport = ultraViewPager.getCurrentItem();

        findViewById(R.id.searchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mServer.search(((EditText)findViewById(R.id.food_name)).getText().toString());
                final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.button_weight_decrease).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWeight > 0)
                    mWeight--;
                mCaloriesBurned = (int)mServer.calculateCalories(mWeight,mMins,ultraViewPager.getCurrentItem());
                updateUI();
            }
        });
        findViewById(R.id.button_weight_decrease).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mWeight > 0) {
                    mAutoDecrement = true;
                    buttonIndex = 0;
                    repeatUpdateHandler.post(new RptUpdater());
                }
                return false;
            }
        });
        findViewById(R.id.button_weight_decrease).setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL)
                        && mAutoDecrement ){
                    mAutoDecrement = false;
                    mCaloriesBurned = (int)mServer.calculateCalories(mWeight,mMins,ultraViewPager.getCurrentItem());
                }
                return false;
            }
        });



        findViewById(R.id.button_weight_increase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWeight++;
                mCaloriesBurned = (int)mServer.calculateCalories(mWeight,mMins,ultraViewPager.getCurrentItem());
                updateUI();
            }
        });
        findViewById(R.id.button_weight_increase).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mAutoIncrement = true;
                buttonIndex =0;
                repeatUpdateHandler.post( new RptUpdater() );
                return false;
            }
        });
        findViewById(R.id.button_weight_increase).setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL)
                        && mAutoIncrement ){
                    mAutoIncrement = false;
                    mCaloriesBurned = (int)mServer.calculateCalories(mWeight,mMins,ultraViewPager.getCurrentItem());
                }
                return false;
            }
        });


        findViewById(R.id.button_time_decrease).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMins > 0)
                    mMins--;
                mCaloriesBurned = (int)mServer.calculateCalories(mWeight,mMins,ultraViewPager.getCurrentItem());
                updateUI();
            }
        });
        findViewById(R.id.button_time_decrease).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mMins > 0) {
                    mAutoDecrement = true;
                    buttonIndex = 1;
                    repeatUpdateHandler.post(new RptUpdater());
                }
                return false;
            }
        });
        findViewById(R.id.button_time_decrease).setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL)
                        && mAutoDecrement ){
                    mAutoDecrement = false;
                    mCaloriesBurned = (int)mServer.calculateCalories(mWeight,mMins,ultraViewPager.getCurrentItem());
                }
                return false;
            }
        });



        findViewById(R.id.button_time_increase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMins++;
                mCaloriesBurned = (int)mServer.calculateCalories(mWeight,mMins,ultraViewPager.getCurrentItem());
                updateUI();
            }
        });
        findViewById(R.id.button_time_increase).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mAutoIncrement = true;
                buttonIndex =1;
                repeatUpdateHandler.post( new RptUpdater() );
                return false;
            }
        });
        findViewById(R.id.button_time_increase).setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL)
                        && mAutoIncrement ){
                    mAutoIncrement = false;
                    mCaloriesBurned = (int)mServer.calculateCalories(mWeight,mMins,ultraViewPager.getCurrentItem());
                }
                return false;
            }
        });



        findViewById(R.id.button_calories_decrease).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCaloriesBurned > 0)
                    mCaloriesBurned--;
                mMins = (int)mServer.calculateTime(mWeight,mCaloriesBurned,ultraViewPager.getCurrentItem());
                updateUI();
            }
        });
        findViewById(R.id.button_calories_decrease).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mCaloriesBurned > 0) {
                    mAutoDecrement = true;
                    buttonIndex = 2;
                    repeatUpdateHandler.post(new RptUpdater());
                }
                return false;
            }
        });
        findViewById(R.id.button_calories_decrease).setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL)
                        && mAutoDecrement ){
                    mAutoDecrement = false;
                    mMins = (int)mServer.calculateTime(mWeight,mCaloriesBurned,ultraViewPager.getCurrentItem());
                }
                return false;
            }
        });

        findViewById(R.id.button_calories_increase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCaloriesBurned++;
                mMins = (int)mServer.calculateTime(mWeight,mCaloriesBurned,ultraViewPager.getCurrentItem());
                updateUI();
            }
        });
        findViewById(R.id.button_calories_increase).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mAutoIncrement = true;
                buttonIndex =2;
                repeatUpdateHandler.post( new RptUpdater() );
                return false;
            }
        });
        findViewById(R.id.button_calories_increase).setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL)
                        && mAutoIncrement ){
                    mAutoIncrement = false;
                    mMins = (int)mServer.calculateTime(mWeight,mCaloriesBurned,ultraViewPager.getCurrentItem());
                }
                return false;
            }
        });

        updateUI();
    }

    public void setTextChangedListener(){
        ((EditText)findViewById(R.id.editText_weight)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!lock) {
                    if (mKeyPressed) {
                        if (s.length() > 0)
                            if (s.length() < 10)
                                mWeight = Integer.parseInt(s.toString());
                            else
                                mWeight = Integer.MAX_VALUE;
                        else
                            mWeight = 0;
                        mCaloriesBurned = (int) mServer.calculateCalories(mWeight, mMins, mSelectedSport);
                        mKeyPressed = false;
                        ((TextView) findViewById(R.id.editText_calories)).setText(String.valueOf(mCaloriesBurned));
                    }
                    mKeyPressed = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ((EditText)findViewById(R.id.editText_time)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!lock) {
                    if (mKeyPressed) {
                        if (s.length() > 0)
                            if (s.length() < 10)
                                mMins = Integer.parseInt(s.toString());
                            else
                                mMins = Integer.MAX_VALUE;
                        else
                            mMins = 0;
                        mCaloriesBurned = (int) mServer.calculateCalories(mWeight, mMins, mSelectedSport);
                        mKeyPressed = false;
                        ((TextView) findViewById(R.id.editText_calories)).setText(String.valueOf(mCaloriesBurned));
                    }
                    mKeyPressed = true;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ((EditText)findViewById(R.id.editText_calories)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!lock) {
                    if (mKeyPressed) {
                        if (s.length() > 0)
                            if (s.length() < 10)
                                mCaloriesBurned = Integer.parseInt(s.toString());
                            else
                                mCaloriesBurned = Integer.MAX_VALUE;
                        else
                            mCaloriesBurned = 0;
                        mMins = (int) mServer.calculateTime(mWeight, mCaloriesBurned, mSelectedSport);
                        mKeyPressed = false;
                        ((TextView) findViewById(R.id.editText_time)).setText(String.valueOf(mMins));
                    }
                    mKeyPressed = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    private void updateUI() {
        findViewById(R.id.sportView).clearFocus();
        mKeyPressed =false;
        ((TextView) findViewById(R.id.editText_weight)).setText(String.valueOf(mWeight));
        mKeyPressed =false;
        ((TextView) findViewById(R.id.editText_time)).setText(String.valueOf(mMins));
        mKeyPressed =false;
        ((TextView) findViewById(R.id.editText_calories)).setText(String.valueOf(mCaloriesBurned));
        //lock = false;
        mKeyPressed =false;
    }

    private class UltraPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.sport_viewer, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.image_sport);
            imageView.setImageDrawable(getResources().getDrawable(mSportsImages[position]));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            TextView textView = (TextView) view.findViewById(R.id.text_size);
            String[] names = getResources().getStringArray(R.array.exercise_array);
            textView.setText(names[position]);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return mSportsImages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }
    }
    class RptUpdater implements Runnable {
        final long REP_DELAY = 50;
        public void run() {
            if( mAutoIncrement ){
                switch (buttonIndex){
                    case 0:
                        mWeight ++;
                        break;
                    case 1:
                        mMins ++;
                        break;
                    case 2:
                        mCaloriesBurned ++;
                        break;
                }
                updateUI();
                repeatUpdateHandler.postDelayed( new RptUpdater(), REP_DELAY );
            } else if( mAutoDecrement ){
                switch (buttonIndex){
                    case 0:
                        if(mWeight>0)
                        mWeight --;
                        break;
                    case 1:
                        if(mMins>0)
                            mMins --;
                        break;
                    case 2:
                        if(mCaloriesBurned>0)
                            mCaloriesBurned --;
                        break;
                }
                updateUI();
                repeatUpdateHandler.postDelayed( new RptUpdater(), REP_DELAY );
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent mIntent = new Intent(this, CalculateService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    };

    ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(UIActivity.this, "Service is disconnected", Toast.LENGTH_SHORT).show();
            mBounded = false;
            mServer = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(UIActivity.this, "Service is connected", Toast.LENGTH_SHORT).show();
            mBounded = true;
            CalculateService.LocalBinder mLocalBinder = (CalculateService.LocalBinder)service;
            mServer = mLocalBinder.getServerInstance();
            setTextChangedListener();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if(mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String message = intent.getStringExtra(Encryptor.decrypt(Encryptor.key, Encryptor.initVector, "JgRBEVH8W+Sb6NJy28uxLg=="));
            ((TextView) findViewById(R.id.api_response)).setText(message);
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
        }
    };

    boolean lock = false;
    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    public void onResume() {
        super.onResume();
        mKeyPressed =false;
        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("api_result"));
    }
    @Override
    protected void onPause() {
        lock = true;
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }
}

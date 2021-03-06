package com.example.android.roomwordssample;



import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Vibrator;


public class MainActivity extends AppCompatActivity {
    Vibrator v;
    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;

    private WordViewModel mWordViewModel;
    FloatingActionButton floatingActionButton;
    WordRoomDatabase db;
    WordDao wordDao;
    List<Word> wordList;
    int i = 0; String s0 = null;
    Button button;
   // shake detection code is start from here
   private static final int REQUEST_CALL = 1;
    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    // above code  is foe shake detection


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // permission for message
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);



        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        //above code is ShakeDetector initialization
        wordList = new ArrayList<Word>();
        button = (Button) findViewById(R.id.button);
        db = WordRoomDatabase.getDatabase(this);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final WordListAdapter adapter = new WordListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get a new or existing ViewModel from the ViewModelProvider.
        mWordViewModel = new ViewModelProvider(this).get(WordViewModel.class);

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        mWordViewModel.getAllWords().observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(@Nullable final List<Word> words) {
                // Update the cached copy of the words in the adapter.
                adapter.setWords(words);
                wordList.addAll(words);
//                s0  =  words.get(i).getWord();
//                String s1=  words.get(1).getWord();
//                String s3=  words.get(2).getWord();
//                Log.d("ajeet", "onChanged: " +s1);

            }
        });

        // just for testing
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(wordList.size()!= 0 ) {
                    if(wordList.size() <3){
                        Toast.makeText(MainActivity.this,
                                "Please Enter Altleat Three Contacts" , Toast.LENGTH_LONG).show();
                    }else {
                        Random randomnumbergenerator1 = new Random();
                        i = randomnumbergenerator1.nextInt(3);

                        s0 = wordList.get(i).getWord();
                        makePhoneCall(s0);
                        Log.d("ajeet", "onClick: " + s0);
                        sendSMSMessage(s0);
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(1000);
                    }
                }else {
                    Toast.makeText(MainActivity.this,
                            "Please Enter Three Contacts" , Toast.LENGTH_LONG).show();
                }
            }
        });


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if(wordList.size() > 3)
              {
                  Toast.makeText(MainActivity.this,
                          "Trying to add more then three contacts" , Toast.LENGTH_LONG).show();
              }else {
                  Intent intent = new Intent(MainActivity.this, NewWordActivity.class);
                  startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
              }
            }
        });
        // Delete Floating action button
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mWordViewModel.delete();
                wordList.clear();

            }
        });

         // Shake detection on shakelistener
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                /*
                 * The following method, "handleShakeEvent(count):" is a stub //
                 * method you would use to setup whatever you want done once the
                 * device has been shook.
                 */
                if(wordList.size()!= 0 ) {
                    if(wordList.size() <3){
                        Toast.makeText(MainActivity.this,
                                "Please Enter Altleat Three Contacts" , Toast.LENGTH_LONG).show();
                    }else {
                        Random randomnumbergenerator1 = new Random();
                        i = randomnumbergenerator1.nextInt(3);

                        s0 = wordList.get(i).getWord();
                        makePhoneCall(s0);
                        Log.d("ajeet", "onClick: " + s0);
                        sendSMSMessage(s0);
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(1000);
                    }
                }else {
                    Toast.makeText(MainActivity.this,
                            "Please Enter Three Contacts" , Toast.LENGTH_LONG).show();
                }
            }

        });


    }

       // functions and onruntime permission

        public void handleShakeEvent(int x)
        {
            Toast.makeText(MainActivity.this,
                    "count" + x , Toast.LENGTH_LONG).show();
        }
        @Override
        public void onResume() {
            super.onResume();
            // Add the following line to register the Session Manager Listener onResume
            mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
        }

        @Override
        public void onPause() {
            // Add the following line to unregister the Sensor Manager onPause
            mSensorManager.unregisterListener(mShakeDetector);
            super.onPause();
        }

        private void makePhoneCall(String number) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:"+number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }

        }
    protected void sendSMSMessage(String number) {

        SmsManager mySmsManager = SmsManager.getDefault();
        mySmsManager.sendTextMessage(number,null, "PLEASE HELP ME IM IN DANGER.", null, null);
        Toast.makeText(this, "message sent", Toast.LENGTH_SHORT).show();
    }
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == REQUEST_CALL) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makePhoneCall(s0);
                } else {
                    Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
                }
            }
        }



















    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_WORD_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Word word = new Word(data.getStringExtra(NewWordActivity.EXTRA_REPLY));
            mWordViewModel.insert(word);
            String s = data.getStringExtra(NewWordActivity.EXTRA_REPLY);




        } else {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show();
        }
    }
}

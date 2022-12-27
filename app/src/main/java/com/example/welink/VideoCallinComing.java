package com.example.welink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.BroadcastIntentHelper;
import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

public class VideoCallinComing extends AppCompatActivity {

    DatabaseReference referencecaller,referenceVc,vcref;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String sender_url,sender_prof,sender_name,sender_uid,receiver_uid;
    VcModel model;
    CircleImageView imageView;
    FloatingActionButton declinebtn,acceptbtn;
    TextView tvname,tvprof;
    MediaPlayer mp;

    //--1
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBroadcastReceived(intent);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_callin_coming);

        imageView = findViewById(R.id.iv_ic_vc);
        tvname = findViewById(R.id.name_vc_ic);
        tvprof = findViewById(R.id.prof_ic_vc);
        declinebtn = findViewById(R.id.decline_vc_ic);
        acceptbtn = findViewById(R.id.accept_vc_ic);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        receiver_uid = user.getUid();

        //---2
        URL serverURL;
        try {
            // object creation of JitsiMeetConferenceOptions
            // class by the name of options
//            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
//                    .setServerURL(new URL("https://meet.jit.si"))
//                    .setWelcomePageEnabled(false)
//                    .build();

            serverURL = new URL("https://meet.jit.si");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid server URL!");
        }

        JitsiMeetConferenceOptions defaultOptions
                = new JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                // When using JaaS, set the obtained JWT here
                //.setToken("MyJWT")
                // Different features flags can be set
                // .setFeatureFlag("toolbox.enabled", false)
                // .setFeatureFlag("filmstrip.enabled", false)
                .setFeatureFlag("welcomepage.enabled", false)
                .build();
        JitsiMeet.setDefaultConferenceOptions(defaultOptions);
        registerForBroadcastMessages();

        Bundle bundle = getIntent().getExtras();
        if (bundle!= null){
//            checkIncoming();
            sender_uid = bundle.getString("uid");
            Toast.makeText(this, "Data 11111111", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this, "Data missing", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent (VideoCallinComing.this, ChatActivity.class);
            startActivity(intent);
        }

        model = new VcModel();
        checkCallstatus();
//        checkIncoming();
        referencecaller = database.getReference("ALl Users").child(sender_uid);

        referencecaller.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    sender_name = snapshot.child("name").getValue().toString();
                    sender_url = snapshot.child("url").getValue().toString();
                    sender_prof = snapshot.child("prof").getValue().toString();

                    tvname.setText(sender_name);
                    Picasso.get().load(sender_url).into(imageView);
                    tvprof.setText(sender_prof);

                }else {
                    Toast.makeText(VideoCallinComing.this, "Cannot make call", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        vcref  = FirebaseDatabase.getInstance().getReference("vc");

        referenceVc = database.getReference("vcref").child(sender_uid).child(receiver_uid);

        try {

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            mp = MediaPlayer.create(getApplicationContext(),notification);
            mp.start();
        }catch (Exception e){
            Toast.makeText(this, "msg22222222222222 "+e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        acceptbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String response = "yes";
                sendResponse(response);
                vcref.removeValue();
                referenceVc.removeValue();
                mp.stop();
            }
        });

        declinebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String response = "no";
                sendResponse(response);
                Intent intent = new Intent(VideoCallinComing.this,ChatActivity.class);
                startActivity(intent);
                vcref.removeValue();
                referenceVc.removeValue();
                mp.stop();
                finish();
            }
        });
    }



    private void checkCallstatus() {

        DatabaseReference cancelRef;
        cancelRef = database.getInstance().getReference("cancel");


        cancelRef.child(sender_uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    String response = snapshot.child("response").getValue().toString();

                    if (response.equals("no")){

                        Intent intent = new Intent(VideoCallinComing.this,MainActivity.class);
                        startActivity(intent);
                        mp.stop();
                        finish();


                    }else{

                    }

                }else {
//                    Toast.makeText(VideoCallinComing.this, "kappa", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(VideoCallinComing.this, ChatActivity.class);
//                    startActivity(intent);
//                    mp.stop();
//                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }//checked

    private void sendResponse(String response) {

        if (response.equals("yes")){

            model.setKey(sender_name+receiver_uid);
            model.setResponse(response);
            referenceVc.child("res").setValue(model);
            joinmeeting();
            Toast.makeText(this, "Call granted", Toast.LENGTH_SHORT).show();
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Intent intent = new Intent(VideoCallinComing.this, ChatActivity.class);
////                        intent.putExtra("uid", senderuid);
////                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    finish();
////                    referenceVc.removeValue();
//                }
//            },3000);


        }else if (response.equals("no")){

            model.setKey(sender_name+receiver_uid);
            model.setResponse(response);
            referenceVc.child("res").setValue(model);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    referenceVc.removeValue();
                }
            },3000);

            finish();

        }


    }//checked

    private void joinmeeting() {


        try {

//            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
//                    .setRoom(sender_name+receiver_uid)
//                    .build();
//            JitsiMeetActivity.launch(VideoCallinComing.this,options);
//            finish();

            JitsiMeetConferenceOptions options
                    = new JitsiMeetConferenceOptions.Builder()
                    .setRoom(sender_name+receiver_uid)
                    // Settings for audio and video
                    //.setAudioMuted(true)
                    //.setVideoMuted(true)
                    .build();
            // Launch the new activity with the given options. The launch() method takes care
            // of creating the required Intent and passing the options.
            JitsiMeetActivity.launch(this, options);
            finish();

        }catch (Exception e){

            Toast.makeText(this, "msg33333333 "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }//checked

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        String response = "no";
        sendResponse(response);
        Intent intent = new Intent(VideoCallinComing.this,ChatActivity.class);
        startActivity(intent);
        mp.stop();
        vcref.removeValue();
        finish();

    }


    //extra
    public void checkIncoming() {

        vcref = FirebaseDatabase.getInstance().getReference("vc");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        try {

            vcref.child(currentuid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {


                    } else {
                        //                        senderuid = snapshot.child("calleruid").getValue().toString();
                        Intent intent = new Intent(VideoCallinComing.this, ChatActivity.class);
//                        intent.putExtra("uid", senderuid);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {

            //   Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }


    }


    //-3
    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

        super.onDestroy();
    }

    private void registerForBroadcastMessages() {
        IntentFilter intentFilter = new IntentFilter();

        /* This registers for every possible event sent from JitsiMeetSDK
           If only some of the events are needed, the for loop can be replaced
           with individual statements:
           ex:  intentFilter.addAction(BroadcastEvent.Type.AUDIO_MUTED_CHANGED.getAction());
                intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_TERMINATED.getAction());
                ... other events
         */
        for (BroadcastEvent.Type type : BroadcastEvent.Type.values()) {
            intentFilter.addAction(type.getAction());
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    // Example for handling different JitsiMeetSDK events
    private void onBroadcastReceived(Intent intent) {
        if (intent != null) {
            BroadcastEvent event = new BroadcastEvent(intent);

            switch (event.getType()) {
                case CONFERENCE_JOINED:
                    Timber.i("Conference Joined with url%s", event.getData().get("url"));
                    break;
                case PARTICIPANT_JOINED:
                    Timber.i("Participant joined%s", event.getData().get("name"));
                    break;
                case CONFERENCE_TERMINATED:
                    Toast.makeText(VideoCallinComing.this, "Call Ended", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(VideoCallinComing.this, MainActivity.class);
//                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                    mp.stop();
                    finishAffinity();
                    break;
                case PARTICIPANT_LEFT:
                    Toast.makeText(VideoCallinComing.this, "Call ended", Toast.LENGTH_SHORT).show();
                    Intent intent2 = new Intent(VideoCallinComing.this, MainActivity.class);
//                    intent2.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                    startActivity(intent2);
                    mp.stop();
                    finishAffinity();
                    break;
                default :
//                    Toast.makeText(VideoCallinComing.this, "kappa33", Toast.LENGTH_SHORT).show();
                    Intent intent3 = new Intent(VideoCallinComing.this, MainActivity.class);
//                    intent3.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                    startActivity(intent3);
                    mp.stop();
                    finishAffinity();

            }
        }
    }

//    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
    // Example for sending actions to JitsiMeetSDK
    private void hangUp() {
        Intent hangupBroadcastIntent = BroadcastIntentHelper.buildHangUpIntent();
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(hangupBroadcastIntent);
    }


}
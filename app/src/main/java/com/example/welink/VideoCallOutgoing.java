package com.example.welink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import java.net.MalformedURLException;
import java.net.URL;

import timber.log.Timber;

public class VideoCallOutgoing extends AppCompatActivity {

    ImageView imageView;
    TextView tvname,tvprof;
    FloatingActionButton declinebtn;
    String receiver_url,receive_prof,receiver_name,receiver_token,reciver_uid,sender_uid;
    DatabaseReference reference,reference_response,videocallref;
    VcModel model;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    VideoCallModel videoCallModel;

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
        setContentView(R.layout.activity_video_call_outgoing);


        model = new VcModel();

        videoCallModel  = new VideoCallModel();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        sender_uid = user.getUid();
        imageView = findViewById(R.id.iv_og_vc);
        tvname = findViewById(R.id.name_vc_og);
        tvprof = findViewById(R.id.prof_og_vc);
        declinebtn = findViewById(R.id.decline_vc_og);

        //-2
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
            reciver_uid = bundle.getString("uid");
            Toast.makeText(this, "Data gottt", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Data missing", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent (VideoCallOutgoing.this, ChatActivity.class);
            startActivity(intent);
        }

        reference = database.getReference("ALl Users").child(reciver_uid);
        videocallref = FirebaseDatabase.getInstance().getReference("vc");
        reference_response = database.getReference("vcref").child(sender_uid).child(reciver_uid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    receiver_name = snapshot.child("name").getValue().toString();
                    receiver_url = snapshot.child("url").getValue().toString();
                    receive_prof = snapshot.child("prof").getValue().toString();

                    tvname.setText(receiver_name);
                    Picasso.get().load(receiver_url).into(imageView);
                    tvprof.setText(receive_prof);

                }else {
                    Toast.makeText(VideoCallOutgoing.this, "Cannot make call", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendCallInvitation();

        checkResponse();

        declinebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelVC();
            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        cancelVC();
    }

    private void cancelVC() {
        DatabaseReference cancelRef;
        cancelRef = database.getInstance().getReference("cancel");

        model.setResponse("no");
        model.setKey("120");
        cancelRef.child(sender_uid).setValue(model);
        Toast.makeText(this, "Call ended", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(VideoCallOutgoing.this,ChatActivity.class);
        startActivity(intent);
        reference_response.removeValue();
        videocallref.removeValue();
        finish();


    }//checked

    private void checkResponse() {

        reference_response.child("res").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    String key = snapshot.child("key").getValue().toString();
                    String response = snapshot.child("response").getValue().toString();

                    if (response.equals("yes")){
                        reference_response.removeValue();

                        joinmeeting(key);
                        Toast.makeText(VideoCallOutgoing.this, "Call Accepted", Toast.LENGTH_SHORT).show();

                    }else  if (response.equals("no")){

                        Toast.makeText(VideoCallOutgoing.this, "Call denied", Toast.LENGTH_SHORT).show();
                        reference_response.removeValue();
                        Intent intent = new Intent(VideoCallOutgoing.this,ChatActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }else {

                    // Toast.makeText(VideoCallOutgoing.this, "Not responding", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }//full checked

    private void joinmeeting(String key) {

        try {

//            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
//                    .setRoom(key)
//                    .build();
//            JitsiMeetActivity.launch(VideoCallOutgoing.this,options);
//            finish();

            JitsiMeetConferenceOptions options
                    = new JitsiMeetConferenceOptions.Builder()
                    .setRoom(key)
                    // Settings for audio and video
                    //.setAudioMuted(true)
                    //.setVideoMuted(true)
                    .build();
            // Launch the new activity with the given options. The launch() method takes care
            // of creating the required Intent and passing the options.
            JitsiMeetActivity.launch(this, options);
            finish();

        }catch (Exception e){

            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }//full checked

    private void sendCallInvitation() {

        FirebaseDatabase.getInstance().getReference("Token").child(reciver_uid).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                receiver_token = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        videoCallModel.setCalleruid(sender_uid);
        videocallref.child(reciver_uid).setValue(videoCallModel);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                FcmNotificationsSender notificationsSender =
                        new FcmNotificationsSender(receiver_token,"WELink","Incoming Video Call",
                                getApplicationContext(),VideoCallOutgoing.this);
                //body part e onno kichu korse 35 min e ses korsi

                notificationsSender.SendNotifications();

            }
        },1000);


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
            }
        }
    }

    // Example for sending actions to JitsiMeetSDK
    private void hangUp() {
        Intent hangupBroadcastIntent = BroadcastIntentHelper.buildHangUpIntent();
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(hangupBroadcastIntent);
    }
}
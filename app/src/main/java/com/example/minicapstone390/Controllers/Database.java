package com.example.minicapstone390.Controllers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.minicapstone390.R;
import com.example.minicapstone390.Views.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Locale;

public class Database extends FirebaseMessagingService {
    private static final String DEVICES = DatabaseEnv.DEVICES.getEnv();
    private static final String SENSORS = DatabaseEnv.SENSORS.getEnv();
    private static final String USERS = DatabaseEnv.USERS.getEnv();

    private final FirebaseMessaging messaging = FirebaseMessaging.getInstance();
    private final FirebaseDatabase database;
    private final FirebaseAuth auth;
    private final FirebaseUser user;

    public Database() {
        this.auth = FirebaseAuth.getInstance();
        this.database = FirebaseDatabase.getInstance();
        this.user = auth.getCurrentUser();
    }

    public FirebaseMessaging getMessaging() { return this. messaging; }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void generateNotification(RemoteMessage message) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        String title = message.getNotification().getTitle();
        String text = message.getNotification().getBody();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        final String channelId = "HEADS_UP_NOTIFICATION";
        NotificationChannel channel = new NotificationChannel(channelId,"Heads Up Notification", NotificationManager.IMPORTANCE_HIGH);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);

        Notification.Builder notification = new Notification.Builder(this, channelId).setContentTitle(title).setContentText(text).setSmallIcon(R.drawable.ic_launcher_foreground).setAutoCancel(true);
        NotificationManagerCompat.from(this).notify(1, notification.build());
        super.onMessageReceived(message);
    }

    public FirebaseUser getUser() { return this.user; }

    public FirebaseAuth getAuth() { return this.auth; }

    public FirebaseDatabase getDatabase() { return this.database; }

    public DatabaseReference getUserRef() { return this.database.getReference(USERS); }

    public DatabaseReference getUserChild() { return this.database.getReference(USERS).child(getUserId()); }

    public DatabaseReference getUserChild(String node) { return getUserRef().child(node); }

    public DatabaseReference getDeviceRef() { return this.database.getReference(DEVICES); }

    public DatabaseReference getDeviceChild(String node) { return getDeviceRef().child(node); }

    public DatabaseReference getSensorRef() { return this.database.getReference(SENSORS); }

    public DatabaseReference getSensorChild(String node) { return getSensorRef().child(node); }

    public String getUserId() { return this.user.getUid(); }
}

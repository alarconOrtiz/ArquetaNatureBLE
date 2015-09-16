package es.alarcon.arquetanatureble.CORE;

/*
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.R;
import android.content.Intent;
import android.content.Context;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;*/


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;


import es.alarcon.arquetanatureble.GUI.ScanActivity;
import es.alarcon.arquetanatureble.R;


/**
 * Created by alarcon on 24/7/15.
 */
public class MyNotificationHandler
{
    //private Notification mNotification;
    private NotificationManager mNotificationManager;
    private Service mService;

    private NotificationCompat.Builder mNotification;
    private PendingIntent pIntent;
    private Intent resultIntent;
    private TaskStackBuilder stackBuilder;

    public static final String NOTIFICACION_MESSAGE   = "Nuevo Dispositivo encontrado.";
    public static final String NOTIFICACION_TITLE     = "DISPOSITIVO: Arqueta";
    public static final String NOTIFICACION_TICKER    ="Nueva Arqueta detectada.";
    public static final int NOTIFICATION_ID           = 1100;

    public MyNotificationHandler(Service service)
    {
        super();
        //mNotification        = new Notification();
        mService             = service;
    }

    public void SendNotify()
    {
        //old implementation
        /*Notification notification                = CreateNotification();
        mNotificationManager =(NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, notification);*/

        startNotification();
        mNotificationManager = (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mNotification.build());
    }


    protected void startNotification()
    {
        //Creating Notification Builder
        mNotification = new NotificationCompat.Builder(mService.getBaseContext());
        //Title for Notification
        mNotification.setContentTitle(NOTIFICACION_MESSAGE);
        //Message in the Notification
        mNotification.setContentText(NOTIFICACION_TITLE);
        //Alert shown when Notification is received
        mNotification.setTicker(NOTIFICACION_TICKER);
        //Icon to be set on Notification
        mNotification.setSmallIcon(R.drawable.abc_btn_radio_material);

        //Creating new Stack Builder
        stackBuilder = TaskStackBuilder.create(mService.getBaseContext());
        stackBuilder.addParentStack(ScanActivity.class);
        //Intent which is opened when notification is clicked
        resultIntent = new Intent(mService.getBaseContext(), ScanActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        pIntent =  stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mNotification.setContentIntent(pIntent);
    }

    //old implementation
    /*
    private Notification CreateNotification()
    {
        Notification notification = new Notification(R.drawable.ic_input_add, NOTIFICACION_MESSAGE, System.currentTimeMillis());

        Intent notificationIntent = new Intent(mService.getApplicationContext(), ScanActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mService.getApplicationContext(), 0, notificationIntent, 0);

        notification.setLatestEventInfo(mService.getApplicationContext(), NOTIFICACION_TITLE, NOTIFICACION_MESSAGE, pendingIntent);

        return notification;
    }*/
}

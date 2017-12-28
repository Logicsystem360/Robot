package bgstudios.robot;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity {

    int refresh = 300;
    String pwd = "";
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);

        final PipedWriter pw = new PipedWriter();
        PipedReader pr = null;
        try {
            pr = new PipedReader(pw);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SSH2 task = new SSH2("Thread 1", pr, pw);
        task.start();

        VideoView vidView = (VideoView)findViewById(R.id.vid);
        String vidAddress = "http://10.120.15.189:8080/?action=stream.mp4";
        Uri vidUri = Uri.parse(vidAddress);

        vidView.setVideoURI(vidUri);

        vidView.start();

        final Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                command(pw, "python2.7 /home/pi/script/python.py");
            }
        });

        final Button stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                command(pw, String.valueOf(-2));
            }
        });

        JoystickView joystick = (JoystickView) findViewById(R.id.joystickView);

        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                command(pw, String.valueOf(angle) + " " + String.valueOf(strength));
            }
        }, refresh);
    }


    public void command(PipedWriter pw, String command){
        try {
            pw.write(pwd + command + "\n");
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

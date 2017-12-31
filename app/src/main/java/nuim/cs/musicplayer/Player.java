package nuim.cs.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.marcinmoskala.arcseekbar.ArcSeekBar;
import com.marcinmoskala.arcseekbar.ProgressListener;
import com.triggertrap.seekarc.SeekArc;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by david on 31/12/2017.
 */

public class Player extends AppCompatActivity implements View.OnClickListener {

    Button rot;
    Animation rotate;

    ImageButton play, btFF, btRW, btNxt, btPre;
    ImageView robt;

    SeekArc sb;
    Handler handler;
    Runnable runnable;

    static MediaPlayer mp;
    ArrayList<File> mySongs;
    int position;
    Uri u;
    Thread updateSeekArc;

    public SeekBar volumeSeekBar;
    private AudioManager audioManager;
    ArcSeekBar arcSeekBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_layout);


        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initControls();



        play = (ImageButton) findViewById(R.id.play);
        btFF = (ImageButton) findViewById(R.id.btFF);
        btRW = (ImageButton) findViewById(R.id.btRW);
        btNxt = (ImageButton) findViewById(R.id.playNext);
        btPre = (ImageButton) findViewById(R.id.playPrev);


        play.setOnClickListener(this);
        btFF.setOnClickListener(this);
        btRW.setOnClickListener(this);
        btNxt.setOnClickListener(this);
        btPre.setOnClickListener(this);

        sb = (SeekArc) findViewById(R.id.seekArc);
        updateSeekArc = new Thread(){
            @Override
            public void run() {
                int totalDuration = mp.getDuration();
                int currentPosition = 0;


                while(currentPosition < totalDuration){

                    try {
                        sleep(500);
                        currentPosition = mp.getCurrentPosition();
                        sb.setProgress(currentPosition);
                    } catch(InterruptedException e){
                        e.printStackTrace();
                    }

                }
                //super.run();
            }
        };

        if(mp!= null)
        {
            mp.stop();
            mp.release();

        }


        Intent i = getIntent();
        Bundle b = i.getExtras();
        mySongs = (ArrayList) b.getParcelableArrayList("songlist");
        position = b.getInt("pos",0);

        u = Uri.parse(mySongs.get(position).toString());
        mp = MediaPlayer.create(getApplicationContext(),u);
        mp.start();
        sb.setMax(mp.getDuration());
        updateSeekArc.start();

        sb.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {

            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
            mp.seekTo(seekArc.getProgress());
            }
        });







        //Arc seek bar
        arcSeekBar = (ArcSeekBar) findViewById(R.id.arcSeekBar);
        //int[] array = getResources().getIntArray(R.array.gradient);
        //arcSeekBar.setProgressGradient(array);
        //arcSeekBar.addOnAttachStateChangeListener((View.OnAttachStateChangeListener) volumeSeekBar);
        arcSeekBar.setOnProgressChangedListener(new ProgressListener() {
            @Override
            public void invoke(int progress) {
                volumeSeekBar.setProgress(progress);

            }

        });

    }


    //Method to control audio volume
    public void initControls()
    {
        try {
            volumeSeekBar = findViewById(R.id.seekBarVolume);
            volumeSeekBar.setVisibility(View.GONE);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volumeSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekBar.setProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar)
                {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar)
                {

                }
            });

        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.play:
                if (mp.isPlaying()) {
                    mp.pause();
                    play.setBackgroundResource(R.drawable.play);
                } else {
                    mp.start();
                    play.setBackgroundResource(R.drawable.pause);
                }
                break;

            case R.id.btFF:
                mp.seekTo(mp.getCurrentPosition() + 5000);
                break;
            case R.id.btRW:
                mp.seekTo(mp.getCurrentPosition() - 5000);
                break;
            case R.id.playNext:
                mp.stop();
                mp.release();
                position = (position + 1) % mySongs.size();
                u = Uri.parse(mySongs.get(position).toString());
                mp = MediaPlayer.create(getApplicationContext(), u);
                mp.start();
                sb.setMax(mp.getDuration());
                break;
            case R.id.playPrev:
                mp.stop();
                mp.release();
                position = (position - 1 < 0) ? mySongs.size() - 1 : position - 1;
                /*if(position-1 < 0)
                {
                    position = mySongs.size()-1;
                }
                else{
                    position = position-1;
                }*/
                u = Uri.parse(mySongs.get(position).toString());
                mp = MediaPlayer.create(getApplicationContext(), u);
                mp.start();
                sb.setMax(mp.getDuration());
                break;




        }
    }
}

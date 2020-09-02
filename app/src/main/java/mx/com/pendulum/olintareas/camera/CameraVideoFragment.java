package mx.com.pendulum.olintareas.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.File;

import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.utilities.Tools;

import static android.media.MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED;
import static android.media.MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED;


/**
 * Use the {@link CameraVideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraVideoFragment extends VideoCapture implements MediaPlayer.OnCompletionListener,
        MediaRecorder.OnInfoListener, View.OnClickListener {
     public static final String KEY_SECONDS_RECORD = "Key_seconds_record";
    //Unbinder unbinder;
    AutoFitTextureView mTextureView;
    ImageButton mRecordVideo;
    ImageButton flash;
    VideoView mVideoView;
    ImageView ivClose;
    ImageButton mZoom;
    Chronometer chrTimer;
    String mOutputFilePath;
    private Interfaces.OnResponse response;
    @SuppressLint("StaticFieldLeak")
    private static CameraVideoFragment fragment;
    private int handlerRequest;

    public CameraVideoFragment() {
        // Required empty public constructor
    }

    public static CameraVideoFragment newInstance(Interfaces.OnResponse response, int handlerRequest, Bundle arguments) {
        if (fragment == null) {
            fragment = new CameraVideoFragment();
        }
        fragment.response = response;
        fragment.handlerRequest = handlerRequest;
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        //unbinder = ButterKnife.bind(this, view);

        mTextureView = view.findViewById(R.id.mTextureView);
        mRecordVideo = view.findViewById(R.id.mRecordVideo);
        flash = view.findViewById(R.id.flash);
        mVideoView = view.findViewById(R.id.mVideoView);
        ivClose = view.findViewById(R.id.ivClose);
        mZoom = view.findViewById(R.id.ibZoom);
        chrTimer = view.findViewById(R.id.chrTimer);
        positionFreeUsed = 0;
        chrTimer.setVisibility(View.GONE);
        view.findViewById(R.id.mRecordVideo).setOnClickListener(this);
        view.findViewById(R.id.flash).setOnClickListener(this);
        view.findViewById(R.id.ivClose).setOnClickListener(this);
        view.findViewById(R.id.ibZoom).setOnClickListener(this);
//        chrTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
//            @Override
//            public void onChronometerTick(Chronometer chronometer) {
//                String time = chronometer.getText().toString().split(":")[1];
//                Log.i("T_CRONO",time+"");
//                if(time.equals("30")){
//                    stopRecording(true);
//                    Toast.makeText(getActivity(), "El tiempo maximo de grabación es de 30 segundos.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        return view;
    }

    @Override
    public int getTextureResource() {
        return R.id.mTextureView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibZoom:
                responseToActivity(getActivity(), mOutputFilePath, 4);
                break;
            case R.id.ivClose:
                finishPreview();
                responseToActivity(getActivity(), mOutputFilePath, 3);
                break;
            case R.id.mRecordVideo:
                if (mIsRecordingVideo) {
                    stopRecording(true);
                    /*File file = new File(mOutputFilePath);
                    knowSizeInMegas(file);*/
                } else {
                    int numberVideos = getArguments().getInt(CaptureVideoActivity.KEY_NUMBER_OF_VIDEOS, -1);
                    TextView tvContador = getActivity().findViewById(R.id.contador);
                    int contador = Integer.parseInt(tvContador.getText().toString());
                    if (numberVideos == -1 || (contador < numberVideos)) {
                        startRecordingVideo(this);
                        mRecordVideo.setImageResource(R.mipmap.ic_stop);
                        mZoom.setVisibility(View.VISIBLE);
                        mOutputFilePath = getCurrentFile().getAbsolutePath();
                        responseToActivity(getActivity(), mOutputFilePath, 1);
                        startChronometer();
                    } else
                        Toast.makeText(getActivity(), "Sólo se pueden tomar " +
                                numberVideos + " videos", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.flash:
                @SuppressWarnings("ConstantConditions")
                ImageButton ib = getView().findViewById(R.id.flash);
                switch (flashStatus) {
                    case CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH:
                        flashStatus = CaptureRequest.CONTROL_AE_MODE_OFF;
                        ib.setImageResource(R.mipmap.ic_flash_off);
                        break;
                    case CaptureRequest.CONTROL_AE_MODE_OFF:
                        flashStatus = CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH;
                        ib.setImageResource(R.mipmap.ic_flash_auto);
                        break;
                    case CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH:
                        flashStatus = CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH;
                        ib.setImageResource(R.mipmap.ic_flash_on);
                        break;
                }
                break;
        }
    }

    /*private void knowSizeInMegas(File file){
        if (file.exists()) {
            long fileSizeInBytes = file.length(); // Size in bytes
            long fileSizeInKB = fileSizeInBytes / 1024; // Size in kilobytes (1 KB = 1024 Bytes)
            long fileSizeInMB = fileSizeInKB / 1024; //  Size in Megas (1 MB = 1024 KBytes)
            Toast.makeText(getContext(), "Megas ---> " + fileSizeInMB, Toast.LENGTH_SHORT).show();
        }
    }*/

    private void startChronometer() {
        chrTimer.setVisibility(View.VISIBLE);
        chrTimer.setBase(SystemClock.elapsedRealtime());
        chrTimer.start();
    }

    private void stopChronometer() {
        chrTimer.setVisibility(View.GONE);
        chrTimer.stop();
    }

    void stopRecording(boolean infortToActivity) {
        try {
            stopRecordingVideo();
            if (infortToActivity) {
                responseToActivity(getActivity(), mOutputFilePath, 2);
                stopChronometer();
            } else {
                File file = new File(mOutputFilePath);
                if (file.exists())
                    if (file.delete()) {
                        mOutputFilePath = "";
                    } else {
                        mOutputFilePath = "";
                    }
            }
            mRecordVideo.setImageResource(R.mipmap.ic_record);
            mZoom.setVisibility(View.GONE);
            startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void prepareViews() {
        if (mVideoView.getVisibility() == View.GONE) {
            mVideoView.setVisibility(View.VISIBLE);
            mTextureView.setVisibility(View.GONE);
            ivClose.setVisibility(View.VISIBLE);
            mZoom.setVisibility(View.VISIBLE);
            mRecordVideo.setVisibility(View.GONE);
            flash.setVisibility(View.GONE);
            setMediaForRecordVideo();
        } else {
            setMediaForRecordVideo();
        }
    }

    private void setMediaForRecordVideo() {
        MediaController media = new MediaController(getActivity());
        mVideoView.setMediaController(media);
        mVideoView.requestFocus();
        mVideoView.setVideoPath(mOutputFilePath);
        mVideoView.seekTo(100);
        mVideoView.setOnCompletionListener(this);
        media.show(5000);
        mVideoView.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //unbinder.unbind();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //finishPreview();
    }

    void finishPreview() {
        mVideoView.setVisibility(View.GONE);
        ivClose.setVisibility(View.GONE);
        mTextureView.setVisibility(View.VISIBLE);
        mRecordVideo.setVisibility(View.VISIBLE);
        flash.setVisibility(View.VISIBLE);
        mRecordVideo.setImageResource(R.mipmap.ic_record);
        mZoom.setVisibility(View.GONE);
        if (mVideoView.isPlaying()) {
            mVideoView.stopPlayback();
        }
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        if (what == MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
            stopRecording(true);
            Tools.showSnack(getContext(), "Limite de tamaño permitido alcanzado");
        } else if (what == MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            stopRecording(true);
            Tools.showSnack(getContext(), "Limite de duración permitido alcanzado");
        }
    }

    @SuppressWarnings("unchecked")
    public void responseToActivity(final Activity activity, final String mOutputFilePath, final int option) {
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    File file = new File(mOutputFilePath);
                    switch (option) {
                        case 4: // Zoom Pantalla
                            fragment.response.onResponse(CaptureVideoActivity.EXPAND_SCREEN, file);
                            break;
                        case 3: // Cerrar Preview Video
                            fragment.response.onResponse(CaptureVideoActivity.CLOSE_VIDEO_PREVIEW, file);
                            break;
                        case 2: // Finalizar grabacion
                            fragment.response.onResponse(fragment.handlerRequest, file);
                            break;
                        case 1: // Iniciar Grabacion
                            fragment.response.onResponse(CaptureVideoActivity.BEGIN_RECORD, file);
                            break;
                    }
                }
            });
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ImageButton takeVideo = getActivity().findViewById(R.id.mRecordVideo);
        ConstraintLayout.LayoutParams paramsTakeVideo = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            paramsTakeVideo.endToEnd = R.id.clFragmentVideo;
            paramsTakeVideo.topToTop = R.id.clFragmentVideo;
            paramsTakeVideo.bottomToBottom = R.id.clFragmentVideo;
            paramsTakeVideo.setMargins(0, 0, 96, 0);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            paramsTakeVideo.leftToLeft = R.id.clFragmentVideo;
            paramsTakeVideo.rightToRight = R.id.clFragmentVideo;
            paramsTakeVideo.bottomToBottom = R.id.clFragmentVideo;
            paramsTakeVideo.setMargins(0, 0, 0, 96);
        }
        takeVideo.setLayoutParams(paramsTakeVideo);
    }
}
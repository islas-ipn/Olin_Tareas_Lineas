package mx.com.pendulum.olintareas.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.MediaController;

import java.io.IOException;

import mx.com.pendulum.olintareas.R;

public class VIdeoTextureView extends TextureView implements TextureView.SurfaceTextureListener,
        MediaController.MediaPlayerControl, MediaPlayer.OnPreparedListener {

    private MediaPlayer mMediaPlayer;
    private Uri mSource;
    private MediaPlayer.OnCompletionListener mCompletionListener;
    private boolean isLooping = false;
    private MediaController mediaController;
    private Handler handler = new Handler();
    private FrameLayout frameView;


    public VIdeoTextureView(Context context) {
        this(context, null, 0);
    }

    public VIdeoTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VIdeoTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setSurfaceTextureListener(this);
    }

    public void setSource(String filePath) {
        mSource = Uri.parse(filePath);
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        mCompletionListener = listener;
    }

    public void setLooping(boolean looping) {
        isLooping = looping;
    }

    @Override
    protected void onDetachedFromWindow() {
        // release resources on detach
        if (mMediaPlayer != null) {
            mediaController.hide();
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Surface surface = new Surface(surfaceTexture);
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return false;
                }
            });
            mediaController = new MediaController(getContext());
            mMediaPlayer.setLooping(isLooping);
            mMediaPlayer.setDataSource(getContext(), mSource);
            mMediaPlayer.setSurface(surface);
            mMediaPlayer.setOnPreparedListener(this);
            //mMediaPlayer.setDisplay(holder);
            mMediaPlayer.prepare();
            mMediaPlayer.setScreenOnWhilePlaying(true);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            mMediaPlayer.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        surface.release();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mediaController.isShowing()) {
            mediaController.hide();
            frameView.setVisibility(GONE);
        } else {
            mediaController.show();
            frameView.setVisibility(VISIBLE);
        }
        return false;
    }

    //--MediaPlayerControl methods----------------------------------------------------
    public void start() {
        mMediaPlayer.start();
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public void seekTo(int i) {
        mMediaPlayer.seekTo(i);
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public int getBufferPercentage() {
        return 0;
    }

    public boolean canPause() {
        return true;
    }

    public boolean canSeekBackward() {
        return true;
    }

    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp,
                                           int width, int height) {
            }
        });
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(findViewById(R.id.mVideoView));
        handler.post(new Runnable() {
            public void run() {
                mediaController.setEnabled(true);
                if (mediaController.isShowing()) {
                    mediaController.hide();
                    frameView.setVisibility(GONE);
                } else {
                    mediaController.show();
                    frameView.setVisibility(VISIBLE);
                }
            }
        });
        ((ViewGroup) mediaController.getParent()).removeView(mediaController);
        frameView.addView(mediaController);
        mediaController.setVisibility(View.VISIBLE);
        mMediaPlayer.start();
    }

    public void SetFrameLaout(FrameLayout viewById) {
        this.frameView = viewById;
    }
}
package mx.com.pendulum.olintareas.camera;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ExifInterface;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;
import com.j256.ormlite.dao.Dao;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import mx.com.pendulum.olintareas.Properties;
import mx.com.pendulum.olintareas.R;
import mx.com.pendulum.olintareas.config.util.ContextApplication;
import mx.com.pendulum.olintareas.db.UserDatabaseHelper;
import mx.com.pendulum.olintareas.dto.UserData;
import mx.com.pendulum.olintareas.interfaces.Interfaces;
import mx.com.pendulum.olintareas.tareas.views.ViewFile_upload;
import mx.com.pendulum.utilities.Tools;

import static android.media.MediaPlayer.create;

@SuppressWarnings("rawtypes")
@SuppressLint("NewApi")
public class CameraTakePhotoFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private static CameraTakePhotoFragment fragment;
    private Interfaces.OnResponse response;
    private int handlerRequest;
    ArrayList<Integer> spacesFree;
    private int positionFreeUsed;
    ArrayList<Integer> internSpacesFree;
    private MediaPlayer mp;
    private int flashStatus;

    public static CameraTakePhotoFragment newInstance(Interfaces.OnResponse response,
                                                      int handlerRequest, Bundle arguments) {
        if (fragment == null) {
            fragment = new CameraTakePhotoFragment();
        }
        fragment.response = response;
        fragment.handlerRequest = handlerRequest;
        fragment.setArguments(arguments);
        return fragment;
    }

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /**
     * Tag for the {@link Log}.
     */
    private static final String TAG = "Camera2BasicFragment";

    /**
     * Camera state: Showing camera preview.
     */
    private static final int STATE_PREVIEW = 0;

    /**
     * Camera state: Waiting for the focus to be locked.
     */
    private static final int STATE_WAITING_LOCK = 1;

    /**
     * Camera state: Waiting for the exposure to be precapture state.
     */
    private static final int STATE_WAITING_PRECAPTURE = 2;

    /**
     * Camera state: Waiting for the exposure state to be something other than precapture.
     */
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;

    /**
     * Camera state: Picture was taken.
     */
    private static final int STATE_PICTURE_TAKEN = 4;

    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_WIDTH = 1920;

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_HEIGHT = 1080;

    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }
    };

    /**
     * ID of the current {@link CameraDevice}.
     */
    private String mCameraId;

    /**
     * An {@link AutoFitTextureView} for camera preview.
     */
    private AutoFitTextureView mTextureView;

    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    private CameraCaptureSession mCaptureSession;

    /**
     * A reference to the opened {@link CameraDevice}.
     */
    private CameraDevice mCameraDevice;

    /**
     * The {@link android.util.Size} of camera preview.
     */
    private Size mPreviewSize;

    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
     */
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            Activity activity = getActivity();
            if (null != activity) {
                activity.finish();
            }
        }
    };

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread mBackgroundThread;

    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler mBackgroundHandler;

    /**
     * An {@link ImageReader} that handles still image capture.
     */
    private ImageReader mImageReader;

    /**
     * This is the output file for our picture.
     */
    private File mFile;

    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            mBackgroundHandler.post(new ImageSaver(getActivity(), reader.acquireNextImage(), mFile));
        }

    };

    /**
     * {@link CaptureRequest.Builder} for the camera preview
     */
    private CaptureRequest.Builder mPreviewRequestBuilder;

    /**
     * {@link CaptureRequest} generated by {@link #mPreviewRequestBuilder}
     */
    private CaptureRequest mPreviewRequest;

    /**
     * The current state of camera state for taking pictures.
     *
     * @see #mCaptureCallback
     */
    private int mState = STATE_PREVIEW;

    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    /**
     * Whether the current camera device supports Flash or not.
     */
    private boolean mFlashSupported;

    /**
     * Orientation of the camera sensor
     */
    private int mSensorOrientation;

    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
     */
    private CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            switch (mState) {
                case STATE_PREVIEW: {
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
                case STATE_WAITING_LOCK: {
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == null) {
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null ||
                                aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = STATE_PICTURE_TAKEN;
                            captureStillPicture();
                        } else {
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                }
            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            process(result);
        }
    };

    /**
     * Shows a {@link Snackbar} on the UI thread.
     */
    private void showSnack() {
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Tools.showSnack(activity, "Failed");
                }
            });
        }
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, choose the smallest one that
     * is at least as large as the respective texture view size, and that is at most as large as the
     * respective max size, and whose aspect ratio matches with the specified value. If such size
     * doesn't exist, choose the largest one that is at most as large as the respective max size,
     * and whose aspect ratio matches with the specified value.
     *
     * @param choices           The list of sizes that the camera supports for the intended output
     *                          class
     * @param textureViewWidth  The width of the texture view relative to sensor coordinate
     * @param textureViewHeight The height of the texture view relative to sensor coordinate
     * @param maxWidth          The maximum width that can be chosen
     * @param maxHeight         The maximum height that can be chosen
     * @param aspectRatio       The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth,
                                          int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }
        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        SimpleOrientationListener mOrientationListener = new SimpleOrientationListener(
//                getActivity()) {
//            @Override
//            public void onSimpleOrientationChanged(int orientation) {
//                CameraTakePhotoFragment.orientacion = orientation;
//            }
//        };
//        mOrientationListener.enable();
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ImageButton takePhoto = getActivity().findViewById(R.id.takePhoto);
        ImageButton flash = getActivity().findViewById(R.id.flash);
        ImageButton back = getActivity().findViewById(R.id.back);
        RelativeLayout.LayoutParams paramsTakePhoto = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams paramsFlash = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams paramsBack = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            paramsTakePhoto.addRule(RelativeLayout.ALIGN_PARENT_END);
            paramsTakePhoto.addRule(RelativeLayout.CENTER_VERTICAL);
            paramsFlash.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            paramsFlash.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            paramsFlash.setMargins(32, 32, 32, 32);
            paramsBack.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            paramsBack.setMargins(32, 32, 32, 32);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            paramsTakePhoto.addRule(RelativeLayout.CENTER_HORIZONTAL);
            paramsTakePhoto.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            paramsFlash.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            paramsFlash.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            paramsFlash.setMargins(32, 32, 32, 32);
            paramsBack.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            paramsBack.setMargins(32, 32, 32, 32);
        }
        takePhoto.setLayoutParams(paramsTakePhoto);
        flash.setLayoutParams(paramsFlash);
        back.setLayoutParams(paramsBack);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        view.findViewById(R.id.takePhoto).setOnClickListener(this);
        view.findViewById(R.id.takePhoto).setOnLongClickListener(this);
        view.findViewById(R.id.flash).setOnClickListener(this);
        view.findViewById(R.id.texture).setOnClickListener(this);
        view.findViewById(R.id.gallery).setOnClickListener(this);
        mTextureView = view.findViewById(R.id.texture);
        mp = create(getActivity(), R.raw.camera_click);
        positionFreeUsed = 0;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        flashStatus = CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH;
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();
        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        try {
            closeCamera();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    /**
     * Sets up member variables related to camera.
     *
     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    private void setUpCameraOutputs(int width, int height) {
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics
                        = manager.getCameraCharacteristics(cameraId);
                // We don't use a front facing camera in this sample.
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }
                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }
                // For still image captures, we use the largest available size.
                Size largest = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new CompareSizesByArea());
                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                        ImageFormat.JPEG, /*maxImages*/2);
                mImageReader.setOnImageAvailableListener(
                        mOnImageAvailableListener, mBackgroundHandler);
                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.
                int displayRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                //noinspection ConstantConditions
                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                boolean swappedDimensions = false;
                switch (displayRotation) {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_180:
                        if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                            swappedDimensions = true;
                        }
                        break;
                    case Surface.ROTATION_90:
                    case Surface.ROTATION_270:
                        if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                            swappedDimensions = true;
                        }
                        break;
                    default:
                        Log.e(TAG, "Display rotation is invalid: " + displayRotation);
                }
                Point displaySize = new Point();
                activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
                int rotatedPreviewWidth = width;
                int rotatedPreviewHeight = height;
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;
                if (swappedDimensions) {
                    //noinspection SuspiciousNameCombination
                    rotatedPreviewWidth = height;
                    //noinspection SuspiciousNameCombination
                    rotatedPreviewHeight = width;
                    //noinspection SuspiciousNameCombination
                    maxPreviewWidth = displaySize.y;
                    //noinspection SuspiciousNameCombination
                    maxPreviewHeight = displaySize.x;
                }
                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                    maxPreviewWidth = MAX_PREVIEW_WIDTH;
                }
                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                    maxPreviewHeight = MAX_PREVIEW_HEIGHT;
                }
                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                        maxPreviewHeight, largest);
                // We fit the aspect ratio of TextureView to the size of preview we picked.
//                int orientation = getResources().getConfiguration().orientation;
//                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                    mTextureView.setAspectRatio(
//                            mPreviewSize.getWidth(), mPreviewSize.getHeight());
//                } else {
//                    mTextureView.setAspectRatio(
//                            mPreviewSize.getHeight(), mPreviewSize.getWidth());
//                }
                // Check if the flash is supported.
                Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                mFlashSupported = available == null ? false : available;
                mCameraId = cameraId;
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the camera specified by {@link CameraTakePhotoFragment#mCameraId}.
     */
    private void openCamera(int width, int height) {
        setUpCameraOutputs(width, height);
        configureTransform(width, height);
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            if (ActivityCompat.checkSelfPermission(ContextApplication.getAppContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            }
            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    /**
     * Closes the current {@link CameraDevice}.
     */
    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new {@link CameraCaptureSession} for camera preview.
     */

    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);
            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);
            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == mCameraDevice) {
                                return;
                            }
                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession;
                            try {
                                // Auto focus should be continuous for camera preview.
                                if (isAutoFocusSupported())
                                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                            CaptureRequest.CONTROL_AF_MODE_AUTO);
                                else
                                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                // Flash is automatically enabled when necessary.
                                setAutoFlash(mPreviewRequestBuilder);
                                // Finally, we start displaying the camera preview.
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        mCaptureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                @NonNull CameraCaptureSession cameraCaptureSession) {
                            showSnack();
                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = getActivity();
        if (null == mTextureView || null == mPreviewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    /**
     * Lock the focus as the first step for a still image capture.
     */
    private void lockFocus() {
        try {
            // This is how to tell the camera to lock focus.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the lock.
            mState = STATE_WAITING_LOCK;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Run the precapture sequence for capturing a still image. This method should be called when
     * we get a response in {@link #mCaptureCallback} from {@link #lockFocus()}.
     */
    private void runPrecaptureSequence() {
        try {
            // This is how to tell the camera to trigger.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     * {@link #mCaptureCallback} from both {@link #lockFocus()}.
     */
    private void captureStillPicture() {
        try {
            final Activity activity = getActivity();
            if (null == activity || null == mCameraDevice) {
                return;
            }
            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());
            if (isAutoFocusSupported())
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_AUTO);
            else
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            setAutoFlash(captureBuilder);
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation));
            CameraCaptureSession.CaptureCallback CaptureCallback
                    = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureStarted(@NonNull CameraCaptureSession session,
                                             @NonNull CaptureRequest request,
                                             long timestamp,
                                             long frameNumber) {
                    super.onCaptureStarted(session, request, timestamp, frameNumber);
                    mp.start();
                }

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                               @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {
                    // showToast("Saved: " + mFile);
                    Log.d(TAG, mFile.toString());
                    unlockFocus();
                }
            };
            mCaptureSession.stopRepeating();
            mCaptureSession.capture(captureBuilder.build(), CaptureCallback, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the JPEG orientation from the specified screen rotation.
     *
     * @param rotation The screen rotation.
     * @return The JPEG orientation (one of 0, 90, 270, and 360)
     */
    private int getOrientation(int rotation) {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // We have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
        // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
        return (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360;
    }

    /**
     * Unlock the focus. This method should be called when still image capture sequence is
     * finished.
     */
    private void unlockFocus() {
        try {
            // Reset the auto-focus trigger
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            setAutoFlash(mPreviewRequestBuilder);
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW;
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
                    mBackgroundHandler);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAutoFlash(CaptureRequest.Builder requestBuilder) {
        if (mFlashSupported) {
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    flashStatus);
        }
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    /**
     * Saves a JPEG {@link Image} into the specified {@link File}.
     */
    public static class ImageSaver implements Runnable {

        private final Image mImage;
        private Activity activity;
        private final File mFile;

        ImageSaver(Activity activity, Image image, File file) {
            mImage = image;
            mFile = file;
            this.activity = activity;
        }

        @Override
        public void run() {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
//            rotateImage(mFile.getAbsolutePath());
            resizeBitmap(activity, mFile.getAbsolutePath());
            responseToActivity(activity, mFile);
        }
    }

    private boolean isAutoFocusSupported() {
        return isHardwareLevelSupported() || getMinimumFocusDistance() > 0;
    }

    // Returns true if the device supports the required hardware level, or better.
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean isHardwareLevelSupported() {
        boolean res = false;
        if (mCameraId == null)
            return res;
        try {
            CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(mCameraId);
            int deviceLevel = cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
            switch (deviceLevel) {
//                case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3:
//                    Log.d(TAG, "Camera support level: INFO_SUPPORTED_HARDWARE_LEVEL_3");
//                    break;
                case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL:
                    Log.d(TAG, "Camera support level: INFO_SUPPORTED_HARDWARE_LEVEL_FULL");
                    break;
                case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY:
                    Log.d(TAG, "Camera support level: INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY");
                    break;
                case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED:
                    Log.d(TAG, "Camera support level: INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED");
                    break;
                default:
                    Log.d(TAG, "Unknown INFO_SUPPORTED_HARDWARE_LEVEL: " + deviceLevel);
                    break;
            }
            if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
                //res = CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY == deviceLevel;
                res = true;
            } else {
                // deviceLevel is not LEGACY, can use numerical sort
                res = CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY <= deviceLevel;
            }
        } catch (Exception e) {
            Log.e(TAG, "isHardwareLevelSupported Error", e);
        }
        return res;
    }

    private float getMinimumFocusDistance() {
        if (mCameraId == null)
            return 0;
        Float minimumLens = null;
        try {
            CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics c = manager.getCameraCharacteristics(mCameraId);
            minimumLens = c.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
        } catch (Exception e) {
            Log.e(TAG, "isHardwareLevelSupported Error", e);
        }
        if (minimumLens != null)
            return minimumLens;
        return 0;
    }

    @Override
    public boolean onLongClick(final View v) {
        v.setOnClickListener(null);
        v.setOnLongClickListener(null);
        if (v.getId() == R.id.takePhoto) {
            mCameraOpenCloseLock.release();
            createCameraPreviewSession();
            View view = new View(getActivity());
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    takePicture();
                }
            }, 2000);
        }
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setOnClickListener(CameraTakePhotoFragment.this);
                v.setOnLongClickListener(CameraTakePhotoFragment.this);
            }
        }, 1000);
        return true;
    }

    //This method is called from background
    @SuppressWarnings("unchecked")
    public static void responseToActivity(final Activity activity, final File file) {
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fragment.response.onResponse(fragment.handlerRequest, file);
                }
            });
        }
    }

    private static void rotateImage(String filePath) {
//        String deviceName = android.os.Build.MODEL; // returns model name
        String deviceManufacturer = android.os.Build.MANUFACTURER; // returns manufacturer
        if ("samsung".equalsIgnoreCase(deviceManufacturer.toLowerCase())) {
            HashMap<String, String> exifParams = Tools.getExifParams(filePath);
            String orientatio = exifParams.get(ExifInterface.TAG_ORIENTATION);
            if (orientatio != null) {
                int o = Integer.parseInt(orientatio);
                Tools.rotateAndCompressImage(filePath, 100, o);
            }
        }
    }

    public static void resizeBitmap(Activity activity, String photoPath) {
        double destWidth = 870d;
        double destHeight = 570d;
        try {
            Bundle arguments = activity.getIntent().getExtras();
            if (arguments != null) {
                boolean typeSelected = arguments.getBoolean(CameraActivity.QUESTION_TYPE_IMAGE_RESIZE);
                // TODO SI ABA REGRESA, DESCOMENTAR Y VALIDAR SI ES NECESARIO EL REDIMENCIONAMIENTO
                //boolean isImageResize = arguments.getBoolean(CameraActivity.KEY_IMAGE_RESIZE, false);
                if (!typeSelected)
                    return;
                File imgFileOrig = new File(photoPath);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                options.inJustDecodeBounds = false;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                Bitmap b = BitmapFactory.decodeFile(imgFileOrig.getAbsolutePath(), options);
                int tipoSusuario = 0;
                UserDatabaseHelper helper = UserDatabaseHelper.getHelper(activity);
                try {
                    Dao<UserData, Long> dao = helper.getDao(UserData.class);
                    List<UserData> list = dao.queryForAll();
                    UserData userData = list.get(0);
                    tipoSusuario = userData.getPermisos().getTipo_usuario();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    helper.close();
                }
                if (tipoSusuario != 1) { //1 == interno
                    //FIXME cambios para usuarios externos
                    String deviceManufacturer = android.os.Build.MANUFACTURER;
                    Matrix matrix = new Matrix();
                    if ("samsung".equalsIgnoreCase(deviceManufacturer.toLowerCase())) {
                        HashMap<String, String> exifParams = Tools.getExifParams(photoPath);
                        String orientatio = exifParams.get(ExifInterface.TAG_ORIENTATION);
                        if (orientatio != null) {
                            int h = (int) destHeight;
                            int w = (int) destWidth;
                            int orientation = Integer.parseInt(orientatio);
                            switch (orientation) {
                                case 3:
                                    matrix.postRotate(180);
                                    break;
                                case 6:
                                    matrix.postRotate(90);

                                    break;
                                case 8:
                                    matrix.postRotate(270);

                                    break;
                                default:
                                    matrix.postRotate(0);
                                    break;
                            }
                            b = Bitmap.createScaledBitmap(b, w, h, false);
                            b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
                        }
                    }
                }
                // original measurements
                int origWidth = b.getWidth();
                int origHeight = b.getHeight();
                Bitmap b2;
                int h;
                int w;
                if (origHeight >= origWidth) {
                    h = (int) destHeight;
                    w = (int) destWidth;
                    //create white bitmpat at specific size, will be the background
                    Bitmap whiteBm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                    //resize captured image
                    double percent = destHeight / origHeight;//.1370
                    destWidth = (int) (origWidth * percent);
                    h = (int) destHeight;
                    w = (int) destWidth;
                    //overlay captured Image in white bitmap
                    b2 = overlayBitmapToCenter(whiteBm, Bitmap.createScaledBitmap(b, w, h, false));
                } else {
                    //is horizontal
                    h = (int) destHeight;
                    w = (int) destWidth;
                    b2 = Bitmap.createScaledBitmap(b, w, h, false);
                }
//            b2 = Bitmap.createScaledBitmap(b, destWidth, destHeight, false);
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                b2.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
                FileOutputStream fo = new FileOutputStream(imgFileOrig);
                fo.write(outStream.toByteArray());
                fo.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(final View view) {
        view.setOnClickListener(null);
        view.setOnLongClickListener(null);
        switch (view.getId()) {
            case R.id.gallery:
                response.onResponse(fragment.handlerRequest, true);
            case R.id.texture:
//                mCameraOpenCloseLock.release();
//                createCameraPreviewSession();
                this.onResume();
                break;
            case R.id.takePhoto:
                takePicture();
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
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setOnClickListener(CameraTakePhotoFragment.this);
                view.setOnLongClickListener(CameraTakePhotoFragment.this);
            }
        }, 1000);
    }

    @SuppressLint("SimpleDateFormat")
    private void takePicture() {
        int numberOfPhotos = getArguments().getInt(CameraActivity.KEY_NUMBER_OF_PHOTOS, -1);
        int alreadyTaken = getArguments().getInt(CameraActivity.KEY_ALREADY_TAKEN, 0);
        TextView tvContador = getActivity().findViewById(R.id.contador);
        int contador = Integer.parseInt(tvContador.getText().toString());
        if (numberOfPhotos == -1 || (contador < numberOfPhotos)) {
            Date date = new Date();
            String dateStr = new SimpleDateFormat(Properties.DATE_TIME_FORMAT_FILE).format(date);
            String mFileName;
            if (spacesFree != null) {
                if (spacesFree.size() > 0) {
                    if (internSpacesFree.size() > 0) {
                        mFileName = getArguments().getString(CameraActivity.KEY_FILE_NAME)
                                //+ alreadyTaken
                                + internSpacesFree.get(0)
                                + ViewFile_upload.SEPATATOR
                                + dateStr
                                + ".jpg";
                        internSpacesFree.remove(0);
                    } else {
                        positionFreeUsed = spacesFree.get(0);
                        mFileName = getArguments().getString(CameraActivity.KEY_FILE_NAME)
                                //+ alreadyTaken
                                + positionFreeUsed
                                + ViewFile_upload.SEPATATOR
                                + dateStr
                                + ".jpg";
                        spacesFree.remove(0);
                        positionFreeUsed++;
                    }
                } else {
                    if (internSpacesFree.size() > 0) {
                        mFileName = getArguments().getString(CameraActivity.KEY_FILE_NAME)
                                //+ alreadyTaken
                                + internSpacesFree.get(0)
                                + ViewFile_upload.SEPATATOR
                                + dateStr
                                + ".jpg";
                        internSpacesFree.remove(0);
                    } else {
                        mFileName = getArguments().getString(CameraActivity.KEY_FILE_NAME)
                                //+ alreadyTaken
                                + positionFreeUsed
                                + ViewFile_upload.SEPATATOR
                                + dateStr
                                + ".jpg";
                        positionFreeUsed++;
                    }
                }
            } else {
                mFileName = getArguments().getString(CameraActivity.KEY_FILE_NAME)
                        + alreadyTaken
                        + ViewFile_upload.SEPATATOR
                        + dateStr
                        + ".jpg";
            }
            String dir = getArguments().getString(CameraActivity.KEY_FILE_PATH);
            createFile(dir);
            mFile = new File(dir, mFileName);
            lockFocus();
        } else {
            Toast.makeText(getActivity(), "Sólo se pueden tomar " + numberOfPhotos + " fotos", Toast.LENGTH_SHORT).show();
        }
    }

    //This method is called from background
    @SuppressWarnings("unchecked")
    private static void saveOnDB(Activity activity, final File file) {
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fragment.response.onResponse(fragment.handlerRequest, file);
                }
            });
        }
    }

    public static Bitmap overlayBitmapToCenter(Bitmap bitmap1, Bitmap bitmap2) {
        int bitmap1Width = bitmap1.getWidth();
        int bitmap1Height = bitmap1.getHeight();
        int bitmap2Width = bitmap2.getWidth();
        int bitmap2Height = bitmap2.getHeight();
        float marginLeft = (float) (bitmap1Width * 0.5 - bitmap2Width * 0.5);
        float marginTop = (float) (bitmap1Height * 0.5 - bitmap2Height * 0.5);
        Bitmap overlayBitmap = Bitmap.createBitmap(bitmap1Width, bitmap1Height, bitmap1.getConfig());
        Canvas canvas = new Canvas(overlayBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap1, new Matrix(), null);
        canvas.drawBitmap(bitmap2, marginLeft, marginTop, null);
        return overlayBitmap;
    }

    private void createFile(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            boolean result = dir.mkdirs();
            if (result) {
                File noMedia = new File(path + File.separator + ".nomedia");
                if (!noMedia.exists()) {
                    try {
                        if(noMedia.createNewFile())
                            Log.d("<-- TAG -->","Archivo creado");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
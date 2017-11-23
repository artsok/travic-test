package io.github.artsok.travis.utils;

import lombok.NonNull;
import ru.sbtqa.tag.videorecorder.Recorder;
import ru.sbtqa.tag.videorecorder.VideoRecorderModule;
import ru.sbtqa.tag.videorecorder.VideoRecorderService;

import java.io.IOException;
import java.util.UUID;

/**
 *
 * @author sbt-sidochenko-vv
 */
public class VideoRecorder {

    private final String DEFAULT_VIDEOS_FOLDER = System.getProperty("java.io.tmpdir");
    private final String TEMP_FOLDER_PROPERTY_NAME = "videoTempFolder";
    private final String DST_PATH_PROPERTY_NAME = "videoDestinationPath";
    private static VideoRecorderModule videoRecorderModule;
    private static VideoRecorderService service;
    private final String videoDestinationPath;
    private String savedVideoPath;
    private String videoFileName;
    private boolean isVideoStarted = false;

    public VideoRecorder(@NonNull String path) throws IOException {
        videoDestinationPath = path;
        if (videoDestinationPath.isEmpty()) {
            throw new IOException("Property " + DST_PATH_PROPERTY_NAME + " was not set in capplication.properties");
        }
    }

    public void startRecording() {
        isVideoStarted = true;
        videoFileName = UUID.randomUUID().toString();
        videoRecorderModule = new VideoRecorderModule(getVideoFolderPath());
        Recorder provideScreenRecorder = videoRecorderModule.provideScreenRecorder();
        service = new VideoRecorderService(provideScreenRecorder);
        service.start();
    }

    public String stopRecording() {
        service.stop();
        isVideoStarted = false;
        savedVideoPath = service.save(videoDestinationPath, videoFileName);
        return savedVideoPath;
    }

    private String getVideoFolderPath() {
        String videoFolderPath = System.getProperty(TEMP_FOLDER_PROPERTY_NAME);
        if (videoFolderPath == null) {
            videoFolderPath = DEFAULT_VIDEOS_FOLDER;
        }
        return videoFolderPath;
    }

    public String getVideoPath() {
        return this.videoDestinationPath + "\\" + videoFileName + ".avi";
    }

    /**
     * @return the isVideoStarted
     */
    public boolean isVideoStarted() {
        return isVideoStarted;
    }
}


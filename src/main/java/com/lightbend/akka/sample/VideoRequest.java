package com.lightbend.akka.sample;

/**
 * Created by Kamil Radziszewski on 06.06.17.
 */
public class VideoRequest {
    private String name;
    private int jobId;
    private int parts;
    private int partId;
    private int startFrame;
    private int endFrame;
    private byte[] data;

    public VideoRequest(String name, int jobId, int parts, int partId, int startFrame, int endFrame, byte[] data) {
        this.name = name;
        this.jobId = jobId;
        this.parts = parts;
        this.partId = partId;
        this.startFrame = startFrame;
        this.endFrame = endFrame;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getParts() {
        return parts;
    }

    public void setParts(int parts) {
        this.parts = parts;
    }

    public int getPartId() {
        return partId;
    }

    public void setPartId(int partId) {
        this.partId = partId;
    }

    public int getStartFrame() {
        return startFrame;
    }

    public void setStartFrame(int startFrame) {
        this.startFrame = startFrame;
    }

    public int getEndFrame() {
        return endFrame;
    }

    public void setEndFrame(int endFrame) {
        this.endFrame = endFrame;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}

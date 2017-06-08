package com.lightbend.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaMaster extends AbstractActor {

    static public Props props() {
        return Props.create(MediaMaster.class, () -> new MediaMaster());
    }

    // map of job ids : list of video requests
    static private Map<Integer, Map<Integer, VideoRequest>> jobs = new HashMap<>();

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public MediaMaster() {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {

                    switch (s) {
                        case "partial_distribute":
                            log.info("Splitting video");
                            List<Process> processes = new ArrayList<>();
                            for (int i = 0; i < 3; i++) {
                                processes.add(Runtime.getRuntime().exec("ffmpeg -v quiet -y -i input.mp4 -ss " + i * 10 + " -t 10 MasterData/test" + i + ".mp4"));
                            }
                            int count = 0;
                            for (Process process : processes) {
                                synchronized (process) {
                                    process.wait();

                                    if (process.exitValue() != 0) {
                                        log.error(process.getErrorStream().toString());
                                        return;
                                    }
                                    log.info("Successfully splitted video number {}", count);
                                    Path path = Paths.get("MasterData/test" + count + ".mp4");
                                    byte[] bytes = Files.readAllBytes(path);
                                    VideoRequest videoRequest = new VideoRequest(Integer.toString(count), 0,
                                            3, count, 0, 0, bytes);
                                    AkkaQuickstart.actors.get(count++).tell(videoRequest, getSelf());
                                }
                            }
                    }
                })
                .match(VideoRequest.class, s -> {

                    log.info("Started receiving data from workers");
                    Integer jobId = s.getJobId();
                    Integer partNumber = s.getPartId();
                    Integer partsNumber = s.getParts();
                    jobs.putIfAbsent(jobId, new HashMap<>());
                    Map<Integer, VideoRequest> parts = jobs.get(jobId);

                    jobs.putIfAbsent(jobId, new HashMap<>());
                    jobs.get(jobId).putIfAbsent(partNumber, s);

                    if (parts.size() == partsNumber) {
                        for (int i = 0; i < partsNumber; i++) {
                            Files.write(Paths.get("MasterData/worker_part" + i + ".mp4"), parts.get(i).getData());
                        }

                        List<String> ffmpegCmd = new ArrayList<>();
                        ffmpegCmd.add("ffmpeg");
                        ffmpegCmd.add("-y");
                        ffmpegCmd.add("-v");
                        ffmpegCmd.add("quiet");
                        for (int i = 0; i < partsNumber; i++) {
                            ffmpegCmd.add("-i");
                            ffmpegCmd.add("MasterData/worker_part" + i + ".mp4");
                        }
                        ffmpegCmd.add("-filter_complex");
                        ffmpegCmd.add("concat=n=3:v=1:a=1 [v] [a]");
                        ffmpegCmd.add("-map");
                        ffmpegCmd.add("[v]");
                        ffmpegCmd.add("-map");
                        ffmpegCmd.add("[a]");
                        ffmpegCmd.add("MasterData/output.mp4");
                        String[] ffmpegArr = new String[ffmpegCmd.size()];
                        ffmpegArr = ffmpegCmd.toArray(ffmpegArr);
                        Process process = Runtime.getRuntime().exec(ffmpegArr);
                        synchronized (process) {
                            process.wait();
                        }
                        log.info("Converted video!");
                        jobs.get(jobId).clear();
                    }
                })
                .build();
    }
}
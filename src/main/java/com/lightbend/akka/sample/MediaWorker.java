package com.lightbend.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.dsl.Creators;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.nio.file.Files;
import java.nio.file.Paths;

public class MediaWorker extends AbstractActor {

    private static final String WORKER_DIRECTORY = "WorkerData";

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    static public Props props() {
        return Props.create(MediaWorker.class, () -> new MediaWorker());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(VideoRequest.class, s -> {
                    log.info("Converting video {}", s.getName());

                    Files.write(Paths.get("WorkerData/worker_media" + s.getPartId() + ".mp4"), s.getData());
                    Process process = Runtime.getRuntime().exec("ffmpeg -y -v quiet -i WorkerData/worker_media" + s.getPartId() + ".mp4 -shortest -af aecho=1:1 WorkerData/worker_output" + s.getPartId() + ".mp4");

                    byte[] bytes = Files.readAllBytes(Paths.get("WorkerData/worker_output" + s.getPartId() + ".mp4"));

                    VideoRequest videoRequest = new VideoRequest("worker_media" + s.getPartId(),
                            s.getJobId(), s.getParts(), s.getPartId(), 0, 0, bytes);
                    getSender().tell(videoRequest, getSelf());
                })
                .build();
    }
}
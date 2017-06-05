package com.lightbend.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class MediaMaster extends AbstractActor {

    static public Props props() {
        return Props.create(MediaMaster.class, () -> new MediaMaster());
    }

    static public class Greeting {
        public final String message;

        public Greeting(String message) {
            this.message = message;
        }
    }

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public MediaMaster() {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {

                    switch (s) {
                        case "distribute":
                            for (int i = 0; i < 10; i++) {
                                AkkaQuickstart.actors.get(i).tell("convert", getSelf());
                            }
                            break;
                    }
                })
                .build();
    }
}
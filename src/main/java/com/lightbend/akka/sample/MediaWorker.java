package com.lightbend.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.dsl.Creators;
import akka.event.Logging;
import akka.event.LoggingAdapter;

//#greeter-messages
public class MediaWorker extends AbstractActor {
//#greeter-messages

    private static final String text = "EXAMPLEAKKAPROGRAMEXAMPLEAKKAPROGRAMEXAMPLEAKKAPROGRAMEXAMPLEAKKAPROGRAMEXAMPLEAKKAPROGRAMEXAMPLEAKKAPROGRAMEXAMPLEAKKAPROGRAMEXAMPLEAKKAPROGRAMEXAMPLEAKKAPROGRAMEXAMPLEAKKAPROGRAM";

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    static public Props props(Long from, Long to) {
        return Props.create(MediaWorker.class, () -> new MediaWorker(from, to));
    }

    private final Long from;
    private final Long to;

    public MediaWorker(Long from, Long to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    switch(s) {
                        case "convert":
                            log.info(text.substring(from.intValue(), to.intValue()));
                            getSender().tell(text.substring(from.intValue(), to.intValue()), ActorRef.noSender());
                            break;
                    }
                })
                .build();
    }
}
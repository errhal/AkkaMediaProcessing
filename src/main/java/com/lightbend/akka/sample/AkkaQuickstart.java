package com.lightbend.akka.sample;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AkkaQuickstart {

    public static List<ActorRef> actors;

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("helloakka");
        try {

            actors = new ArrayList<>();
            for (Long i = 0L; i < 3; i++) {
                actors.add(system.actorOf(MediaWorker.props(), "actor_number_" + i));
            }

            ActorRef master = system.actorOf(MediaMaster.props(), "actor_master");
            master.tell("partial_distribute", ActorRef.noSender());

            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (Exception ioe) {
        } finally {
            system.terminate();
        }
    }
}

package matheus.ismael.distributed;

import matheus.ismael.distributed.messages.server.*;
import org.jgroups.*;
import org.jgroups.util.MessageBatch;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class TupleSpaceReceiver implements Receiver, Runnable {
    ArrayList<String> tuple;
    Lock lock;
    Condition condition;
    ArrayList<ArrayList<String>> tuples;

    TupleSpaceReceiver(ArrayList<String> tuple, Lock lock, Condition condition, ArrayList<ArrayList<String>> tuples) throws Exception {
        this.tuple = tuple;
        this.lock = lock;
        this.condition = condition;
        this.tuples = tuples;
    }

    @Override
    public void receive(MessageBatch batch) {
        Object message = batch.stream().iterator().next().getObject();
        if (message instanceof ReturnTuple){
            tuple.clear();
            tuple.addAll(((ReturnTuple)message).getTuple());
            lock.lock();
            condition.signalAll();
            lock.unlock();
        } else if (message instanceof GetListReturnalMessage){
            synchronized (tuples){
                tuples.clear();
                tuples.addAll(((GetListReturnalMessage)message).getTuples());
                lock.lock();
                condition.signalAll();
                lock.unlock();
            }
        }
    }
    @Override
    public void run() {
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

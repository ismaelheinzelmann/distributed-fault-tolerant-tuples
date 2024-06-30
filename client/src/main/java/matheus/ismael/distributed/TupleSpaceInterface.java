package matheus.ismael.distributed;

import matheus.ismael.distributed.messages.server.*;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.ObjectMessage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TupleSpaceInterface {
    private final JChannel clientChannel;
    private final ArrayList<String> tuple = new ArrayList<>();
    private final ArrayList<ArrayList<String>> tupleList = new ArrayList<>();
    private final Lock lock = new ReentrantLock();
    private final Condition returnTupleCondition = lock.newCondition();
    final String SEM_SERVIDORES = "Não há servidores disponíveis.";
    public TupleSpaceInterface() throws Exception {
        InputStream clientInputStream =
                TupleSpaceReceiver.class.getClassLoader().getResourceAsStream("CLIENT.xml");
        if (clientInputStream == null) {
            throw new RuntimeException("CLIENT.xml not found in resources");
        }
        clientChannel = new JChannel(clientInputStream);
        TupleSpaceReceiver tupleSpaceReceiver =
                new TupleSpaceReceiver(tuple, lock, returnTupleCondition, tupleList);
        Thread thread = new Thread(tupleSpaceReceiver);
        thread.start();
        clientChannel.connect("tuple-spaces-client");
        clientChannel.setDiscardOwnMessages(true);
        clientChannel.setReceiver(tupleSpaceReceiver);
    }

    public ArrayList<String> getTuple(ArrayList<String> tupplePattern) throws Exception {
        var server = getServer();
        if (server.isEmpty()) {
            throw new RuntimeException(SEM_SERVIDORES);
        }
        clientChannel.send(new ObjectMessage(server.get(), new GetTupleMessage(tupplePattern)));
        lock.lock();
        returnTupleCondition.await();
        lock.unlock();
        clientChannel.send(new ObjectMessage(null, new GetTupleQueueRemovalMessage()));
        return tuple;
    }

    public ArrayList<String> readTuple(ArrayList<String> tupplePattern) throws Exception {
        var server = getServer();
        if (server.isEmpty()) {
            throw new RuntimeException(SEM_SERVIDORES);
        }
        clientChannel.send(new ObjectMessage(server.get(), new ReadTupleMessage(tupplePattern)));
        lock.lock();
        returnTupleCondition.await();
        lock.unlock();
        return tuple;
    }

    public void writeTuple(ArrayList<String> tupplePattern) throws Exception {
        var server = getServer();
        if (server.isEmpty()) {
            throw new RuntimeException(SEM_SERVIDORES);
        }
        clientChannel.send(new ObjectMessage(server.get(), new WriteTupleMessage(tupplePattern)));
    }

    private Optional<Address> getServer() {
        for (Address address : clientChannel.getView().getMembers()) {
            if (address.toString().startsWith("SERVER")) {
                return Optional.of(address);
            }
        }
        return Optional.empty();
    }

    public ArrayList<ArrayList<String>> listTuples() throws Exception {
        var server = getServer();
        if (server.isEmpty()) {
            throw new RuntimeException(SEM_SERVIDORES);
        }
        clientChannel.send(new ObjectMessage(server.get(), new GetListMessage()));
        lock.lock();
        returnTupleCondition.await();
        lock.unlock();
        return tupleList;
    }

    public void close() {
        clientChannel.close();
    }
}

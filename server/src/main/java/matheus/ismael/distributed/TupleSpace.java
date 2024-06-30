package matheus.ismael.distributed;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.SneakyThrows;
import matheus.ismael.distributed.messages.server.*;
import org.jgroups.*;
import org.jgroups.blocks.locking.LockService;
import org.jgroups.util.MessageBatch;
import org.jgroups.util.Tuple;

public class TupleSpace implements Receiver, Runnable, ChannelListener {
    private final JChannel serverChannel;
    private final JChannel clientChannel;
    private final ArrayList<ArrayList<String>> tupleSpace = new ArrayList<>();
    private final Lock tupleSpaceLock = new ReentrantLock();
    private final Condition stateCondition = tupleSpaceLock.newCondition();
    private final ArrayList<Tuple<String, Address>> getQueue = new ArrayList<>();
    private final LockService lockService;

    TupleSpace() throws Exception {
        InputStream serverInputStream =
                TupleSpace.class.getClassLoader().getResourceAsStream("SERVER.xml");
        if (serverInputStream == null) {
            throw new RuntimeException("SERVER.xml not found in resources");
        }
        serverChannel = new JChannel(serverInputStream);
        serverChannel.setName("SERVER");
        serverChannel.connect("tuple-spaces-server");
        serverChannel.setDiscardOwnMessages(true);
        serverChannel.setReceiver(this);
        lockService = new LockService(serverChannel);
        Lock tupleSpaceLock = lockService.getLock("tuple-spaces");
        tupleSpaceLock.lock();
        tupleSpaceLock.unlock();
        InputStream clientInputStream =
                TupleSpace.class.getClassLoader().getResourceAsStream("CLIENT.xml");
        if (clientInputStream == null) {
            throw new RuntimeException("CLIENT.xml not found in resources");
        }
        clientChannel = new JChannel(clientInputStream);
        clientChannel.setName("SERVER");
        clientChannel.connect("tuple-spaces-client");
        clientChannel.setDiscardOwnMessages(true);
        clientChannel.setReceiver(this);
        if (serverChannel.getView().getCoord() != serverChannel.getAddress()) {
            sendGetStateRequest();
        } else {
            tupleSpaceLock.lock();
            Arrays.stream(StartSalas.values()).forEach(s ->{
                tupleSpace.add(s.sample.getTuple());
            });
            serverChannel.send(new ObjectMessage(null, new StateMessage(tupleSpace, new ArrayList<>())));
            tupleSpaceLock.unlock();
        }
    }

    @SneakyThrows
    @Override
    public void receive(MessageBatch batch) {
        Object message = batch.stream().iterator().next().getObject();
        if (message instanceof GetStateMessage) {
            Lock tupleSpaceLock = lockService.getLock("tuple-spaces");
            tupleSpaceLock.lock();
            synchronized (tupleSpaceLock) {
                ArrayList<ArrayList<String>> temp = new ArrayList<>();
                for (Tuple<String, Address> tuple : getQueue) {
                    ArrayList<String> get = new ArrayList<>();
                    get.add(tuple.getVal1());
                    get.add(tuple.getVal2().toString());
                    temp.add(get);
                }
                serverChannel.send(
                        new ObjectMessage(batch.getSender(), new StateMessage(tupleSpace, temp)));
            }
            tupleSpaceLock.unlock();
        } else if (message instanceof RemoveTuple) {
            synchronized (tupleSpaceLock) {
                tupleSpace.remove(((RemoveTuple) message).getTuple());
            }
        } else if (message instanceof StoreTuple) {
            synchronized (tupleSpaceLock) {
                var matchingPattern =
                        getMatchingPattern(String.join(",", ((StoreTuple) message).getTuple()));
                if (matchingPattern.isPresent()) {
                    getQueue.remove(matchingPattern.get());
                    clientChannel.send(
                            new ObjectMessage(
                                    matchingPattern.get().getVal2(),
                                    new ReturnTuple(((StoreTuple) message).getTuple())));
                } else {
                    if (!tupleSpace.contains(((StoreTuple) message).getTuple())) {
                        tupleSpace.add(((StoreTuple) message).getTuple());
                    }
                }
            }
        } else if (message instanceof StateMessage) {
            tupleSpaceLock.lock();
            tupleSpace.clear();
            tupleSpace.addAll(((StateMessage) message).getTuples());
            for (ArrayList<String> tuple : ((StateMessage) message).getGetRequests()) {
                Address address = clientChannel.getView().getMembers().stream().filter(f -> f.toString().equals(tuple.get(1))).findFirst().get();
                getQueue.add(new Tuple<>(tuple.get(0), address));
            }
            stateCondition.signalAll();
            tupleSpaceLock.unlock();
        } else if (message instanceof GetTupleQueueMessage) {
            for (Address address : clientChannel.getView().getMembers()) {
                if (address.toString().equals(((GetTupleQueueMessage) message).getAddressName())) {
                    getQueue.add(
                            new Tuple<>(((GetTupleQueueMessage) message).getPattern(), address));
                }
            }
        } else if (message instanceof GetTupleQueueRemovalMessage) {
            getQueue.removeIf(get -> get.getVal2().equals(batch.getSender()));
        } else if (message instanceof GetTupleMessage) {
            synchronized (tupleSpaceLock) {
                Lock tupleSpaceLock = lockService.getLock("tuple-spaces");
                tupleSpaceLock.lock();
                var matchingTuple =
                        getMatchingTuple(String.join(",", ((GetTupleMessage) message).getTuple()));
                if (matchingTuple.isPresent()) {
                    tupleSpace.remove(matchingTuple.get());
                    clientChannel.send(
                            new ObjectMessage(
                                    batch.getSender(), new ReturnTuple(matchingTuple.get())));
                } else {
                    getQueue.add(
                            new Tuple<>(
                                    String.join(",", ((GetTupleMessage) message).getTuple()),
                                    batch.getSender()));
                    serverChannel.send(
                            new ObjectMessage(
                                    null,
                                    new GetTupleQueueMessage(
                                            String.join(
                                                    ",", ((GetTupleMessage) message).getTuple()),
                                            batch.getSender().toString())));
                }
                tupleSpaceLock.unlock();
            }
        } else if (message instanceof WriteTupleMessage) {
            synchronized (tupleSpaceLock) {
                var matchingTuple =
                        getMatchingTuple(
                                String.join(",", ((WriteTupleMessage) message).getTuple()));
                if (matchingTuple.isEmpty()) {
                    var matchingPatter =
                            getMatchingPattern(
                                    String.join(",", ((WriteTupleMessage) message).getTuple()));
                    Lock tupleSpaceLock = lockService.getLock("tuple-spaces");
                    tupleSpaceLock.lock();
                    if (matchingPatter.isPresent()) {
                        getQueue.remove(matchingPatter.get());
                        clientChannel.send(
                                new ObjectMessage(
                                        matchingPatter.get().getVal2(),
                                        new ReturnTuple(((WriteTupleMessage) message).getTuple())));
                    } else {
                        tupleSpace.add(((WriteTupleMessage) message).getTuple());
                        serverChannel.send(
                                new ObjectMessage(
                                        null,
                                        new StoreTuple(((WriteTupleMessage) message).getTuple())));
                    }
                    tupleSpaceLock.unlock();
                }
            }
        } else if (message instanceof ReadTupleMessage) {
            var matchinTuple =
                    getMatchingTuple(String.join(",", (((ReadTupleMessage) message).getTuples())));
            if (matchinTuple.isPresent()) {
                clientChannel.send(
                        new ObjectMessage(batch.getSender(), new ReturnTuple(matchinTuple.get())));
            } else {
                clientChannel.send(
                        new ObjectMessage(batch.getSender(), new ReturnTuple(new ArrayList<>())));
            }
        } else if (message instanceof GetListMessage) {
            Lock tupleSpaceLock = lockService.getLock("tuple-spaces");
            tupleSpaceLock.lock();
            clientChannel.send(
                    new ObjectMessage(batch.getSender(), new GetListReturnalMessage(tupleSpace)));
            tupleSpaceLock.unlock();
        }
        System.out.println("STATE");
        System.out.println(tupleSpace);
        System.out.println(getQueue);
    }

    private void sendGetStateRequest() throws Exception {
        Message message =
                new ObjectMessage(serverChannel.getView().getCoord(), new GetStateMessage());
        serverChannel.send(message);
        Thread.sleep(500);
    }

    Optional<ArrayList<String>> getMatchingTuple(String tuplePattern) {
        String[] pattern = tuplePattern.split(",");
        for (ArrayList<String> tuple : tupleSpace) {
            if (pattern.length != tuple.size()) {
                continue;
            }
            boolean verify = true;
            for (int j = 0; j < pattern.length; j++) {
                if (pattern[j].endsWith("*") && pattern[j].length() == 1) {
                    continue;
                }
                if (!Objects.equals(pattern[j], tuple.get(j))) {
                    verify = false;
                    break;
                }
            }
            if (verify) {
                return Optional.of(tuple);
            }
        }
        return Optional.empty();
    }

    public Optional<Tuple<String, Address>> getMatchingPattern(String tuplePattern) {
        String[] pattern = tuplePattern.split(",");
        for (Tuple<String, Address> tuple : getQueue) {
            String[] getQueuePattern = tuple.getVal1().split(",");
            if (pattern.length != getQueuePattern.length) {
                continue;
            }
            boolean verify = true;
            for (int j = 0; j < pattern.length; j++) {
                if (getQueuePattern[j].endsWith("*") && getQueuePattern[j].length() == 1) {
                    continue;
                }
                if (!Objects.equals(pattern[j], getQueuePattern[j])) {
                    verify = false;
                    break;
                }
            }
            if (verify) {
                return Optional.of(tuple);
            }
        }
        return Optional.empty();
    }

    @Override
    public void run() {
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void viewAccepted(View view) {
        if (!view.getMembers().stream().allMatch(add -> add.toString().equals("SERVER"))){
            getQueue.removeIf(tuple -> !view.containsMember(tuple.getVal2()));
        } else if (clientChannel.getView().getMembers().stream().allMatch(add -> add.toString().equals("SERVER"))){
            getQueue.clear();
        }
    }

}

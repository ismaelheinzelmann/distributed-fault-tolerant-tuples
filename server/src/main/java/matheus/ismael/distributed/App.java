package matheus.ismael.distributed;

public class App {
    public static void main(String[] args) throws Exception {
        TupleSpace tupleSpace = new TupleSpace();
        Thread thread = new Thread(tupleSpace);
        thread.start();
        Thread.onSpinWait();
    }
}

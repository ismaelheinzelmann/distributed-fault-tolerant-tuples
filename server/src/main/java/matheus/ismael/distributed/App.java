package matheus.ismael.distributed;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
public class App {
    public static void main(String[] args) throws Exception {
        TupleSpace tupleSpace = new TupleSpace();
        Thread thread = new Thread(tupleSpace);
        thread.start();
        Thread.onSpinWait();
    }
}

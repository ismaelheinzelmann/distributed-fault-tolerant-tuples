package matheus.ismael.distributed;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {

        TupleSpaceInterface tupleSpaceInterface = new TupleSpaceInterface();
        Scanner reader = new Scanner(System.in);
        System.out.println("Distributed Tuple Space");
        System.out.println("Usage:");
        System.out.println("\tValues separated by a ','");
        System.out.println("\t* is a wildcard");
        while (true) {
            System.out.println();
            System.out.println("Options:");
            for (Option option : Option.values()) {
                System.out.println("-> " + option);
            }
            String input = reader.nextLine();
            Optional<Option> option = Option.findByCode(input);
            if (option.isPresent()) {
                switch (option.get()) {
                    case get -> {
                        try {
                            String tupleInput = reader.nextLine();
                            ArrayList<String> getArray = new ArrayList<>();
                            getArray.addAll(List.of(tupleInput.split(",")));
                            ArrayList<String> test = tupleSpaceInterface.getTuple(getArray);
                            System.out.println(test);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    case write -> {
                        try {
                            String tupleInput = reader.nextLine();
                            ArrayList<String> getArray = new ArrayList<>();
                            getArray.addAll(List.of(tupleInput.split(",")));
                            tupleSpaceInterface.writeTuple(getArray);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    case read -> {
                        String tupleInput = reader.nextLine();
                        ArrayList<String> getArray = new ArrayList<>();
                        getArray.addAll(List.of(tupleInput.split(",")));
                        try {
                            ArrayList<String> test = tupleSpaceInterface.readTuple(getArray);
                            if (test.isEmpty()) {
                                System.out.println("No tuple found");
                            } else {
                                System.out.println(test);
                            }
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    case list -> {
                        try {
                            ArrayList<ArrayList<String>> tuples = tupleSpaceInterface.listTuples();
                            for (ArrayList<String> tuple : tuples) {
                                System.out.println(String.join(",", tuple));
                            }
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    case exit -> {
                        tupleSpaceInterface.close();
                        System.exit(0);
                    }
                }
            }
        }
    }
}

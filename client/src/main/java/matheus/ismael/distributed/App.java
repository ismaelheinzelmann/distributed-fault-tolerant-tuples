package matheus.ismael.distributed;

import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        Sala sala = new Sala();
        TupleSpaceInterface tupleSpaceInterface = new TupleSpaceInterface();
        Scanner reader = new Scanner(System.in);
        System.out.println("Bem vindo ao alocador de salas!");
        while (true) {
            System.out.println();
            System.out.println("Escolha uma opção:");
            Arrays.stream(Option.values()).toList().forEach(op ->{
                System.out.println(op.toString() + op.description);
            });
            String input = reader.nextLine();
            Optional<Option> option = Option.findByCode(input);
            if (option.isPresent()) {
                System.out.println();
                switch (option.get()) {
                    case reservar -> {
                        try {
                            System.out.println(">Para as opções abaixo, insira-as na ordem dividas por espaço.");
                            System.out.println(">As opções podem ser respondidas com <sim/nao/*>.");
                            System.out.println(">Capacidades disponíveis são 25 e 50");
                            System.out.println(">* significa tanto faz.");
                            System.out.println(">Projetor | Ar Condicionado | Computadores | Mesa Compartilhada | Capacidade");
                            var request = getValidated(reader);
                            ArrayList<String> returnTuple = tupleSpaceInterface.getTuple(request);
                            sala = new Sala(returnTuple);
                            System.out.println();
                            System.out.println(">A sala adquirida foi a seguinte:");
                            System.out.println(sala);
                            System.out.println(">Para devolver, aperte Enter!");
                            reader.nextLine();
                            tupleSpaceInterface.writeTuple(returnTuple);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    case registrar -> {
                        System.out.println(">Para os dados abaixo, insira-as na ordem dividas por espaço.");
                        System.out.println(">Os dados podem ser respondidas com <sim/nao/*>.");
                        System.out.println(">Capacidades disponíveis são 25 e 50");
                        System.out.println(">Nome | Projetor | Ar Condicionado | Computadores | Mesa Compartilhada | Capacidade");
                        System.out.println(">Exemplo de entrada: CTC101 sim sim sim sim 25");
                        tupleSpaceInterface.writeTuple(writeValidated(reader));
                    }
                    case verificar -> {
                        System.out.println(">Para os dados abaixo, insira-as na ordem dividas por espaço.");
                        System.out.println(">Os dados podem ser respondidas com <sim/nao/*>.");
                        System.out.println(">Capacidades disponíveis são 25 e 50");
                        System.out.println(">Nome | Projetor | Ar Condicionado | Computadores | Mesa Compartilhada | Capacidade");
                        System.out.println();
                        var tuple = tupleSpaceInterface.readTuple(readValidated(reader));
                        if (tuple.isEmpty()) {
                            System.out.println("Não há salas para o padrão inserido.");
                        } else {
                            var temp = new Sala(tuple);
                            System.out.println(temp);
                        }
                    }
                    case listar -> {
                        var salas = tupleSpaceInterface.listTuples();
                        salas.stream().forEach(s ->{
                            var availableSala = new Sala(s);
                            System.out.println(availableSala);
                        });
                    }
                    case sair -> {
                        tupleSpaceInterface.close();
                        System.exit(0);
                    }
                }
            }
        }
    }

    private static ArrayList<String> getValidated(Scanner reader) {
        String option;
        ArrayList<String> pick = new ArrayList<>();
        boolean picking = true;
        while (picking) {
            picking = false;
            option = reader.nextLine();
            pick.clear();
            pick.addAll(List.of(option.split(" ")));
            for (int i = 0; i < 5; i++) {
                if (i == 4 && !pick.get(i).equals("25") && !pick.get(i).equals("50") && !pick.get(i).equals("*")) {
                    picking = true;
                    System.out.println("Entrada inválida! Digite novamente.");
                    break;
                } else if (i != 4 && !pick.get(i).equals("sim") && !pick.get(i).equals("nao") && !pick.get(i).equals("*")) {
                    picking = true;
                    System.out.println("Entrada inválida! Digite novamente.");
                    break;
                }
            }
        }
        pick.add(0, "*");
        return pick;
    }

    private static ArrayList<String> readValidated(Scanner reader) {
        String option;
        ArrayList<String> pick = new ArrayList<>();
        boolean picking = true;
        while (picking) {
            picking = false;
            option = reader.nextLine();
            pick.clear();
            pick.addAll(List.of(option.split(" ")));
            for (int i = 1; i < 6; i++) {
                if (i == 5 && !pick.get(i).equals("25") && !pick.get(i).equals("50") && !pick.get(i).equals("*")) {
                    picking = true;
                    System.out.println("Entrada inválida! Digite novamente.");
                    break;
                } else if (i != 4 && !pick.get(i).equals("sim") && !pick.get(i).equals("nao") && !pick.get(i).equals("*")) {
                    picking = true;
                    System.out.println("Entrada inválida! Digite novamente.");
                    break;
                }
            }
        }
        return pick;
    }

    private static ArrayList<String> writeValidated(Scanner reader) {
        String option;
        ArrayList<String> pick = new ArrayList<>();
        boolean picking = true;
        while (picking) {
            picking = false;
            option = reader.nextLine();
            pick.clear();
            pick.addAll(List.of(option.split(" ")));
            for (int i = 1; i < 6; i++) {
                if (i == 5 && !pick.get(i).equals("25") && !pick.get(i).equals("50")) {
                    System.out.println("Entrada inválida! Digite novamente.");
                    picking = true;
                    break;
                } else if (i != 5 && !pick.get(i).equals("sim") && !pick.get(i).equals("nao")) {
                    System.out.println("Entrada inválida! Digite novamente.");
                    picking = true;
                    break;
                }

            }
        }
        return pick;
    }
}

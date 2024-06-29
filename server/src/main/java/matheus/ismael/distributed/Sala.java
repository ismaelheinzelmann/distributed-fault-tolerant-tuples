package matheus.ismael.distributed;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Sala {
    private String nome;
    private boolean projetor;
    private boolean arCondicionado;
    private boolean computadores;
    private boolean mesaCompartilhada;
    private boolean capacidade;

    public Sala(ArrayList<String> tuple) {
        nome = tuple.get(0);
        projetor = tuple.get(1).equals("sim");
        arCondicionado = tuple.get(2).equals("sim");
        computadores = tuple.get(3).equals("sim");
        mesaCompartilhada = tuple.get(4).equals("sim");
        capacidade = tuple.get(5).equals("50");
    }

    @Override
    public String toString() {
        return nome.concat(": ").concat(
                (projetor ? "" : "Sem" ) + " Projetor | "
        ).concat(
                (arCondicionado ? "" : "Sem") + " Ar Condicionado | "
        ).concat(
                (computadores ? "" : "Sem") + " Computadores | "
        ).concat(
                ( mesaCompartilhada ? "" : "Sem") + " Mesa Compartilhada | "
        ).concat(
                capacidade ? "50" : "25" + " pessoas"
        );
    }

    public ArrayList<String> getTuple() {
        ArrayList<String> tuple = new ArrayList<>();
        tuple.add(nome);
        tuple.add(projetor ? "sim" : "nao");
        tuple.add(arCondicionado ? "sim" : "nao");
        tuple.add(computadores ? "sim" : "nao");
        tuple.add(mesaCompartilhada ? "sim" : "nao");
        tuple.add(capacidade ? "50" : "25");
        return tuple;
    }
}

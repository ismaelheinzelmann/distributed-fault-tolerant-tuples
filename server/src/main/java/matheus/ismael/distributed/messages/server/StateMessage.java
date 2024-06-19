package matheus.ismael.distributed.messages.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StateMessage implements Serializable {
    private ArrayList<ArrayList<String>> tuples;
}

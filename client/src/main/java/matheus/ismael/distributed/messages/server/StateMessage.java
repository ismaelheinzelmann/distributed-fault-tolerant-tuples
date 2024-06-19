package matheus.ismael.distributed.messages.server;

import java.io.Serializable;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StateMessage implements Serializable {
    private ArrayList<ArrayList<String>> tuples;
}

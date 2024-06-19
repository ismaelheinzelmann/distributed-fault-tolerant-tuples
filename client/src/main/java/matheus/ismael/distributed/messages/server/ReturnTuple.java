package matheus.ismael.distributed.messages.server;

import java.io.Serializable;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReturnTuple implements Serializable {
    private ArrayList<String> tuple;
}

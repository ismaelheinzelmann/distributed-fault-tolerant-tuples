package matheus.ismael.distributed.messages.server;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

@Data
@AllArgsConstructor
public class ReturnTuple implements Serializable {
    private ArrayList<String> tuple;
}

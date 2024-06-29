package matheus.ismael.distributed.messages.server;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

@AllArgsConstructor
@Data
public class StoreTuple implements Serializable {
    private ArrayList<String> tuple;
}

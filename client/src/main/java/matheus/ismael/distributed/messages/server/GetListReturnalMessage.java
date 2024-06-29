package matheus.ismael.distributed.messages.server;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

@Data
@AllArgsConstructor
public class GetListReturnalMessage implements Serializable {
    ArrayList<ArrayList<String>> tuples;
}

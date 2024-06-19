package matheus.ismael.distributed.messages.server;

import java.io.Serializable;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetListReturnalMessage implements Serializable {
    ArrayList<ArrayList<String>> tuples;
}

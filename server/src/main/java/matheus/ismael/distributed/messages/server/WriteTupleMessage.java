package matheus.ismael.distributed.messages.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WriteTupleMessage implements Serializable {
    ArrayList<String> tuple;
}

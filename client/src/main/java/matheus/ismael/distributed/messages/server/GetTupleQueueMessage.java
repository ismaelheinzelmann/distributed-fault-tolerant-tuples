package matheus.ismael.distributed.messages.server;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTupleQueueMessage implements Serializable {
    private String pattern;
    private String addressName;
}

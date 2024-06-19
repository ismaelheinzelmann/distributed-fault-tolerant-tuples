package matheus.ismael.distributed.messages.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jgroups.util.UUID;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTupleQueueMessage implements Serializable {
    private String pattern;
    private String addressName;
}

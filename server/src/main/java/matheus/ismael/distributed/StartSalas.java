package matheus.ismael.distributed;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;

@RequiredArgsConstructor
public enum StartSalas {
    CTC101(new Sala(new ArrayList<>(Arrays.asList("CTC101", "sim", "sim", "nao", "nao","25")))),
    CTC102(new Sala(new ArrayList<>(Arrays.asList("CTC102", "sim", "nao", "nao", "nao","50")))),
    INE101(new Sala(new ArrayList<>(Arrays.asList("INE101", "sim", "sim", "sim", "sim","25")))),
    CTC304(new Sala(new ArrayList<>(Arrays.asList("CTC101", "sim", "sim", "nao", "nao","25")))),
    INE105(new Sala(new ArrayList<>(Arrays.asList("INE105", "sim", "nao", "sim", "sim","25"))));
    final Sala sample;
}

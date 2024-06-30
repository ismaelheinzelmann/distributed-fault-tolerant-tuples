package matheus.ismael.distributed;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

@RequiredArgsConstructor
public enum StartSalas {
    CTC101(new Sala(new ArrayList<>(){{add("CTC101"); add("sim"); add("sim"); add("nao"); add("nao"); add("25");}})),
    CTC102(new Sala(new ArrayList<>(){{add("CTC102"); add("sim"); add("nao"); add("nao"); add("nao"); add("50");}})),
    INE101(new Sala(new ArrayList<>(){{add("INE101"); add("sim"); add("sim"); add("sim"); add("sim"); add("25");}})),
    INE105(new Sala(new ArrayList<>(){{add("INE105"); add("sim"); add("nao"); add("sim"); add("sim"); add("25");}})),
    CTC304(new Sala(new ArrayList<>(){{add("CTC304"); add("nao"); add("nao"); add("nao"); add("sim"); add("50");}})),
    CTC305(new Sala(new ArrayList<>(){{add("CTC305"); add("sim"); add("sim"); add("nao"); add("nao"); add("50");}}));
    final Sala sample;
}

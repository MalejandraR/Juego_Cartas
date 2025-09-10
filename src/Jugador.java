import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Jugador {

    private final int TOTAL_CARTAS = 10;
    private final int SEPARACION = 40;
    private final int MARGEN = 10;
    private Carta[] cartas = new Carta[TOTAL_CARTAS];
    private Random r = new Random();

    public void repartir() {
        for (int i = 0; i < TOTAL_CARTAS; i++) {
            cartas[i] = new Carta(r);
        }
    }

    public void mostrar(JPanel pnl) {
        pnl.removeAll();
        int posicion = MARGEN;
        JLabel[] lblCartas = new JLabel[TOTAL_CARTAS];
        int z = 0;
        for (Carta carta : cartas) {
            lblCartas[z] = carta.mostrar(pnl, posicion, MARGEN);
            posicion += SEPARACION;
            z++;
        }

        z = lblCartas.length - 1;
        for (JLabel lbl : lblCartas) {
            pnl.setComponentZOrder(lbl, z);
            z--;
        }

        pnl.repaint();
    }

    public String getGrupos() {
        String resultado = "";
        boolean hayGrupos = false;
        int puntaje = 0;

        // --- Grupos por nombre (pares, ternas, etc.) ---
        int[] contadores = new int[NombreCarta.values().length];
        for (Carta carta : cartas) {
            contadores[carta.getNombre().ordinal()]++;
        }
        for (int contador : contadores) {
            if (contador >= 2) {
                hayGrupos = true;
                break;
            }
        }
        // --- Grupos encontrados ---
        resultado += "Grupos encontrados:\n";
        if (hayGrupos) {
            for (int p = 0; p < contadores.length; p++) {
                int contador = contadores[p];
                if (contador >= 2) {
                    String tipoGrupo = (contador == 2) ? "Par" : (contador == 3) ? "Terna" : (contador == 4) ? "Cuarta" : "Grupo de " + contador;
                    resultado += tipoGrupo + " de " + NombreCarta.values()[p] + "\n";
                }
            }
        } else {
            resultado += "Ninguno\n";
        }

        // --- Escaleras encontradas ---
        resultado += "\nEscaleras encontradas:\n";
        
        boolean[] usadaGrupo = new boolean[cartas.length];
        boolean[] usadaEscalera = new boolean[cartas.length];
        // Marcar usadas en grupos
        for (int i = 0; i < contadores.length; i++) {
            if (contadores[i] >= 2) {
                int count = 0;
                for (int c = 0; c < cartas.length; c++) {
                    if (!usadaGrupo[c] && cartas[c].getNombre().ordinal() == i) {
                        usadaGrupo[c] = true;
                        count++;
                        if (count == contadores[i]) break;
                    }
                }
            }
        }
        // Detectar y marcar escaleras (independiente de grupos)
        boolean hayEscalera = false;
        for (Pinta pinta : Pinta.values()) {
            int[] indices = new int[cartas.length];
            int idx = 0;
            for (int c = 0; c < cartas.length; c++) {
                if (cartas[c].getPinta() == pinta) {
                    indices[idx++] = c;
                }
            }
            for (int i = 0; i < idx - 1; i++) {
                for (int j = i + 1; j < idx; j++) {
                    if (cartas[indices[i]].getNombre().ordinal() > cartas[indices[j]].getNombre().ordinal()) {
                        int temp = indices[i];
                        indices[i] = indices[j];
                        indices[j] = temp;
                    }
                }
            }
            int inicio = 0;
            while (inicio < idx) {
                int fin = inicio + 1;
                while (fin < idx && cartas[indices[fin]].getNombre().ordinal() == cartas[indices[fin-1]].getNombre().ordinal() + 1) {
                    fin++;
                }
                if (fin - inicio >= 2) {
                    hayEscalera = true;
                    String tipo = (fin - inicio == 2) ? "Par" : (fin - inicio == 3) ? "Terna" : (fin - inicio == 4) ? "Cuarta" : "Escalera";
                    resultado += tipo + " de " + pinta + " de " +
                            NombreCarta.values()[cartas[indices[inicio]].getNombre().ordinal()] + " a " +
                            NombreCarta.values()[cartas[indices[fin-1]].getNombre().ordinal()] + "\n";
                    for (int k = inicio; k < fin; k++) {
                        usadaEscalera[indices[k]] = true;
                    }
                }
                inicio = fin;
            }
        }
        if (!hayEscalera) resultado += "Ninguna\n";

        // --- Cartas Sobrantes y Puntaje ---
        resultado += "\nCartas Sobrantes:\n";
        boolean haySobrantes = false;
        for (int c = 0; c < cartas.length; c++) {
            if (!usadaGrupo[c] && !usadaEscalera[c]) {
                haySobrantes = true;
                resultado += "- " +
                        NombreCarta.values()[cartas[c].getNombre().ordinal()] + " de " +
                        cartas[c].getPinta() + "\n";
                puntaje += valorCarta(cartas[c].getNombre());
            }
        }
        if (!haySobrantes) resultado += "ninguna\n";
        resultado += "\nPuntaje: " + puntaje + "\n";

        return resultado;
    }
    
    // Valor de la carta según reglas
    private int valorCarta(NombreCarta nombre) {
        switch (nombre) {
            case AS:
            case JACK:
            case QUEEN:
            case KING:
                return 10;
            default:
                return nombre.ordinal() + 1;
        }
    }

}

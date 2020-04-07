package mx.unam.ciencias.edd.proyecto2;

import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.IOException;

import mx.unam.ciencias.edd.Lista;
import mx.unam.ciencias.edd.proyecto2.graficadores.GraficadorSVG;

/**
 * Clase que utilizamos para encapsular los métodos correspondientes al
 * procesamiento de la entrada del programa.
 */
public class Entrada {

    /**
     * Constructor privado para evitar instanciación y utilizar los métodos
     * públicos solo de manera estática.
     */
    private Entrada() {  }

    /**
     * Procesa los argumentos recibidos en el programa y lee la entrada, ya sea
     * desde el archivo que se pasó en los argumentos o desde la entrada
     * estándar, y genera el graficador que genera el SVG de la estructura de
     * datos correspondiente.
     * @param args los argumentos con los que inicia el programa.
     * @return el objeto capaz de generar el gráfico SVG.
     */
    public static GraficadorSVG<Integer> procesaEntrada(String[] args) {
        BufferedReader flujoEntrada = abrirEntrada(args);
        Estructura estructuraDeDatos = identificaEstructura(flujoEntrada);

        if (estructuraDeDatos == null || estructuraDeDatos == Estructura.INVALIDO) {
            System.out.println("No se ingresó el nombre de una estructura de " +
                               "datos válida al comienzo de la entrada.");
            System.exit(1);
        }

        Lista<Integer> entrada = leerEntrada(flujoEntrada);
        cierraEntrada(flujoEntrada);

        return FabricaGraficador.<Integer>getGraficadorSVG(entrada, estructuraDeDatos);
    }

    /* Método privado que se encarga de abrir el flujo de entrada. */
    private static BufferedReader abrirEntrada(String[] args) {
        if (args.length != 0)
            try {
                return new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
            } catch (IOException ioe) {
                System.out.printf("Error. El archivo %s no pudo ser leído.\n", args[0]);
                System.exit(1);
            }

        return new BufferedReader(new InputStreamReader(System.in));
    }

    /* Cierra el flujo de entrada. */
    private static void cierraEntrada(BufferedReader entrada) {
        try {
            entrada.close();
        } catch(IOException ioe) { /* Ya no hay nada que hacer. */ }
    }

    /**
     * Método privado para recuperar los elementos en la entrada que componen
     * la estructura de datos.
     */
    private static Lista<Integer> leerEntrada(BufferedReader entrada) {
        Lista<Integer> coleccion = new Lista<>();
        String numero = "";
        int letraInt;

        try {
            while ((letraInt = entrada.read()) != -1) {
                char letra = (char) letraInt;

                if (letra == '#') {
                    entrada.readLine();
                    continue;
                }

                if (letra <= 32) {
                    if (!numero.isEmpty())
                        coleccion.agrega(Integer.parseInt(numero));

                    numero = "";
                } else if (Character.isDigit(letra)) {
                    numero += String.valueOf(letra);
                } else {
                    System.out.printf("El archivo contiene el siguiente caracter no permitido: %c\n", letra);
                    System.exit(1);
                }
            }
        } catch (IOException ioe) {
            System.out.println("Ocurrió un error al leer la entrada.");
            System.exit(1);
        }

        return coleccion;
    }

    /**
     * Método para identificar el nombre de la estructura de datos que será
     * representada.
     */
    private static Estructura identificaEstructura(BufferedReader entrada) {
        String estructuraString = "";
        char letra;

        try {
            while ((letra = (char) entrada.read()) != -1) {
                if (letra == '#') {
                    entrada.readLine();
                    continue;
                }

                if (estructuraString.isEmpty() && letra <= 32)
                    continue;
                else if ((65 <= letra && letra <= 90) || (97 <= letra && letra <= 122))
                    estructuraString += letra;
                else
                    return Estructura.getEstructura(estructuraString);
            }
        } catch (IOException ioe) {
            System.out.println("Hubo un error al leer de la entrada.");
            System.exit(1);
        }

        return null;
    }
}

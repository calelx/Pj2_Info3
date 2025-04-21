/*
	Utilice esta clase para guardar la informacion de su
	AFN. NO DEBE CAMBIAR LOS NOMBRES DE LA CLASE NI DE LOS 
	METODOS que ya existen, sin embargo, usted es libre de 
	agregar los campos y metodos que desee.
*/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AFN {
    Map<Integer, Map<Character, List<Integer>>> transiciones = new HashMap<>();
    Map<Integer, List<Integer>> transicionesLambda = new HashMap<>();
    Set<Integer> estadosFinales = new HashSet<>();
    int estadoInicial;
    Set<Character> alfabeto = new HashSet<>();

    /*
		Implemente el constructor de la clase AFN
		que recibe como argumento un string que 
		representa el path del archivo que contiene
		la informacion del AFN (i.e. "Documentos/archivo.AFN").
		Puede utilizar la estructura de datos que desee
	*/
    public AFN(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            // Leer el alfabeto (primera línea)
            String line = reader.readLine();
            String[] simbolos = line.split(",");
            for (String simbolo : simbolos) {
                alfabeto.add(simbolo.charAt(0));
            }

            // Leer la cantidad de estados (segunda línea)
            line = reader.readLine();
            int cantidadEstados = Integer.parseInt(line);

            // Leer los estados finales (tercera línea)
            line = reader.readLine();
            String[] finales = line.split(",");
            for (String estado : finales) {
                estadosFinales.add(Integer.parseInt(estado));
            }

            // Leer la matriz de transiciones (resto de líneas)
            int fila = 0;
            while ((line = reader.readLine()) != null) {
                String[] transicionesFila = line.split(",");
                for (int columna = 0; columna < transicionesFila.length; columna++) {
                    String[] estadosDestino = transicionesFila[columna].split(";");
                    for (String estadoDestino : estadosDestino) {
                        int estado = Integer.parseInt(estadoDestino);
                        if (fila == 0) { // Transiciones lambda
                            transicionesLambda.computeIfAbsent(columna, k -> new ArrayList<>()).add(estado);
                        } else { // Transiciones normales
                            char simbolo = simbolos[fila - 1].charAt(0);
                            transiciones.computeIfAbsent(columna, k -> new HashMap<>())
                                    .computeIfAbsent(simbolo, k -> new ArrayList<>())
                                    .add(estado);
                        }
                    }
                }
                fila++;
            }

            // Cerrar el lector
            reader.close();

            // Establecer el estado inicial
            estadoInicial = 1; // el estado inicial siempre es 1

        } catch (Exception e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    /*
		Implemente el metodo toAFD. Este metodo debe generar un archivo
		de texto que contenga los datos de un AFD segun las especificaciones
		del proyecto.
	*/
    public boolean accept(String string) {
        return verificarAceptacion(estadoInicial, string, 0, new HashSet<>());
    }

    private boolean verificarAceptacion(int estadoActual, String string, int indice, Set<Integer> visitados) {
     
   // Si hemos consumido toda la cuerda, verificamos si estamos en un estado final
        if (indice == string.length()) {
            return verificarEstadoFinal(estadoActual, string, indice, visitados);
        }

        char simboloActual = string.charAt(indice);

        // Verificar transiciones normales
        if (verificarTransicionesNormales(estadoActual, simboloActual, string, indice)) {
            return true;
        }

        // Verificar transiciones lambda
        if (verificarTransicionesLambda(estadoActual, string, indice, visitados)) {
            return true;
        }

        return false;
    }

    // Función para verificar si estamos en un estado final o si hay transiciones
    // lambda
    private boolean verificarEstadoFinal(int estadoActual, String string, int indice, Set<Integer> visitados) {
        if (estadosFinales.contains(estadoActual)) {
            return true;
        }
        if (transicionesLambda.containsKey(estadoActual)) {
            for (int estadoDestino : transicionesLambda.get(estadoActual)) {
                if (!visitados.contains(estadoDestino)) {
                    visitados.add(estadoDestino);
                    if (verificarAceptacion(estadoDestino, string, indice, visitados)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Función para verificar transiciones normales
    private boolean verificarTransicionesNormales(int estadoActual, char simboloActual, String string, int indice) {
        if (transiciones.containsKey(estadoActual) && transiciones.get(estadoActual).containsKey(simboloActual)) {
            for (int estadoDestino : transiciones.get(estadoActual).get(simboloActual)) {
                if (verificarAceptacion(estadoDestino, string, indice + 1, new HashSet<>())) {
                    return true;
                }
            }
        }
        return false;
    }

    // Función para verificar transiciones lambda
    private boolean verificarTransicionesLambda(int estadoActual, String string, int indice, Set<Integer> visitados) {
        if (transicionesLambda.containsKey(estadoActual)) {
            for (int estadoDestino : transicionesLambda.get(estadoActual)) {
                if (!visitados.contains(estadoDestino)) {
                    visitados.add(estadoDestino);
                    if (verificarAceptacion(estadoDestino, string, indice, visitados)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
    
    /* 
		El metodo main debe recibir como primer argumento el path
		donde se encuentra el archivo ".afd" y debe empezar a evaluar 
		cuerdas ingresadas por el usuario una a una hasta leer una cuerda vacia (""),
		en cuyo caso debe terminar. Tiene la libertad de implementar este metodo
		de la forma que desee. Si se envia la bandera "-to-afd", entonces en vez de
		evaluar, debe generar un archivo .afd
	*/

    public void toAFD(String afdPath) {
        try {
            Map<Set<Integer>, Map<Character, Set<Integer>>> afdTransiciones = new HashMap<>();
            Set<Set<Integer>> estadosAFD = new HashSet<>();
            Set<Set<Integer>> estadosFinalesAFD = new HashSet<>();
            Map<Set<Integer>, Integer> estadoAFDMap = new HashMap<>();
            List<Set<Integer>> estadosPorProcesar = new ArrayList<>();

            Set<Integer> estadoInicialAFD = cerraduraLambda(Set.of(estadoInicial));
            estadosPorProcesar.add(estadoInicialAFD);
            estadosAFD.add(estadoInicialAFD);
            estadoAFDMap.put(estadoInicialAFD, 1);

            int estadoID = 2;

            while (!estadosPorProcesar.isEmpty()) {
                Set<Integer> estadoActual = estadosPorProcesar.remove(0);
                Map<Character, Set<Integer>> transicionesEstado = new HashMap<>();

                for (char simbolo : alfabeto) {
                    Set<Integer> estadosDestino = mover(estadoActual, simbolo);
                    Set<Integer> cerraduraDestino = cerraduraLambda(estadosDestino);

                    if (!cerraduraDestino.isEmpty()) {
                        transicionesEstado.put(simbolo, cerraduraDestino);

                        if (!estadosAFD.contains(cerraduraDestino)) {
                            estadosAFD.add(cerraduraDestino);
                            estadosPorProcesar.add(cerraduraDestino);
                            estadoAFDMap.put(cerraduraDestino, estadoID++);
                        }
                    }
                }

                afdTransiciones.put(estadoActual, transicionesEstado);

                for (int estado : estadoActual) {
                    if (estadosFinales.contains(estado)) {
                        estadosFinalesAFD.add(estadoActual);
                        break;
                    }
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(afdPath))) {
                writer.write(String.join(",", alfabeto.stream().map(String::valueOf).toArray(String[]::new)));
                writer.newLine();
                writer.write(String.valueOf(estadosAFD.size()));
                writer.newLine();
                writer.write(String.join(",", estadosFinalesAFD.stream()
                        .map(estadoAFDMap::get)
                        .map(String::valueOf)
                        .toArray(String[]::new)));
                writer.newLine();

                for (Set<Integer> estado : estadosAFD) {
                    Map<Character, Set<Integer>> transiciones = afdTransiciones.get(estado);
                    for (char simbolo : alfabeto) {
                        Set<Integer> destino = transiciones != null ? transiciones.get(simbolo) : null;
                        writer.write(destino != null ? String.valueOf(estadoAFDMap.get(destino)) : "0");
                        writer.write(",");
                    }
                    writer.newLine();
                }
            }

        } catch (Exception e) {
            System.out.println("Error al convertir el AFN a AFD: " + e.getMessage());
        }
    }

    private Set<Integer> cerraduraLambda(Set<Integer> estados) {
        Set<Integer> cerradura = new HashSet<>(estados);
        List<Integer> porProcesar = new ArrayList<>(estados);

        while (!porProcesar.isEmpty()) {
            int estado = porProcesar.remove(0);
            if (transicionesLambda.containsKey(estado)) {
                for (int destino : transicionesLambda.get(estado)) {
                    if (!cerradura.contains(destino)) {
                        cerradura.add(destino);
                        porProcesar.add(destino);
                    }
                }
            }
        }

        return cerradura;
    }

    private Set<Integer> mover(Set<Integer> estados, char simbolo) {
        Set<Integer> resultado = new HashSet<>();
        for (int estado : estados) {
            if (transiciones.containsKey(estado) && transiciones.get(estado).containsKey(simbolo)) {
                resultado.addAll(transiciones.get(estado).get(simbolo));
            }
        }
        return resultado;
    }



    /*
     * Implemente el metodo main. Este metodo debe recibir como argumento
     * el path del archivo que contiene la informacion del AFN (i.e.
     * "Documentos/archivo.AFN"). Si se recibe un segundo argumento "-to-afd"
     * seguido de un path, el programa debe generar un archivo AFD en el
     * path indicado.
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Uso: java AFN <archivo.afn> [-to-afd <archivo.afd>]");
            return;
        }
    
        String afnPath = args[0];
        AFN afn = new AFN(afnPath);
    
        if (args.length == 3 && "-to-afd".equals(args[1])) {
            generarAFD(afn, args[2]);
        } else {
            evaluarCadenas(afn);
        }
    }
    
    private static void generarAFD(AFN afn, String afdPath) {
        try {
            afn.toAFD(afdPath);
            System.out.println("Archivo AFD generado en: " + afdPath);
        } catch (Exception e) {
            System.out.println("Error al generar el archivo AFD: " + e.getMessage());
        }
    }
    
    private static void evaluarCadenas(AFN afn) {
        try (BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(System.in))) {
            String cuerda;
            System.out.println("Ingrese una cuerda (o una cuerda vacía para salir):");
            while (!(cuerda = reader.readLine()).isEmpty()) {
                if (afn.accept(cuerda)) {
                    System.out.println("Cuerda Aceptada");
                } else {
                    System.out.println("Cuerda Rechazada");
                }
            }
        } catch (Exception e) {
            System.out.println("Error al evaluar las cadenas: " + e.getMessage());
        }
    }
}
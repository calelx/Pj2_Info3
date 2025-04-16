/*
	Utilice esta clase para guardar la informacion de su
	AFN. NO DEBE CAMBIAR LOS NOMBRES DE LA CLASE NI DE LOS 
	METODOS que ya existen, sin embargo, usted es libre de 
	agregar los campos y metodos que desee.
*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AFN{
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
			System.out.print(simbolo + " ");
            alfabeto.add(simbolo.charAt(0));
        }

        // Leer la cantidad de estados (segunda línea)
        line = reader.readLine();
        int cantidadEstados = Integer.parseInt(line);
        System.out.println("Cantidad de estados: " + cantidadEstados);
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
        estadoInicial = 1; // Según el formato, el estado inicial siempre es 1

    } catch (Exception e) {
        System.out.println("Error al leer el archivo: " + e.getMessage());
    }
}

	/*
		Implemente el metodo accept, que recibe como argumento
		un String que representa la cuerda a evaluar, y devuelve
		un boolean dependiendo de si la cuerda es aceptada o no 
		por el AFN. Recuerde lo aprendido en el proyecto 1.
	*/
	public boolean accept(String string){
		return false;
	}

	/*
		Implemente el metodo toAFD. Este metodo debe generar un archivo
		de texto que contenga los datos de un AFD segun las especificaciones
		del proyecto.
	*/
	public void toAFD(String afdPath){
	}

	/*
		El metodo main debe recibir como primer argumento el path
		donde se encuentra el archivo ".afd" y debe empezar a evaluar 
		cuerdas ingresadas por el usuario una a una hasta leer una cuerda vacia (""),
		en cuyo caso debe terminar. Tiene la libertad de implementar este metodo
		de la forma que desee. Si se envia la bandera "-to-afd", entonces en vez de
		evaluar, debe generar un archivo .afd
	*/
	public static void main(String[] args) throws Exception{
		
	}
}
package ar.com.andessalud.andes;

import java.util.ArrayList;

public class datosPrestador {
    private static String _idUsuario, _idConvenio;
    static ArrayList<String> modulosPrestador = new ArrayList<String>();

    public static void guardarIdUsuario(String idUsuario) {
        _idUsuario = idUsuario;
    }

    public static String leerIdUsuario() {
        return _idUsuario;
    }

    public static void guardarIdConvenio(String idCovenio) {
        _idConvenio = idCovenio;
    }

    public static String leerIdConvenio() {
        return _idConvenio;
    }

    public static void inicializaAreaPrestador() {
        modulosPrestador.clear();
    }

    public static void agregarAreaPrestador(String codArea) {
        modulosPrestador.add(codArea);
    }

    public static boolean verificaAreaPrestador(String codArea) {
        for (int i = 0; i < modulosPrestador.size(); ++i) {
            if (modulosPrestador.get(i).toString().equals(codArea)) {
                return true;
            }
        }
        return false;
    }
}

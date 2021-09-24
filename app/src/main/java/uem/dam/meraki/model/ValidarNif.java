package uem.dam.meraki.model;

/**
 * Clase que calcula o valida un documento de identificación del reino de España. (DNI,NIE,CIF).
 * Extraido de "https://github.com/agarimo/Validador/blob/master/src/principal/CalculaNif.java"
 * @author Agárimo
 */
public class ValidarNif {

    private static String strLetrasNif = "TRWAGMYFPDXBNJZSQVHLCKE";
    private static String strLetrasCif = "ABCDEFGHJKLMNPQRSUVW";
    private static String strLetrasNie = "XYZ";
    private static String strDigitoControlCif = "JABCDEFGHI";
    // private final String cifNumero = "ABEH";
    private static String strCifLetra = "KPQRSNW";

    public ValidarNif() {
    }

    /**
     * Calcula el dígito o letra de control de un documento de identificación
     * del reino de España
     *
     * @param strNIF documento a calcular
     * @return devuelve el documento con el dígito o letra de control calculado.
     */
    public String calcular(String strNIF) {
        strNIF = strNIF.toUpperCase();
        String a = strNIF.substring(0, 1);

        if (strLetrasCif.contains(a)) {
            return calculaCif(strNIF);
        } else if (strLetrasNie.contains(a)) {
            return calculaNie(strNIF);
        } else {
            return calculaDni(strNIF);
        }
    }

    /**
     * Valida un documento de identificación del reino de España
     *
     * @param strNIF documento a validar
     * @return true si es válido, false si no lo es.
     */
    public static boolean isOK(String strNIF) {
        if (strNIF.length()!=9) return false;
        strNIF = strNIF.toUpperCase();
        String a = strNIF.substring(0, 1);

        if (strLetrasCif.contains(a)) {
            return isCifValido(strNIF);
        } else if (strLetrasNie.contains(a)) {
            return isNieValido(strNIF);
        } else {
            return isDniValido(strNIF);
        }
    }

    private static String calculaDni(String dni) {
        String str = completaCeros(dni, 8);

        if(str.length()==9){
            str=str.substring(0,dni.length()-1);
        }
        return str + calculaLetra(str);
    }

    private static String calculaNie(String strNIE) {
        String str = null;

        if(strNIE.length()==9){
            strNIE=strNIE.substring(0, strNIE.length()-1);
        }

        if (strNIE.startsWith("X")) {
            str = strNIE.replace('X', '0');
        } else if (strNIE.startsWith("Y")) {
            str = strNIE.replace('Y', '1');
        } else if (strNIE.startsWith("Z")) {
            str = strNIE.replace('Z', '2');
        }

        return strNIE + calculaLetra(str);
    }

    private static String calculaCif(String cif) {
        return cif + calculaDigitoControl(cif);
    }

    private static int posicionImpar(String str) {
        int intAux = Integer.parseInt(str);
        intAux = intAux * 2;
        intAux = (intAux / 10) + (intAux % 10);

        return intAux;
    }

    private static boolean isDniValido(String dni) {
        if (dni.length()<9) return false;
        String strAux = dni.substring(0, 8); //Cojo solo numeros
        strAux = calculaDni(strAux);

        return dni.equals(strAux);
    }

    private static boolean isNieValido(String strNIE) {
        String strAux = strNIE.substring(0, 8);
        strAux = calculaNie(strAux);

        return strNIE.equals(strAux);
    }

    private static boolean isCifValido(String strCIF) {
        String strAux = strCIF.substring(0, 8);
        strAux = calculaCif(strAux);

        return strCIF.equals(strAux);
    }

    private static char calculaLetra(String strAux) {
        return strLetrasNif.charAt(Integer.parseInt(strAux) % 23);
    }

    private static String calculaDigitoControl(String strCIF) {
        String str = strCIF.substring(1, 8);
        String cabecera = strCIF.substring(0, 1);
        int intSumaPar = 0;
        int intSumaImpar = 0;
        int intSumaTotal;

        for (int i = 1; i < str.length(); i += 2) {
            int intAux = Integer.parseInt("" + str.charAt(i));
            intSumaPar += intAux;
        }

        for (int i = 0; i < str.length(); i += 2) {
            intSumaImpar += posicionImpar("" + str.charAt(i));
        }

        intSumaTotal = intSumaPar + intSumaImpar;
        intSumaTotal = 10 - (intSumaTotal % 10);

        if(intSumaTotal==10){
            intSumaTotal=0;
        }

        if (strCifLetra.contains(cabecera)) {
            str = "" + strDigitoControlCif.charAt(intSumaTotal);
        } else {
            str = "" + intSumaTotal;
        }

        return str;
    }

    private static String completaCeros(String str, int intNum) {
        while (str.length() < intNum) {
            str = "0" + str;
        }
        return str;
    }

}

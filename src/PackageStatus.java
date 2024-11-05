public class PackageStatus {
    public static final int ENOFICINA = 0;
    public static final int RECOGIDO = 1;
    public static final int ENCLASIFICACION = 2;
    public static final int DESPACHADO = 3;
    public static final int ENENTREGA = 4;
    public static final int ENTREGADO = 5;
    public static final int DESCONOCIDO = -1;

    public static String getStatus(int status) {
        switch (status) {
            case ENOFICINA: return "EN OFICINA";
            case RECOGIDO: return "RECOGIDO";
            case ENCLASIFICACION: return "EN CLASIFICACIÓN";
            case DESPACHADO: return "DESPACHADO";
            case ENENTREGA: return "EN ENTREGA";
            case ENTREGADO: return "ENTREGADO";
            default: return "DESCONOCIDO";
        }
    }
}

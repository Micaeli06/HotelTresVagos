package ar.com.ada.hoteltresvagos.excepciones;

import ar.com.ada.hoteltresvagos.entities.*;

/**
 * HuespedEdadException
 */
public class HuespedEdadException extends HuespedInfoException {

    public HuespedEdadException(Huesped huesped, String mensaje) {
        super(huesped, mensaje);
    }

}
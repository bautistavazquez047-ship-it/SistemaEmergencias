package com.emergencias.modelo;

/**
 * Se lanza cuando una operación viola una regla del dominio:
 * dato inválido, código duplicado, unidad ocupada o incompatible,
 * cambio de estado inconsistente, etc.
 */
public class OperacionInvalidaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OperacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}

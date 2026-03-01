package com.andreutp.centromasajes.exception;

/**
 * Excepción específica para errores de negocio.
 *
 * Se utiliza en lugar de RuntimeException genérica para mejorar la
 * fiabilidad y permitir un tratamiento más fino en controladores.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

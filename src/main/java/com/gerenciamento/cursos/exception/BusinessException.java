package com.gerenciamento.cursos.exception;

/**
 * Exceção customizada para regras de negócio.
 * Lançada quando uma operação viola regras de negócio do sistema.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

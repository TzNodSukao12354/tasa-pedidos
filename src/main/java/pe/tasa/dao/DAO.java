package pe.tasa.dao;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica DAO — define las operaciones CRUD básicas.
 * T = tipo del modelo (ej: Pedido), ID = tipo de la clave primaria (ej: Integer)
 */
public interface DAO<T, ID> {
    void insertar(T objeto) throws Exception;
    void actualizar(T objeto) throws Exception;
    void eliminar(ID id) throws Exception;
    Optional<T> buscarPorId(ID id) throws Exception;
    List<T> listarTodos() throws Exception;
}
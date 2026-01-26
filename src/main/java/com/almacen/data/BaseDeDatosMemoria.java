package com.almacen.data;

import com.almacen.model.Categoria;
import com.almacen.model.Movimiento;
import com.almacen.model.Producto;
import com.almacen.model.Usuario;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BaseDeDatosMemoria {
    public static List<Usuario> usuarios = new ArrayList<>();
    public static List<Categoria> categorias = new ArrayList<>();
    public static List<Producto> productos = new ArrayList<>();
    public static List<Movimiento> movimientos = new ArrayList<>();

    public static void inicializarDatos() {
        usuarios.add(new Usuario(1L, "admin", "123", "ADMIN"));
        usuarios.add(new Usuario(2L, "user", "123", "USER"));

        Categoria cat1 = new Categoria(1L, "Electrónica");
        Categoria cat2 = new Categoria(2L, "Hogar");
        categorias.add(cat1);
        categorias.add(cat2);

        productos.add(
                new Producto(1L, "Laptop", "Laptop Dell", 10, new BigDecimal("1500.00"), LocalDateTime.now(), cat1));
        productos.add(
                new Producto(2L, "Mouse", "Mouse Inalámbrico", 50, new BigDecimal("20.00"), LocalDateTime.now(), cat1));
        productos.add(
                new Producto(3L, "Silla", "Silla de Oficina", 15, new BigDecimal("100.00"), LocalDateTime.now(), cat2));
    }

    public static Usuario buscarUsuario(String username, String password) {
        for (Usuario u : usuarios) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public static Producto buscarProductoPorId(Long id) {
        for (Producto p : productos) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }
}

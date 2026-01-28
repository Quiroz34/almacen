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

        // Solo agregamos datos de prueba si NO existe el archivo
        java.io.File archivo = new java.io.File(ARCHIVO_PRODUCTOS);
        if (!archivo.exists()) {
            productos.add(
                    new Producto(1L, "Laptop", "Laptop Dell", 10, new BigDecimal("1500.00"), LocalDateTime.now(),
                            cat1));
            productos.add(
                    new Producto(2L, "Mouse", "Mouse Inalámbrico", 50, new BigDecimal("20.00"), LocalDateTime.now(),
                            cat1));
            productos.add(
                    new Producto(3L, "Silla", "Silla de Oficina", 15, new BigDecimal("100.00"), LocalDateTime.now(),
                            cat2));
        }

        cargarDatos();
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

    // base de datos provisional para productos

    private static String ARCHIVO_PRODUCTOS = "productos.txt";

    public static void guardarCambios() {
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(ARCHIVO_PRODUCTOS))) {
            for (Producto p : productos) {
                String linea = String.format("%d;%s;%s;%d;%s;%d",
                        p.getId(),
                        p.getNombre(),
                        p.getDescripcion(),
                        p.getCantidad(),
                        p.getPrecio_unitario().toString(),
                        p.getCategoria().getId());
                writer.write(linea);
                writer.newLine();
            }
        } catch (java.io.IOException e) {
            System.out.println("Error al guardar los datos: " + e.getMessage());
        }
    }

    public static void cargarDatos() {
        java.io.File archivo = new java.io.File(ARCHIVO_PRODUCTOS);
        if (!archivo.exists())
            return;
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(archivo))) {
            String linea;

            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length >= 6) {
                    Producto p = new Producto();
                    p.setId(Long.parseLong(partes[0]));
                    p.setNombre(partes[1]);
                    p.setDescripcion(partes[2]);
                    p.setCantidad(Integer.parseInt(partes[3]));
                    p.setPrecio_unitario(new BigDecimal(partes[4]));
                    p.setFecha_registro(LocalDateTime.now());

                    Long catId = Long.parseLong(partes[5]);
                    Categoria cat = categorias.stream()
                            .filter(c -> c.getId().equals(catId)).findFirst().orElse(null);
                    p.setCategoria(cat);

                    boolean existe = productos.stream().anyMatch(existing -> existing.getId().equals(p.getId()));
                    if (!existe) {
                        productos.add(p);
                    }
                }
            }
        } catch (java.io.IOException e) {
            System.out.println("Error al leer los datos: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error en el formato de los datos: " + e.getMessage());
        }
    }
}

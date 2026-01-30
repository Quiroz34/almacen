package com.almacen.data;

import com.almacen.model.Categoria;
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

    public static void inicializarDatos() {
        cargarUsuarios();

        if (usuarios.isEmpty()) {
            usuarios.add(new Usuario(1L, "admin", "123", "ADMIN"));
            usuarios.add(new Usuario(2L, "user", "123", "USER"));
            guardarUsuarios();
        }

        cargarCategorias();

        if (categorias.isEmpty()) {
            Categoria cat1 = new Categoria(1L, "Electrónica");
            Categoria cat2 = new Categoria(2L, "Hogar");
            categorias.add(cat1);
            categorias.add(cat2);
            guardarCategorias();
        }

        java.io.File archivo = new java.io.File(ARCHIVO_PRODUCTOS);
        if (!archivo.exists()) {
            Categoria cat1 = categorias.stream().filter(c -> c.getNombre().equals("Electrónica")).findFirst()
                    .orElse(null);
            Categoria cat2 = categorias.stream().filter(c -> c.getNombre().equals("Hogar")).findFirst().orElse(null);

            if (cat1 != null && cat2 != null) {
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

    public static Usuario buscarUsuarioPorId(Long id) {
        for (Usuario u : usuarios) {
            if (u.getId().equals(id)) {
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

    public static Categoria buscarCategoriaPorId(Long id) {
        for (Categoria c : categorias) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    private static String ARCHIVO_PRODUCTOS = "productos.txt";
    private static String ARCHIVO_USUARIOS = "usuarios.txt";
    private static String ARCHIVO_CATEGORIAS = "categorias.txt";

    public static void guardarUsuarios() {
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(ARCHIVO_USUARIOS))) {
            for (Usuario u : usuarios) {
                String linea = String.format("%d;%s;%s;%s",
                        u.getId(),
                        u.getUsername(),
                        u.getPassword(),
                        u.getRol());
                writer.write(linea);
                writer.newLine();
            }
        } catch (java.io.IOException e) {
            System.out.println("Error al guardar usuarios: " + e.getMessage());
        }
    }

    public static void cargarUsuarios() {
        java.io.File archivo = new java.io.File(ARCHIVO_USUARIOS);
        if (!archivo.exists()) {
            return;
        }
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(archivo))) {
            String linea;
            usuarios.clear();
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length >= 4) {
                    Usuario u = new Usuario();
                    u.setId(Long.parseLong(partes[0]));
                    u.setUsername(partes[1]);
                    u.setPassword(partes[2]);
                    u.setRol(partes[3]);
                    usuarios.add(u);
                }
            }
        } catch (java.io.IOException e) {
            System.out.println("Error al leer usuarios: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error de formato en usuarios: " + e.getMessage());
        }
    }

    public static void agregarUsuario(Usuario u) {
        usuarios.add(u);
        guardarUsuarios();
    }

    public static void eliminarUsuario(Usuario u) {
        usuarios.remove(u);
        guardarUsuarios();
    }

    public static void actualizarUsuario(Usuario u) {
        guardarUsuarios();
    }

    public static void guardarCategorias() {
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(ARCHIVO_CATEGORIAS))) {
            for (Categoria c : categorias) {
                String linea = String.format("%d;%s", c.getId(), c.getNombre());
                writer.write(linea);
                writer.newLine();
            }
        } catch (java.io.IOException e) {
            System.out.println("Error al guardar categorías: " + e.getMessage());
        }
    }

    public static void cargarCategorias() {
        java.io.File archivo = new java.io.File(ARCHIVO_CATEGORIAS);
        if (!archivo.exists()) {
            return;
        }
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(archivo))) {
            String linea;
            categorias.clear();
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length >= 2) {
                    Categoria c = new Categoria();
                    c.setId(Long.parseLong(partes[0]));
                    c.setNombre(partes[1]);
                    categorias.add(c);
                }
            }
        } catch (java.io.IOException e) {
            System.out.println("Error al leer categorías: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error de formato en categorías: " + e.getMessage());
        }
    }

    public static void agregarCategoria(Categoria c) {
        categorias.add(c);
        guardarCategorias();
    }

    public static void eliminarCategoria(Categoria c) {
        categorias.remove(c);
        guardarCategorias();
    }

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

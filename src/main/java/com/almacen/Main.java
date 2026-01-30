package com.almacen;

import com.almacen.data.BaseDeDatosMemoria;
import com.almacen.model.Categoria;
import com.almacen.model.Producto;
import com.almacen.model.Usuario;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static Usuario usuarioLogueado = null;

    public static void main(String[] args) {
        BaseDeDatosMemoria.inicializarDatos();
        System.out.println("Sistema de Almacén Iniciado...");

        while (true) {
            try {
                if (usuarioLogueado == null) {
                    login();
                } else {
                    menuPrincipal();
                }
            } catch (Exception e) {
                System.out.println("Ocurrió un error inesperado o se cerró la entrada: " + e.getMessage());
                break;
            }
        }
    }

    private static void login() {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Usuario: ");
        if (!scanner.hasNextLine())
            return;
        String user = scanner.nextLine();

        System.out.print("Password: ");
        if (!scanner.hasNextLine())
            return;
        String pass = scanner.nextLine();

        Usuario u = BaseDeDatosMemoria.buscarUsuario(user, pass);
        if (u != null) {
            usuarioLogueado = u;
            System.out.println("Bienvenido " + u.getUsername() + " (" + u.getRol() + ")");
        } else {
            System.out.println("Credenciales incorrectas.");
        }
    }

    private static void menuPrincipal() {
        System.out.println("\n--- MENÚ PRINCIPAL ---");
        System.out.println("1. Listar Productos");
        System.out.println("2. Registrar Movimiento");
        System.out.println("3. Ver Reporte de Inventario");

        if ("ADMIN".equals(usuarioLogueado.getRol())) {
            System.out.println("4. Agregar Producto");
            System.out.println("5. Editar Producto");
            System.out.println("6. Eliminar Producto");
            System.out.println("7. Gestionar Categorías");
            System.out.println("8. Gestionar Usuarios");
        }
        System.out.println("9. Salir");
        System.out.print("Opción: ");

        if (!scanner.hasNextLine()) {
            usuarioLogueado = null;
            return;
        }
        String opcion = scanner.nextLine();
        switch (opcion) {
            case "1":
                listarProductos();
                break;
            case "2":
                registrarMovimiento();
                break;
            case "3":
                verReporte();
                break;
            case "4":
                if ("ADMIN".equals(usuarioLogueado.getRol())) {
                    agregarProducto();
                }
                break;
            case "5":
                if ("ADMIN".equals(usuarioLogueado.getRol())) {
                    editarProducto();
                }
                break;
            case "6":
                if ("ADMIN".equals(usuarioLogueado.getRol())) {
                    eliminarProducto();
                }
                break;
            case "7":
                if ("ADMIN".equals(usuarioLogueado.getRol())) {
                    gestionarCategorias();
                }
                break;
            case "8":
                if ("ADMIN".equals(usuarioLogueado.getRol())) {
                    gestionarUsuarios();
                }
                break;
            case "9":
                usuarioLogueado = null;
                System.out.println("Sesión cerrada.");
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }

    private static void listarProductos() {
        System.out.println("\n--- LISTA DE PRODUCTOS ---");
        System.out.printf("%-5s %-20s %-15s %-10s %-10s%n", "ID", "Nombre", "Categoría", "Stock", "Precio");
        for (Producto p : BaseDeDatosMemoria.productos) {
            System.out.printf("%-5d %-20s %-15s %-10d %-10.2f%n",
                    p.getId(),
                    p.getNombre(),
                    p.getCategoria().getNombre(),
                    p.getCantidad(),
                    p.getPrecio_unitario());
        }
    }

    private static void agregarProducto() {
        System.out.println("\n--- AGREGAR PRODUCTO ---");
        System.out.print("Nombre: ");
        if (!scanner.hasNextLine())
            return;
        String nombre = scanner.nextLine();

        System.out.print("Descripción: ");
        if (!scanner.hasNextLine())
            return;
        String desc = scanner.nextLine();

        System.out.print("Precio Unitario: ");
        if (!scanner.hasNextLine())
            return;
        try {
            BigDecimal precio = new BigDecimal(scanner.nextLine());

            System.out.println("Seleccione Categoría (ID):");
            listarCategorias();
            if (!scanner.hasNextLine())
                return;
            Long catId = Long.parseLong(scanner.nextLine());
            Categoria catSeleccionada = BaseDeDatosMemoria.categorias.stream()
                    .filter(c -> c.getId().equals(catId)).findFirst().orElse(null);

            if (catSeleccionada != null) {
                Producto nuevo = new Producto();
                nuevo.setId((long) (BaseDeDatosMemoria.productos.size() + 1));
                nuevo.setNombre(nombre);
                nuevo.setDescripcion(desc);
                nuevo.setCantidad(0);
                nuevo.setPrecio_unitario(precio);
                nuevo.setFecha_registro(LocalDateTime.now());
                nuevo.setCategoria(catSeleccionada);

                BaseDeDatosMemoria.productos.add(nuevo);
                BaseDeDatosMemoria.guardarCambios();
                System.out.println("Producto agregado exitosamente.");
            } else {
                System.out.println("Categoría no válida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Formato de número inválido.");
        }
    }

    private static void registrarMovimiento() {
        System.out.println("\n--- REGISTRAR MOVIMIENTO ---");
        listarProductos();
        System.out.print("ID del Producto: ");
        if (!scanner.hasNextLine())
            return;

        try {
            Long idProd = Long.parseLong(scanner.nextLine());
            Producto p = BaseDeDatosMemoria.buscarProductoPorId(idProd);

            if (p == null) {
                System.out.println("Producto no encontrado.");
                return;
            }

            System.out.println("Producto seleccionado: " + p.getNombre() + " (Stock actual: " + p.getCantidad() + ")");
            System.out.print("Tipo (E para Entrada / S para Salida): ");
            if (!scanner.hasNextLine())
                return;
            String tipo = scanner.nextLine().toUpperCase();

            System.out.print("Cantidad: ");
            if (!scanner.hasNextLine())
                return;
            int cantidad = Integer.parseInt(scanner.nextLine());

            if (cantidad <= 0) {
                System.out.println("La cantidad debe ser mayor a 0.");
                return;
            }

            if ("S".equals(tipo)) {
                if (p.getCantidad() < cantidad) {
                    System.out.println("ERROR: Stock insuficiente. Solo hay " + p.getCantidad());
                    return;
                }
                p.setCantidad(p.getCantidad() - cantidad);
            } else if ("E".equals(tipo)) {
                p.setCantidad(p.getCantidad() + cantidad);
            } else {
                System.out.println("Tipo de movimiento no válido.");
                return;
            }

            BaseDeDatosMemoria.guardarCambios();
            System.out.println("Movimiento registrado. Nuevo stock: " + p.getCantidad());
        } catch (NumberFormatException e) {
            System.out.println("Error: Debe ingresar un número válido.");
        }
    }

    private static void verReporte() {
        System.out.println("\n--- REPORTE DE INVENTARIO ---");
        System.out.printf("%-15s %-15s %-10s %-10s %-10s%n", "Producto", "Categoría", "Cant", "Precio U.", "Total");
        System.out.println("---------------------------------------------------------------");

        BigDecimal granTotal = BigDecimal.ZERO;

        for (Producto p : BaseDeDatosMemoria.productos) {
            BigDecimal totalLinea = p.getPrecio_unitario().multiply(new BigDecimal(p.getCantidad()));
            granTotal = granTotal.add(totalLinea);

            System.out.printf("%-15s %-15s %-10d %-10.2f %-10.2f%n",
                    p.getNombre(),
                    p.getCategoria().getNombre(),
                    p.getCantidad(),
                    p.getPrecio_unitario(),
                    totalLinea);
        }
        System.out.println("---------------------------------------------------------------");
        System.out.printf("VALOR TOTAL DEL INVENTARIO: %30.2f%n", granTotal);
    }

    // editar y eliminar productos

    private static void editarProducto() {
        System.out.println("\n--- EDITAR PRODUCTO ---");
        listarProductos();
        System.out.print("Ingrese el ID del producto que desea editar: ");

        if (!scanner.hasNextLine())
            return;
        try {
            Long id = Long.parseLong(scanner.nextLine());
            Producto p = BaseDeDatosMemoria.buscarProductoPorId(id);

            if (p != null) {
                System.out.println("Nombre actual: " + p.getNombre());
                System.out.print("Nuevo nombre (Enter para mantener): ");
                String nuevoNombre = scanner.nextLine();
                if (!nuevoNombre.isEmpty()) {
                    p.setNombre(nuevoNombre);
                }

                System.out.println("Descripción actual: " + p.getDescripcion());
                System.out.print("Nueva descripción (Enter para mantener): ");
                String nuevaDesc = scanner.nextLine();
                if (!nuevaDesc.isEmpty()) {
                    p.setDescripcion(nuevaDesc);
                }

                System.out.println("Precio actual: " + p.getPrecio_unitario());
                System.out.print("Nuevo precio (Enter para mantener): ");
                String nuevoPrecioStr = scanner.nextLine();
                if (!nuevoPrecioStr.isEmpty()) {
                    try {
                        p.setPrecio_unitario(new BigDecimal(nuevoPrecioStr));
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Precio inválido. No se actualizó el precio.");
                    }
                }

                System.out.println("Categoría actual: " + p.getCategoria().getNombre());
                System.out.println("Seleccione nueva Categoría (ID) (Enter para mantener):");
                listarCategorias();
                if (scanner.hasNextLine()) {
                    String nuevaCatStr = scanner.nextLine();
                    if (!nuevaCatStr.isEmpty()) {
                        try {
                            Long newCatId = Long.parseLong(nuevaCatStr);
                            Categoria newCat = BaseDeDatosMemoria.categorias.stream()
                                    .filter(c -> c.getId().equals(newCatId)).findFirst().orElse(null);
                            if (newCat != null) {
                                p.setCategoria(newCat);
                            } else {
                                System.out.println("Categoría no encontrada. No se actualizó la categoría.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("ID inválido. No se actualizó la categoría.");
                        }
                    }
                }

                BaseDeDatosMemoria.guardarCambios();
                System.out.println("El producto se actualizó correctamente.");
            } else {
                System.out.println("Producto no encontrado.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: ID no valido.");
        }
    }

    private static void eliminarProducto() {
        System.out.println("\n--- ELIMINAR PRODUCTO ---");
        listarProductos();
        System.out.print("Ingresa el ID del producto a eliminar: ");

        if (!scanner.hasNextLine())
            return;
        try {
            Long id = Long.parseLong(scanner.nextLine());
            Producto p = BaseDeDatosMemoria.buscarProductoPorId(id);

            if (p != null) {
                System.out.println("Eliminar permanentemente: " + p.getNombre());
                System.out.print("¿Está seguro? (S/N): ");
                String confirma = scanner.nextLine();

                if (confirma.equalsIgnoreCase("S")) {
                    BaseDeDatosMemoria.productos.remove(p);
                    BaseDeDatosMemoria.guardarCambios();
                    System.out.println("Producto eliminado.");
                } else {
                    System.out.println("Cancelada.");
                }
            } else {
                System.out.println("No se encontro ese producto con ese ID.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: ID no valido.");
        }
    }

    private static void gestionarUsuarios() {
        while (true) {
            System.out.println("\n--- GESTIÓN DE USUARIOS ---");
            System.out.println("1. Listar Usuarios");
            System.out.println("2. Agregar Usuario");
            System.out.println("3. Editar Usuario");
            System.out.println("4. Eliminar Usuario");
            System.out.println("5. Volver al Menú Principal");
            System.out.print("Opción: ");

            if (!scanner.hasNextLine())
                return;
            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    listarUsuarios();
                    break;
                case "2":
                    agregarUsuario();
                    break;
                case "3":
                    editarUsuario();
                    break;
                case "4":
                    eliminarUsuario();
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private static void listarUsuarios() {
        System.out.println("\n--- LISTA DE USUARIOS ---");
        System.out.printf("%-5s %-15s %-10s%n", "ID", "Username", "Rol");
        for (Usuario u : BaseDeDatosMemoria.usuarios) {
            System.out.printf("%-5d %-15s %-10s%n",
                    u.getId(),
                    u.getUsername(),
                    u.getRol());
        }
    }

    private static void agregarUsuario() {
        System.out.println("\n--- AGREGAR USUARIO ---");
        System.out.print("Username: ");
        if (!scanner.hasNextLine())
            return;
        String username = scanner.nextLine();

        if (BaseDeDatosMemoria.usuarios.stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(username))) {
            System.out.println("Error: El usuario ya existe.");
            return;
        }

        System.out.print("Password: ");
        if (!scanner.hasNextLine())
            return;
        String password = scanner.nextLine();

        System.out.print("Rol (ADMIN/USER): ");
        if (!scanner.hasNextLine())
            return;
        String rol = scanner.nextLine().toUpperCase();

        if (!rol.equals("ADMIN") && !rol.equals("USER")) {
            System.out.println("Rol inválido. Se asignará USER por defecto.");
            rol = "USER";
        }

        Usuario nuevo = new Usuario();
        long maxId = BaseDeDatosMemoria.usuarios.stream().mapToLong(Usuario::getId).max().orElse(0);
        nuevo.setId(maxId + 1);
        nuevo.setUsername(username);
        nuevo.setPassword(password);
        nuevo.setRol(rol);

        BaseDeDatosMemoria.agregarUsuario(nuevo);
        System.out.println("Usuario agregado exitosamente.");
    }

    private static void editarUsuario() {
        System.out.println("\n--- EDITAR USUARIO ---");
        listarUsuarios();
        System.out.print("Ingrese ID del usuario a editar: ");
        if (!scanner.hasNextLine())
            return;

        try {
            Long id = Long.parseLong(scanner.nextLine());
            Usuario u = BaseDeDatosMemoria.buscarUsuarioPorId(id);

            if (u != null) {
                System.out.println("Editando usuario: " + u.getUsername());

                System.out.print("Nuevo Password (Enter para mantener): ");
                String newPass = scanner.nextLine();
                if (!newPass.isEmpty()) {
                    u.setPassword(newPass);
                }

                System.out.print("Nuevo Rol (ADMIN/USER) (Enter para mantener): ");
                String newRol = scanner.nextLine().toUpperCase();
                if (!newRol.isEmpty()) {
                    if (newRol.equals("ADMIN") || newRol.equals("USER")) {
                        u.setRol(newRol);
                    } else {
                        System.out.println("Rol inválido, se mantiene el actual.");
                    }
                }

                BaseDeDatosMemoria.actualizarUsuario(u);
                System.out.println("Usuario actualizado.");
            } else {
                System.out.println("Usuario no encontrado.");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
        }
    }

    private static void eliminarUsuario() {
        System.out.println("\n--- ELIMINAR USUARIO ---");
        listarUsuarios();
        System.out.print("Ingrese ID del usuario a eliminar: ");
        if (!scanner.hasNextLine())
            return;

        try {
            Long id = Long.parseLong(scanner.nextLine());
            Usuario u = BaseDeDatosMemoria.buscarUsuarioPorId(id);

            if (u != null) {
                if (u.getId().equals(usuarioLogueado.getId())) {
                    System.out.println("No puedes eliminar tu propio usuario mientras estás logueado.");
                    return;
                }

                System.out.print("¿Seguro que desea eliminar a " + u.getUsername() + "? (S/N): ");
                String resp = scanner.nextLine();
                if (resp.equalsIgnoreCase("S")) {
                    BaseDeDatosMemoria.eliminarUsuario(u);
                    System.out.println("Usuario eliminado.");
                }
            } else {
                System.out.println("Usuario no encontrado.");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
        }
    }

    private static void gestionarCategorias() {
        while (true) {
            System.out.println("\n--- GESTIÓN DE CATEGORÍAS ---");
            System.out.println("1. Listar Categorías");
            System.out.println("2. Agregar Categoría");
            System.out.println("3. Eliminar Categoría");
            System.out.println("4. Volver al Menú Principal");
            System.out.print("Opción: ");

            if (!scanner.hasNextLine())
                return;
            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    listarCategorias();
                    break;
                case "2":
                    agregarCategoria();
                    break;
                case "3":
                    eliminarCategoria();
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private static void listarCategorias() {
        System.out.println("\n--- LISTA DE CATEGORÍAS ---");
        if (BaseDeDatosMemoria.categorias.isEmpty()) {
            System.out.println("No hay categorías registradas.");
        } else {
            for (Categoria c : BaseDeDatosMemoria.categorias) {
                System.out.println(c.getId() + ". " + c.getNombre());
            }
        }
    }

    private static void agregarCategoria() {
        System.out.println("\n--- AGREGAR CATEGORÍA ---");
        System.out.print("Nombre de la categoría: ");
        if (!scanner.hasNextLine())
            return;
        String nombre = scanner.nextLine();

        if (nombre.isEmpty()) {
            System.out.println("El nombre no puede estar vacío.");
            return;
        }

        if (BaseDeDatosMemoria.categorias.stream().anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre))) {
            System.out.println("Error: Ya existe una categoría con ese nombre.");
            return;
        }

        long maxId = BaseDeDatosMemoria.categorias.stream().mapToLong(Categoria::getId).max().orElse(0);
        Categoria nueva = new Categoria(maxId + 1, nombre);
        BaseDeDatosMemoria.agregarCategoria(nueva);
        System.out.println("Categoría agregada exitosamente.");
    }

    private static void eliminarCategoria() {
        System.out.println("\n--- ELIMINAR CATEGORÍA ---");
        listarCategorias();
        System.out.print("Ingrese ID de la categoría a eliminar: ");
        if (!scanner.hasNextLine())
            return;

        try {
            Long id = Long.parseLong(scanner.nextLine());
            Categoria c = BaseDeDatosMemoria.buscarCategoriaPorId(id);

            if (c != null) {
                boolean enUso = BaseDeDatosMemoria.productos.stream()
                        .anyMatch(p -> p.getCategoria().getId().equals(c.getId()));

                if (enUso) {
                    System.out
                            .println("Error: No se puede eliminar la categoría porque hay productos asociados a ella.");
                    return;
                }

                System.out.print("¿Seguro que desea eliminar '" + c.getNombre() + "'? (S/N): ");
                String resp = scanner.nextLine();
                if (resp.equalsIgnoreCase("S")) {
                    BaseDeDatosMemoria.eliminarCategoria(c);
                    System.out.println("Categoría eliminada.");
                }
            } else {
                System.out.println("Categoría no encontrada.");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
        }
    }
}

package com.almacen;

import com.almacen.data.BaseDeDatosMemoria;
import com.almacen.model.Categoria;
import com.almacen.model.Movimiento;
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
        if ("ADMIN".equals(usuarioLogueado.getRol())) {
            System.out.println("2. Agregar Producto");
        }
        System.out.println("3. Registrar Movimiento");
        System.out.println("4. Ver Reporte de Inventario");
        System.out.println("5. Salir");
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
                if ("ADMIN".equals(usuarioLogueado.getRol())) {
                    agregarProducto();
                } else {
                    System.out.println("Opción no válida.");
                }
                break;
            case "3":
                registrarMovimiento();
                break;
            case "4":
                verReporte();
                break;
            case "5":
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
            for (Categoria c : BaseDeDatosMemoria.categorias) {
                System.out.println(c.getId() + ". " + c.getNombre());
            }
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
                tipo = "SALIDA";
            } else if ("E".equals(tipo)) {
                p.setCantidad(p.getCantidad() + cantidad);
                tipo = "ENTRADA";
            } else {
                System.out.println("Tipo de movimiento no válido.");
                return;
            }

            Movimiento mov = new Movimiento();
            mov.setId((long) (BaseDeDatosMemoria.movimientos.size() + 1));
            mov.setProducto(p);
            mov.setTipo_movimiento(tipo);
            mov.setCantidad(cantidad);
            mov.setFecha_movimiento(LocalDateTime.now());
            mov.setUsuario(usuarioLogueado);

            BaseDeDatosMemoria.movimientos.add(mov);
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
}

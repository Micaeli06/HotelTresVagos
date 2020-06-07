package ar.com.ada.hoteltresvagos;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.hibernate.exception.ConstraintViolationException;

import ar.com.ada.hoteltresvagos.entities.*;
import ar.com.ada.hoteltresvagos.excepciones.*;
import ar.com.ada.hoteltresvagos.managers.*;

public class ABM {

    public static Scanner Teclado = new Scanner(System.in);

    protected HuespedManager ABMHuesped = new HuespedManager();
    protected ReservaManager ABMReserva = new ReservaManager();

    public void iniciar() throws Exception {

        try {

            ABMHuesped.setup();
            ABMReserva.setup();

            printOpciones();

            int opcion = Teclado.nextInt();
            Teclado.nextLine();

            while (opcion > 0) {

                if (opcion == 1) {

                    printOpcionHuesped();
                    opcion = Teclado.nextInt();
                    Teclado.nextLine();

                    switch (opcion) {
                        case 1:

                            try {
                                alta();
                            } catch (HuespedDNIException exdni) {
                                System.out.println("Error en el DNI. Indique uno valido");
                            }
                            break;

                        case 2:
                            baja();
                            break;

                        case 3:
                            modifica();
                            break;

                        case 4:
                            listar();
                            break;

                        case 5:
                            listarPorNombre();
                            break;

                        default:
                            System.out.println("La opcion no es correcta.");
                            break;
                    }

                } else if (opcion == 2) {

                    printOpcionReserva();
                    opcion = Teclado.nextInt();
                    Teclado.nextLine();

                    switch (opcion) {
                        case 1:
                            altaReserva();
                            break;

                        case 2:
                            bajaReserva();
                            break;

                        case 3:
                            modificarReserva();
                            break;

                        case 4:
                            listarReservas();
                            break;

                        case 5:
                            listarPorNombreHuesped();
                            break;

                        default:
                            System.out.println("La opcion no es correcta.");
                            break;
                    }

                }

                printOpciones();

                opcion = Teclado.nextInt();
                Teclado.nextLine();
            }

            // Hago un safe exit del manager
            ABMHuesped.exit();

        } catch (Exception e) {
            System.out.println("Que lindo mi sistema,se rompio mi sistema");
            throw e;
        } finally {
            System.out.println("Saliendo del sistema, bye bye...");

        }

    }

    public void alta() throws Exception {
        System.out.println("\nALTA DE HUESPED\n");
        Huesped huesped = new Huesped();
        System.out.println("Ingrese el nombre:");
        huesped.setNombre(Teclado.nextLine());
        System.out.println("Ingrese el DNI:");
        huesped.setDni(Teclado.nextInt());
        Teclado.nextLine();
        System.out.println("Ingrese la domicilio:");
        huesped.setDomicilio(Teclado.nextLine());

        System.out.println("Ingrese el Domicilio alternativo(OPCIONAL):");

        String domAlternativo = Teclado.nextLine();

        if (domAlternativo != null)
            huesped.setDomicilioAlternativo(domAlternativo);

        // Generamos una reserva.
        Reserva reserva = new Reserva();

        BigDecimal importeReserva = new BigDecimal(1000);
        reserva.setImporteReserva(importeReserva); // FORMA1
        reserva.setImporteTotal(new BigDecimal(3000)); // FORMA2
        reserva.setImportePagado(new BigDecimal(0));
        reserva.setFechaReserva(new Date()); // Fecha actual

        System.out.println("Ingrese la fecha de ingreso(dd/mm/yy)");

        Date fechaIngreso = null;
        Date fechaEgreso = null;

        DateFormat dFormat = new SimpleDateFormat("dd/MM/yy"); // el formato en el que se va a ingresar la fecha

        // Alternativa de leer la fecha con try catch
        try {
            fechaIngreso = dFormat.parse(Teclado.nextLine()); // Parsea lo que lea por teclado al formato "dd/mm/yy"

        } catch (Exception ex) {
            System.out.println("Ingreso una fecha invalida. ");
            System.out.println("Vuelva a empezar.");
            return;
        }

        // Alternativa de leer fecha a los golpes(puede tirar una excepcion)
        System.out.println("Ingrese la fecha de egreso(dd/mm/yy)");
        fechaEgreso = dFormat.parse(Teclado.nextLine());

        reserva.setFechaIngreso(fechaIngreso);
        reserva.setFechaEgreso(fechaEgreso); // por ahora 1 dia.
        reserva.setTipoEstadoId(10); // En mi caso, 10 significa pagado.
        reserva.setHuesped(huesped); // Esta es la relacion bidireccional

        // Actualizo todos los objeto
        ABMHuesped.create(huesped);

        /*
         * Si concateno el OBJETO directamente, me trae todo lo que este en el metodo
         * toString() mi recomendacion es NO usarlo para imprimir cosas en pantallas, si
         * no para loguear info Lo mejor es usar:
         * System.out.println("Huesped generada con exito.  " + huesped.getHuespedId);
         */

        System.out.println("Huesped generado/a con exito.  " + huesped);

    }

    public void baja() {
        System.out.println("\nBAJA DE HUESPED\n");
        System.out.println("Ingrese el nombre:");
        String nombre = Teclado.nextLine();
        System.out.println("Ingrese el ID de Huesped:");
        int id = Teclado.nextInt();
        Teclado.nextLine();
        Huesped huespedEncontrado = ABMHuesped.read(id);

        if (huespedEncontrado == null) {
            System.out.println("Huesped no encontrado.");

        } else {

            try {

                ABMHuesped.delete(huespedEncontrado);
                System.out
                        .println("El registro del huesped " + huespedEncontrado.getHuespedId() + " ha sido eliminado.");
            } catch (Exception e) {
                System.out.println("Ocurrio un error al eliminar una huesped. Error: " + e.getCause());
            }

        }
    }

    public void bajaPorDNI() {
        System.out.println("Ingrese el nombre:");
        String nombre = Teclado.nextLine();
        System.out.println("Ingrese el DNI de Huesped:");
        int dni = Teclado.nextInt();
        Huesped huespedEncontrado = ABMHuesped.readByDNI(dni);

        if (huespedEncontrado == null) {
            System.out.println("Huesped no encontrado.");

        } else {
            ABMHuesped.delete(huespedEncontrado);
            System.out.println("El registro del DNI " + huespedEncontrado.getDni() + " ha sido eliminado.");
        }
    }

    public void modifica() throws Exception {
        // System.out.println("Ingrese el nombre de la huesped a modificar:");
        // String n = Teclado.nextLine();
        System.out.println("\nMODIFICACIÓN DE HUESPED\n");
        System.out.println("Ingrese el ID deL huesped a modificar:");
        int id = Teclado.nextInt();
        Teclado.nextLine();
        Huesped huespedEncontrado = ABMHuesped.read(id);

        if (huespedEncontrado != null) {

            // RECOMENDACION NO USAR toString(), esto solo es a nivel educativo.
            System.out.println(huespedEncontrado.toString() + " seleccionado para modificacion.");

            System.out.println(
                    "Elija qué dato deL huesped desea modificar: \n1: nombre, \n2: DNI, \n3: domicilio, \n4: domicilio alternativo");
            int selecper = Teclado.nextInt();

            switch (selecper) {
                case 1:
                    System.out.println("Ingrese el nuevo nombre:");
                    Teclado.nextLine();
                    huespedEncontrado.setNombre(Teclado.nextLine());

                    break;
                case 2:
                    System.out.println("Ingrese el nuevo DNI:");
                    Teclado.nextLine();
                    huespedEncontrado.setDni(Teclado.nextInt());
                    Teclado.nextLine();

                    break;
                case 3:
                    System.out.println("Ingrese el nuevo domicilio:");
                    Teclado.nextLine();
                    huespedEncontrado.setDomicilio(Teclado.nextLine());

                    break;
                case 4:
                    System.out.println("Ingrese el nuevo domicilio alternativo:");
                    Teclado.nextLine();
                    huespedEncontrado.setDomicilioAlternativo(Teclado.nextLine());

                    break;

                default:
                    break;
            }

            // Teclado.nextLine();

            ABMHuesped.update(huespedEncontrado);

            System.out.println("El registro de " + huespedEncontrado.getNombre() + " ha sido modificado.");

        } else {
            System.out.println("Huesped no encontrado.");
        }

    }

    public void listar() {
        System.out.println("\nLISTA DE HUESPED\n");
        List<Huesped> todos = ABMHuesped.buscarTodos();
        for (Huesped c : todos) {
            mostrarHuesped(c);
        }
    }

    public void listarPorNombre() {
        System.out.println("\nLISTA POR NOMBRE DE HUESPED\n");
        System.out.println("Ingrese el nombre:");
        String nombre = Teclado.nextLine();

        List<Huesped> huespedes = ABMHuesped.buscarPor(nombre);
        for (Huesped huesped : huespedes) {
            mostrarHuesped(huesped);
        }
    }

    public void mostrarHuesped(Huesped huesped) {

        System.out.print("Id: " + huesped.getHuespedId() + " Nombre: " + huesped.getNombre() + " DNI: "
                + huesped.getDni() + " Domicilio: " + huesped.getDomicilio());

        if (huesped.getDomicilioAlternativo() != null)
            System.out.println(" Alternativo: " + huesped.getDomicilioAlternativo());
        else
            System.out.println();
    }

    // MÉTODOS DE RESERVA

    public void altaReserva() {
        System.out.println("\nALTA DE RESERVACIÓN\n");

        System.out.println("Introducir el DNI del huésped: ");
        int dni = Teclado.nextInt();
        Teclado.nextLine();
        Huesped huesped = ABMHuesped.readByDNI(dni); 

        if (huesped == null) {

            System.out.println("No existe");
            return;

        }

        Reserva reserva = new Reserva();
        reserva.setHuesped(huesped);
        reserva.setFechaReserva(new Date());

        System.out.println("Ingrese la fecha de ingreso(dd/mm/yy)");

        Date fechaIngreso = null; 
        Date fechaEgreso = null;

        DateFormat dFormat = new SimpleDateFormat("dd/MM/yy");
        try {
           // reserva.setFechaIngreso(dFormat.parse(Teclado.nextLine())); (Otra opción)

           fechaIngreso = dFormat.parse(Teclado.nextLine());
           reserva.setFechaIngreso(fechaEgreso);

        } catch (Exception ex) {
            System.out.println("Ingreso una fecha invalida. ");
            System.out.println("Vuelva a empezar.");
            return;
        }

        System.out.println("Ingrese la fecha de egreso(dd/mm/yy)");

        dFormat = new SimpleDateFormat("dd/MM/yy");
        try {
           // reserva.setFechaEgreso(dFormat.parse(Teclado.nextLine())); (Otra opción)

           fechaEgreso = dFormat.parse(Teclado.nextLine());
           reserva.setFechaEgreso(fechaEgreso);

        } catch (Exception ex) {
            System.out.println("Ingreso una fecha invalida. ");
            System.out.println("Vuelva a empezar.");
            return;
        }

        System.out.println("Ingrese número de habitación.");
        int habitacion = Teclado.nextInt();
        Teclado.nextLine();

        System.out.println("Introducir el importe de la reserva: ");
        BigDecimal importeReserva = Teclado.nextBigDecimal();
        Teclado.nextLine();

        System.out.println("Introducir el importe total: ");
       BigDecimal importeTotal = Teclado.nextBigDecimal();
        Teclado.nextLine();

        System.out.println("Introducir el importe pagado: ");
        BigDecimal importePagado = Teclado.nextBigDecimal();
        Teclado.nextLine();

        System.out.println("Ingrese el estado de pago: ");
        // otra forma   reserva.setTipoEstadoId(Teclado.nextInt());
        int tipoEstadoId = Teclado.nextInt();
        Teclado.nextLine();

        reserva.setFechaIngreso(fechaIngreso);
        reserva.setFechaEgreso(fechaEgreso);
        reserva.setHabitacion(habitacion);
        reserva.setImporteReserva(importeReserva);
        reserva.setImporteTotal(importeTotal);
        reserva.setImportePagado(importePagado);
        reserva.setTipoEstadoId(tipoEstadoId);

        ABMReserva.create(reserva);
        System.out.println("Reserva generada con exito.  " + reserva);
    }

    private void bajaReserva() {
        System.out.println("FUNCIONA BAJA");
    }

    private void modificarReserva() {
        System.out.println("FUNCIONA MODIFICA");
    }

    private void listarReservas() {
        System.out.println("FUNCIONA LISTA");
    }

    private void listarPorNombreHuesped() {
        System.out.println("FUNCIONA LISTA POR NOMBRE");
    }

    public static void printOpciones() {
        System.out.println("=======================================");
        System.out.println("Seleccione una de las siguientes opciones para continuar. \n");
        System.out.println("1. Menú de huespedes.");
        System.out.println("2. Menú de reservas. ");
        System.out.println("");
        System.out.println("=======================================");
    }

    public static void printOpcionHuesped() {
        System.out.println("\n MENÚ HUESPEDES \n");
        System.out.println(" Seleccione : ");
        System.out.println("1. Para agregar un huesped.");
        System.out.println("2. Para eliminar un huesped.");
        System.out.println("3. Para modificar un huesped.");
        System.out.println("4. Para ver listado de huespedes.");
        System.out.println("5. Para buscar un huesped por nombre especifico(SQL Injection)).");
        System.out.println("0. Para terminar.");
    }

    public static void printOpcionReserva() {

        System.out.println(" MENÚ RESERVAS \n");
        System.out.println(" Seleccione : ");
        System.out.println("1. Para agregar una reserva.");
        System.out.println("2. Para eliminar una reserva.");
        System.out.println("3. Para modificar una reserva.");
        System.out.println("4. Para ver listado de reservas.");
        System.out.println("5. Para ver el listado de reservas de un huesped por su nombre.");
        System.out.println("0. Para terminar.");
    }
}
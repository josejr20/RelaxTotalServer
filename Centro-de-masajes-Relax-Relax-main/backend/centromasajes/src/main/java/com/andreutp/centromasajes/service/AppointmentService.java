package com.andreutp.centromasajes.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.andreutp.centromasajes.dao.IAppointmentRepository;
import com.andreutp.centromasajes.dao.IInvoiceRepository;
import com.andreutp.centromasajes.dao.IPaymentRepository;
import com.andreutp.centromasajes.dao.IPlanRepository;
import com.andreutp.centromasajes.dao.IServiceRepository;
import com.andreutp.centromasajes.dao.IUserRepository;
import com.andreutp.centromasajes.dto.AppointmentRequest;
import com.andreutp.centromasajes.exception.BusinessException;
import com.andreutp.centromasajes.model.AppointmentModel;
import com.andreutp.centromasajes.model.PaymentModel;
import com.andreutp.centromasajes.model.ServiceModel;
import com.andreutp.centromasajes.model.UserModel;

@Service
public class AppointmentService {
    public static final String USER_NOT_FOUND = "Usuario no encontrado";
    public static final String WORKER_NOT_FOUND = "Trabajador no encontrado";
    public static final String SERVICE_NOT_FOUND = "Servicio no encontrado";
    private final IAppointmentRepository appointmentRepository;

    private final IUserRepository userRepository;

    private final IServiceRepository serviceRepository;

    private final IPaymentRepository paymentRepository;

    private final IInvoiceRepository invoiceRepository;

    private final IPlanRepository planRepository;

    public AppointmentService(IAppointmentRepository appointmentRepository,
            IUserRepository userRepository,
            IServiceRepository serviceRepository,
            IPlanRepository planRepository,
            IPaymentRepository paymentRepository,
            IInvoiceRepository invoiceRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.planRepository = planRepository;
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
    }

    // Crear cita (validando que el worker no tenga otra a la misma hora)
    public AppointmentModel createAppointment(AppointmentRequest request) {
        if (request == null) {
            throw new BusinessException("La solicitud no puede ser nula");
        }
        if (request.getUserId() == null) {
            throw new BusinessException("El id de usuario no puede ser nulo");
        }
        if (request.getWorkerId() == null) {
            throw new BusinessException("El id del trabajador no puede ser nulo");
        }
        if (request.getServiceId() == null) {
            throw new BusinessException("El id del servicio no puede ser nulo");
        }
        UserModel user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        UserModel worker = userRepository.findById(request.getWorkerId())
                .orElseThrow(() -> new BusinessException(WORKER_NOT_FOUND));

        ServiceModel service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new BusinessException(SERVICE_NOT_FOUND));

        AppointmentModel appointment = new AppointmentModel();

        // Validar disponibilidad antes de crear la cita
        if (appointmentRepository.existsByWorkerAndAppointmentStart(worker, request.getAppointmentStart())) {
            throw new com.andreutp.centromasajes.exception.BusinessException(
                    "El trabajador ya tiene una cita en ese horario");
        }

        appointment.setUser(user);
        appointment.setWorker(worker);
        appointment.setService(service);
        appointment.setAppointmentStart(request.getAppointmentStart());
        appointment.setNotes(request.getNotes());
        appointment.setStatus(AppointmentModel.Status.PENDING);

        // Calcular fin de cita automáticamente
        appointment.setAppointmentEnd(
                appointment.getAppointmentStart().plusMinutes(service.getDurationMin()));

        return appointmentRepository.save(appointment);
    }

    public List<AppointmentModel> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<AppointmentModel> getAppointmentsByUser(Long userId) {
        if (userId == null) {
            throw new BusinessException("El id de usuario no puede ser nulo");
        }
        UserModel user = getUserOrThrow(userId);
        return appointmentRepository.findByUser(user);
    }

    public List<AppointmentModel> getAppointmentsByWorker(Long workerId) {
        if (workerId == null) {
            throw new BusinessException("El id del trabajador no puede ser nulo");
        }
        UserModel worker = getWorkerOrThrow(workerId);
        return appointmentRepository.findByWorker(worker);
    }

    public AppointmentModel getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new com.andreutp.centromasajes.exception.BusinessException("Cita no encontrada"));
    }

    public AppointmentModel updateAppointmentStatus(Long id, String status) {
        if (id == null) {
            throw new BusinessException("El id de la cita no puede ser nulo");
        }
        if (status == null) {
            throw new BusinessException("El estado no puede ser nulo");
        }
        AppointmentModel appointment = getAppointmentById(id);
        AppointmentModel.Status parsed = parseStatus(status);
        appointment.setStatus(parsed);
        return appointmentRepository.save(appointment);
    }

    public AppointmentModel updateAppointment(Long id, AppointmentRequest request) {
        if (id == null) {
            throw new BusinessException("El id de la cita no puede ser nulo");
        }
        if (request == null) {
            throw new BusinessException("La solicitud no puede ser nula");
        }

        AppointmentModel existing = getAppointmentById(id);

        UserModel user = getUserOrThrow(request.getUserId());
        UserModel worker = getWorkerOrThrow(request.getWorkerId());
        ServiceModel service = getServiceOrThrow(request.getServiceId());

        existing.setUser(user);
        existing.setWorker(worker);
        existing.setService(service);
        existing.setAppointmentStart(request.getAppointmentStart());
        existing.setAppointmentEnd(request.getAppointmentStart().plusMinutes(service.getDurationMin()));
        existing.setNotes(request.getNotes());

        AppointmentModel.Status nuevoEstado = parseStatus(request.getStatus());
        existing.setStatus(nuevoEstado);

        refundPaymentsIfCancelled(existing);

        return appointmentRepository.save(existing);
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    // --- Helper methods to remove duplication and centralize validations ---
    private UserModel getUserOrThrow(Long id) {
        if (id == null) {
            throw new BusinessException("El id de usuario no puede ser nulo");
        }
        return userRepository.findById(id).orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
    }

    private UserModel getWorkerOrThrow(Long id) {
        if (id == null) {
            throw new BusinessException("El id del trabajador no puede ser nulo");
        }
        return userRepository.findById(id).orElseThrow(() -> new BusinessException(WORKER_NOT_FOUND));
    }

    private ServiceModel getServiceOrThrow(Long id) {
        if (id == null) {
            throw new BusinessException("El id del servicio no puede ser nulo");
        }
        return serviceRepository.findById(id).orElseThrow(() -> new BusinessException(SERVICE_NOT_FOUND));
    }

    private AppointmentModel.Status parseStatus(String status) {
        if (status == null) {
            throw new BusinessException("El estado no puede ser nulo");
        }
        try {
            return AppointmentModel.Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Estado de cita inválido: " + status);
        }
    }

    private void refundPaymentsIfCancelled(AppointmentModel appointment) {
        if (appointment == null) return;
        if (appointment.getStatus() == AppointmentModel.Status.CANCELLED) {
            List<PaymentModel> payments = paymentRepository.findByAppointment(appointment);
            for (PaymentModel p : payments) {
                p.setStatus(PaymentModel.Status.REFUNDED);
                paymentRepository.save(p);
            }
        }
    }
}

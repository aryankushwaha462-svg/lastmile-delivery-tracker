package com.delivery.tracker.service;

import com.delivery.tracker.dto.ChargeCalculationDto;
import com.delivery.tracker.dto.OrderRequestDto;
import com.delivery.tracker.model.*;
import com.delivery.tracker.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderTrackingRepository trackingRepository;
    private final AreaRepository areaRepository;
    private final AgentRepository agentRepository;
    private final RateCalculationService rateCalculationService;
    private final AutoAssignmentService autoAssignmentService;
    private final NotificationService notificationService;

    public OrderService(OrderRepository orderRepository,
                        OrderTrackingRepository trackingRepository,
                        AreaRepository areaRepository,
                        AgentRepository agentRepository,
                        RateCalculationService rateCalculationService,
                        AutoAssignmentService autoAssignmentService,
                        NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.trackingRepository = trackingRepository;
        this.areaRepository = areaRepository;
        this.agentRepository = agentRepository;
        this.rateCalculationService = rateCalculationService;
        this.autoAssignmentService = autoAssignmentService;
        this.notificationService = notificationService;
    }

    public List<Order> getFilteredOrders(OrderStatus status, Long zoneId, Long agentId) {
        return orderRepository.findWithFilters(status, zoneId, agentId);
    }

    public List<Order> getOrdersByCustomerEmail(String email) {
        return orderRepository.findByCustomerEmailIgnoreCase(email);
    }

    public List<Order> getOrdersByAgentEmail(String email) {
        return orderRepository.findByAssignedAgentEmailIgnoreCase(email);
    }

    public List<OrderTracking> getTrackingHistory(Long orderId, String requesterEmail, boolean isAdmin, boolean isAgent) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (!isAdmin) {
            boolean isOwner = order.getCustomerEmail() != null && order.getCustomerEmail().equalsIgnoreCase(requesterEmail);
            boolean isAssignedAgent = isAgent && order.getAssignedAgent() != null
                    && order.getAssignedAgent().getEmail().equalsIgnoreCase(requesterEmail);

            if (!isOwner && !isAssignedAgent) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only track your own orders.");
            }
        }
        return trackingRepository.findByOrderIdOrderByTimestampAsc(orderId);
    }

    @Transactional
    public Order createOrder(OrderRequestDto dto) {
        ChargeCalculationDto chargeDetails = rateCalculationService.calculateCharge(dto);

        Area pickupArea = areaRepository.findByPincode(dto.getPickupPincode())
                .orElseThrow(() -> new RuntimeException("Invalid pickup pincode: " + dto.getPickupPincode()));
        Area dropArea = areaRepository.findByPincode(dto.getDropPincode())
                .orElseThrow(() -> new RuntimeException("Invalid drop pincode: " + dto.getDropPincode()));

        Order order = Order.builder()
                .pickupAddress(dto.getPickupAddress())
                .pickupPincode(dto.getPickupPincode())
                .dropAddress(dto.getDropAddress())
                .dropPincode(dto.getDropPincode())
                .length(dto.getLength())
                .breadth(dto.getBreadth())
                .height(dto.getHeight())
                .actualWeight(dto.getActualWeight())
                .volumetricWeight(chargeDetails.getVolumetricWeight())
                .chargeableWeight(chargeDetails.getChargeableWeight())
                .orderType(dto.getOrderType().toUpperCase())
                .paymentType(dto.getPaymentType().toUpperCase())
                .baseCharge(chargeDetails.getBaseCharge())
                .codSurcharge(chargeDetails.getCodSurcharge())
                .totalCharge(chargeDetails.getTotalCharge())
                .pickupZone(pickupArea.getZone())
                .dropZone(dropArea.getZone())
                .status(OrderStatus.CREATED)
                .customerEmail(dto.getCustomerEmail().toLowerCase())
                .createdAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);
        logTrackingHistory(savedOrder, OrderStatus.CREATED, "SYSTEM", "Order created");
        notificationService.sendStatusNotification(savedOrder, OrderStatus.CREATED);

        autoAssignmentService.findNearestAvailableAgent(pickupArea.getZone()).ifPresent(agent -> {
            savedOrder.setAssignedAgent(agent);
            savedOrder.setStatus(OrderStatus.ASSIGNED);
            agent.setIsAvailable(false);
            orderRepository.save(savedOrder);
            logTrackingHistory(savedOrder, OrderStatus.ASSIGNED, "SYSTEM", "Auto-assigned agent: " + agent.getName());
            notificationService.sendStatusNotification(savedOrder, OrderStatus.ASSIGNED);
        });

        return savedOrder;
    }

    @Transactional
    public Order assignAgentManually(Long orderId, Long agentId, String actor) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        Agent newAgent = agentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found: " + agentId));

        if (order.getAssignedAgent() != null && !order.getAssignedAgent().getId().equals(newAgent.getId())) {
            order.getAssignedAgent().setIsAvailable(true);
        }

        order.setAssignedAgent(newAgent);
        newAgent.setIsAvailable(false);
        if (order.getStatus() == OrderStatus.CREATED || order.getStatus() == OrderStatus.RESCHEDULED) {
            order.setStatus(OrderStatus.ASSIGNED);
        }

        Order updated = orderRepository.save(order);
        logTrackingHistory(updated, updated.getStatus(), actor, "Manually assigned agent: " + newAgent.getName());
        notificationService.sendStatusNotification(updated, updated.getStatus());
        return updated;
    }

    @Transactional
    public Order triggerAutoAssignment(Long orderId, String actor) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        Agent agent = autoAssignmentService.findNearestAvailableAgent(order.getPickupZone())
                .orElseThrow(() -> new RuntimeException("No available agent in zone: " + order.getPickupZone().getName()));

        if (order.getAssignedAgent() != null) {
            order.getAssignedAgent().setIsAvailable(true);
        }

        order.setAssignedAgent(agent);
        agent.setIsAvailable(false);
        order.setStatus(OrderStatus.ASSIGNED);

        Order updated = orderRepository.save(order);
        logTrackingHistory(updated, OrderStatus.ASSIGNED, actor, "Admin triggered auto-assignment: " + agent.getName());
        notificationService.sendStatusNotification(updated, OrderStatus.ASSIGNED);
        return updated;
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus, String actor, String remarks) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setStatus(newStatus);

        if ((newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.FAILED)
                && order.getAssignedAgent() != null) {
            order.getAssignedAgent().setIsAvailable(true);
        }

        Order updatedOrder = orderRepository.save(order);
        String finalRemarks = (remarks != null && !remarks.isBlank()) ? remarks : "Status updated to " + newStatus.name();
        logTrackingHistory(updatedOrder, newStatus, actor, finalRemarks);
        notificationService.sendStatusNotification(updatedOrder, newStatus);

        return updatedOrder;
    }

    @Transactional
    public Order rescheduleFailedDelivery(Long orderId, LocalDateTime rescheduleDate, String actor, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (!isAdmin && (order.getCustomerEmail() == null || !order.getCustomerEmail().equalsIgnoreCase(actor))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only reschedule your own orders.");
        }

        if (order.getStatus() != OrderStatus.FAILED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only failed orders can be rescheduled");
        }

        order.setRescheduledDate(rescheduleDate);
        order.setStatus(OrderStatus.RESCHEDULED);

        StringBuilder note = new StringBuilder("Delivery rescheduled for " + rescheduleDate);
        autoAssignmentService.findNearestAvailableAgent(order.getPickupZone()).ifPresent(agent -> {
            order.setAssignedAgent(agent);
            agent.setIsAvailable(false);
            note.append(" | Reassigned to agent: ").append(agent.getName());
        });

        Order updatedOrder = orderRepository.save(order);
        logTrackingHistory(updatedOrder, OrderStatus.RESCHEDULED, actor, note.toString());
        notificationService.sendStatusNotification(updatedOrder, OrderStatus.RESCHEDULED);

        return updatedOrder;
    }

    private void logTrackingHistory(Order order, OrderStatus status, String updatedBy, String remarks) {
        OrderTracking tracking = OrderTracking.builder()
                .order(order)
                .status(status)
                .updatedBy(updatedBy)
                .remarks(remarks)
                .timestamp(LocalDateTime.now())
                .build();
        trackingRepository.save(tracking);
    }
}
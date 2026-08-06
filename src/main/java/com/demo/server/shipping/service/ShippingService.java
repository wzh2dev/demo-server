package com.demo.server.shipping.service;

import com.demo.server.shipping.domain.Shipping;
import com.demo.server.shipping.domain.ShippingStatus;
import com.demo.server.shipping.domain.Track;
import com.demo.server.shipping.dto.ShippingCreateRequest;
import com.demo.server.shipping.dto.TrackCreateRequest;
import com.demo.server.shipping.exception.ConflictException;
import com.demo.server.shipping.exception.NotFoundException;
import com.demo.server.shipping.exception.ValidationException;
import com.demo.server.shipping.repository.ShippingRepository;

import java.util.List;
import java.util.Objects;

/**
 * 物流领域服务。
 */
public class ShippingService {

    private static final int COMPANY_MAX_LEN = 64;
    private static final int LOCATION_MAX_LEN = 128;
    private static final int DESC_MAX_LEN = 256;

    private final ShippingRepository shippingRepository;

    public ShippingService(ShippingRepository shippingRepository) {
        this.shippingRepository = Objects.requireNonNull(shippingRepository);
    }

    /**
     * 创建物流单（发货）。
     */
    public Shipping create(ShippingCreateRequest req) {
        validateCreate(req);
        if (shippingRepository.findByOrder(req.getOrderId()) != null) {
            throw new ConflictException("shipping already exists for order: " + req.getOrderId());
        }
        Shipping shipping = new Shipping();
        shipping.setOrderId(req.getOrderId());
        shipping.setCompany(req.getCompany());
        shipping.setTrackingNo(req.getTrackingNo());
        shipping.setStatus(ShippingStatus.IN_TRANSIT);
        shipping.setShippedAt(System.currentTimeMillis());
        return shippingRepository.save(shipping);
    }

    public Shipping get(Long id) {
        return requireShipping(id);
    }

    public Shipping getByOrder(Long orderId) {
        Shipping shipping = shippingRepository.findByOrder(orderId);
        if (shipping == null) {
            throw new NotFoundException("shipping not found for order: " + orderId);
        }
        return shipping;
    }

    public List<Shipping> list(ShippingStatus status, int pageNumber, int pageSize) {
        if (pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize < 1 || pageSize > 100) {
            pageSize = 20;
        }
        return shippingRepository.findAll(status, pageNumber, pageSize);
    }

    /**
     * 上报物流轨迹。
     */
    public Track appendTrack(Long shippingId, TrackCreateRequest req) {
        Shipping shipping = requireShipping(shippingId);
        if (req == null) {
            throw new ValidationException("request is null");
        }
        if (req.getLocation() == null || req.getLocation().isEmpty()) {
            throw new ValidationException("location is empty");
        }
        if (req.getLocation().length() > LOCATION_MAX_LEN) {
            throw new ValidationException("location too long");
        }
        if (req.getDescription() == null || req.getDescription().isEmpty()) {
            throw new ValidationException("description is empty");
        }
        if (req.getDescription().length() > DESC_MAX_LEN) {
            throw new ValidationException("description too long");
        }
        if (shipping.getStatus() == ShippingStatus.DELIVERED) {
            throw new ConflictException("shipping already delivered");
        }
        Track track = new Track();
        track.setShippingId(shippingId);
        track.setTime(req.getTime());
        track.setLocation(req.getLocation());
        track.setDescription(req.getDescription());
        Track saved = shippingRepository.saveTrack(track);

        shipping.getTracks().add(saved);
        if (shipping.getStatus() == ShippingStatus.PENDING) {
            shipping.setStatus(ShippingStatus.IN_TRANSIT);
        }
        shippingRepository.save(shipping);
        return saved;
    }

    public List<Track> listTracks(Long shippingId) {
        requireShipping(shippingId);
        return shippingRepository.findTracksByShipping(shippingId);
    }

    /**
     * 标记已签收。
     */
    public Shipping markDelivered(Long id) {
        Shipping shipping = requireShipping(id);
        if (shipping.getStatus() == ShippingStatus.DELIVERED) {
            return shipping;
        }
        if (shipping.getStatus() != ShippingStatus.IN_TRANSIT) {
            throw new ConflictException("shipping not in transit: " + shipping.getStatus());
        }
        shipping.setStatus(ShippingStatus.DELIVERED);
        shipping.setDeliveredAt(System.currentTimeMillis());
        return shippingRepository.save(shipping);
    }

    /**
     * 标记异常。
     */
    public Shipping markException(Long id, String reason) {
        Shipping shipping = requireShipping(id);
        shipping.setStatus(ShippingStatus.EXCEPTION);
        Track track = new Track();
        track.setShippingId(id);
        track.setTime(System.currentTimeMillis());
        track.setLocation("SYSTEM");
        track.setDescription("EXCEPTION: " + reason);
        Track saved = shippingRepository.saveTrack(track);
        shipping.getTracks().add(saved);
        return shippingRepository.save(shipping);
    }

    public void delete(Long id) {
        if (!shippingRepository.deleteById(id)) {
            throw new NotFoundException("shipping", id);
        }
    }

    private Shipping requireShipping(Long id) {
        Shipping shipping = shippingRepository.findById(id);
        if (shipping == null) {
            throw new NotFoundException("shipping", id);
        }
        return shipping;
    }

    private void validateCreate(ShippingCreateRequest req) {
        if (req == null) {
            throw new ValidationException("request is null");
        }
        if (req.getOrderId() == null) {
            throw new ValidationException("orderId is null");
        }
        if (req.getCompany() == null || req.getCompany().isEmpty()) {
            throw new ValidationException("company is empty");
        }
        if (req.getCompany().length() > COMPANY_MAX_LEN) {
            throw new ValidationException("company too long");
        }
        if (req.getTrackingNo() == null || req.getTrackingNo().isEmpty()) {
            throw new ValidationException("trackingNo is empty");
        }
    }
}

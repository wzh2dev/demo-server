package com.demo.server.shipping.repository;

import com.demo.server.shipping.domain.Shipping;
import com.demo.server.shipping.domain.ShippingStatus;
import com.demo.server.shipping.domain.Track;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 物流仓储，内存实现。
 */
public class ShippingRepository {

    private final Map<Long, Shipping> store = new ConcurrentHashMap<>();
    private final Map<Long, Track> tracks = new ConcurrentHashMap<>();
    private final AtomicLong shippingIdSeq = new AtomicLong(0L);
    private final AtomicLong trackIdSeq = new AtomicLong(0L);

    public Shipping save(Shipping shipping) {
        if (shipping.getId() == null) {
            shipping.setId(shippingIdSeq.incrementAndGet());
            long now = System.currentTimeMillis();
            shipping.setCreatedAt(now);
            shipping.setUpdatedAt(now);
        } else {
            shipping.setUpdatedAt(System.currentTimeMillis());
        }
        store.put(shipping.getId(), shipping);
        return shipping;
    }

    public Shipping findById(Long id) {
        if (id == null) {
            return null;
        }
        return store.get(id);
    }

    public Shipping findByOrder(Long orderId) {
        if (orderId == null) {
            return null;
        }
        return store.values().stream()
                .filter(s -> orderId.equals(s.getOrderId()))
                .findFirst()
                .orElse(null);
    }

    public List<Shipping> findAll(ShippingStatus status, int pageNumber, int pageSize) {
        return store.values().stream()
                .filter(s -> status == null || status == s.getStatus())
                .sorted(Comparator.comparing(Shipping::getCreatedAt,
                        Comparator.nullsFirst(Comparator.reverseOrder())))
                .skip((long) (pageNumber - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    public long count(ShippingStatus status) {
        return store.values().stream()
                .filter(s -> status == null || status == s.getStatus())
                .count();
    }

    public Track saveTrack(Track track) {
        if (track.getId() == null) {
            track.setId(trackIdSeq.incrementAndGet());
            track.setCreatedAt(System.currentTimeMillis());
            if (track.getTime() == null) {
                track.setTime(track.getCreatedAt());
            }
        }
        tracks.put(track.getId(), track);
        return track;
    }

    public List<Track> findTracksByShipping(Long shippingId) {
        if (shippingId == null) {
            return java.util.Collections.emptyList();
        }
        return tracks.values().stream()
                .filter(t -> shippingId.equals(t.getShippingId()))
                .sorted(Comparator.comparing(Track::getTime,
                        Comparator.nullsFirst(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    public boolean deleteById(Long id) {
        boolean removed = store.remove(id) != null;
        List<Track> related = new ArrayList<>();
        for (Track t : tracks.values()) {
            if (id != null && id.equals(t.getShippingId())) {
                related.add(t);
            }
        }
        for (Track t : related) {
            tracks.remove(t.getId());
        }
        return removed;
    }
}

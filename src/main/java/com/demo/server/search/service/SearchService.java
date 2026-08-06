package com.demo.server.search.service;

import com.demo.server.order.domain.Order;
import com.demo.server.order.repository.OrderRepository;
import com.demo.server.product.domain.Product;
import com.demo.server.product.repository.ProductRepository;
import com.demo.server.search.domain.SearchItem;
import com.demo.server.search.domain.SearchType;
import com.demo.server.search.dto.SearchResult;
import com.demo.server.search.exception.ValidationException;
import com.demo.server.user.domain.User;
import com.demo.server.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * 搜索领域服务。
 *
 * <p>简单的内存全文检索实现，用于演示。</p>
 */
public class SearchService {

    private static final int QUERY_MAX_LEN = 128;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public SearchService(ProductRepository productRepository,
                         UserRepository userRepository,
                         OrderRepository orderRepository) {
        this.productRepository = Objects.requireNonNull(productRepository);
        this.userRepository = Objects.requireNonNull(userRepository);
        this.orderRepository = Objects.requireNonNull(orderRepository);
    }

    /**
     * 执行搜索。
     */
    public SearchResult search(String query, SearchType type, int pageNumber, int pageSize) {
        long start = System.currentTimeMillis();
        validate(query);
        if (type == null) {
            type = SearchType.ALL;
        }
        if (pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize < 1 || pageSize > 100) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        List<SearchItem> all = new ArrayList<>();
        if (type == SearchType.ALL || type == SearchType.PRODUCT) {
            all.addAll(searchProducts(query));
        }
        if (type == SearchType.ALL || type == SearchType.USER) {
            all.addAll(searchUsers(query));
        }
        if (type == SearchType.ALL || type == SearchType.ORDER) {
            all.addAll(searchOrders(query));
        }
        all.sort(Comparator.comparingDouble(SearchItem::getScore).reversed());
        long total = all.size();
        int from = (pageNumber - 1) * pageSize;
        int to = Math.min(from + pageSize, all.size());
        List<SearchItem> page = from < all.size()
                ? new ArrayList<>(all.subList(from, to))
                : new ArrayList<>();
        long took = System.currentTimeMillis() - start;
        return new SearchResult(query, total, page, pageNumber, pageSize, took);
    }

    /**
     * 搜索建议（前缀匹配）。
     */
    public List<String> suggest(String prefix, int limit) {
        if (prefix == null || prefix.isEmpty()) {
            return new ArrayList<>();
        }
        if (limit <= 0) {
            limit = 10;
        }
        String p = prefix.toLowerCase(Locale.ROOT);
        List<String> suggestions = new ArrayList<>();
        for (Product product : productRepository.findAll(null, null, null, null, 1, 1000)) {
            if (product.getName() != null
                    && product.getName().toLowerCase(Locale.ROOT).startsWith(p)) {
                suggestions.add(product.getName());
                if (suggestions.size() >= limit) {
                    break;
                }
            }
        }
        return suggestions;
    }

    private List<SearchItem> searchProducts(String query) {
        List<SearchItem> items = new ArrayList<>();
        String q = query.toLowerCase(Locale.ROOT);
        for (Product p : productRepository.findAll(null, null, null, null, 1, Integer.MAX_VALUE)) {
            double score = scoreText(p.getName(), p.getSubtitle(), q);
            if (score > 0) {
                String snippet = buildSnippet(p.getName() + " " + p.getSubtitle(), q);
                items.add(new SearchItem(SearchType.PRODUCT, p.getId(), p.getName(), snippet, score));
            }
        }
        return items;
    }

    private List<SearchItem> searchUsers(String query) {
        List<SearchItem> items = new ArrayList<>();
        String q = query.toLowerCase(Locale.ROOT);
        for (User u : userRepository.snapshot()) {
            double score = scoreText(u.getNickname(), u.getMobile(), q);
            if (score > 0) {
                String snippet = buildSnippet(u.getNickname() + " " + u.getMobile(), q);
                items.add(new SearchItem(SearchType.USER, u.getId(), u.getNickname(), snippet, score));
            }
        }
        return items;
    }

    private List<SearchItem> searchOrders(String query) {
        List<SearchItem> items = new ArrayList<>();
        String q = query.toLowerCase(Locale.ROOT);
        for (Order o : orderRepository.findAll(null, null, 1, Integer.MAX_VALUE)) {
            double score = scoreText(o.getOrderNo(), o.getRemark(), q);
            if (score > 0) {
                String snippet = buildSnippet(o.getOrderNo(), q);
                items.add(new SearchItem(SearchType.ORDER, o.getId(), o.getOrderNo(), snippet, score));
            }
        }
        return items;
    }

    private double scoreText(String primary, String secondary, String q) {
        if (primary == null && secondary == null) {
            return 0d;
        }
        double score = 0d;
        if (primary != null) {
            String p = primary.toLowerCase(Locale.ROOT);
            if (p.contains(q)) {
                score += 1.0;
                if (p.startsWith(q)) {
                    score += 0.5;
                }
            }
        }
        if (secondary != null) {
            String s = secondary.toLowerCase(Locale.ROOT);
            if (s.contains(q)) {
                score += 0.5;
            }
        }
        return score;
    }

    private String buildSnippet(String text, String q) {
        if (text == null) {
            return "";
        }
        int idx = text.toLowerCase(Locale.ROOT).indexOf(q.toLowerCase(Locale.ROOT));
        if (idx < 0) {
            return truncate(text, 80);
        }
        int from = Math.max(0, idx - 20);
        int to = Math.min(text.length(), idx + q.length() + 40);
        String snippet = text.substring(from, to);
        if (from > 0) {
            snippet = "..." + snippet;
        }
        if (to < text.length()) {
            snippet = snippet + "...";
        }
        return snippet;
    }

    private String truncate(String text, int max) {
        if (text.length() <= max) {
            return text;
        }
        return text.substring(0, max) + "...";
    }

    private void validate(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new ValidationException("query is empty");
        }
        if (query.length() > QUERY_MAX_LEN) {
            throw new ValidationException("query too long");
        }
    }
}
